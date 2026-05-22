package com.grandtour.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grandtour.audio.AudioEngine
import com.grandtour.audio.SoundId
import com.grandtour.data.model.CardDef
import com.grandtour.data.model.ChapterDef
import com.grandtour.data.model.CharacterPresets
import com.grandtour.data.model.DefaultGameOverMessages
import com.grandtour.data.model.EndCondition
import com.grandtour.data.model.GameState
import com.grandtour.data.repo.ChapterRepository
import com.grandtour.data.repo.SaveRepository
import com.grandtour.domain.GameEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameSessionViewModel @Inject constructor(
    private val chapterRepo: ChapterRepository,
    private val saveRepo: SaveRepository,
    private val engine: GameEngine,
    private val audio: AudioEngine,
) : ViewModel() {

    private val _state = MutableStateFlow<GameUiState>(GameUiState.Loading)
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val _events = Channel<GameEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var chapter: ChapterDef? = null
    private var gameState: GameState? = null
    private var initialized = false

    fun start(chapterId: String, characterId: String, resume: Boolean) {
        if (initialized) return
        initialized = true
        viewModelScope.launch {
            val soundEnabled = saveRepo.soundEnabledFlow.first()
            audio.enabled = soundEnabled

            val loadedChapter = chapterRepo.byId(chapterId) ?: return@launch
            chapter = loadedChapter

            val existing = if (resume) saveRepo.loadActiveGame() else null
            val state = if (existing != null && existing.chapterId == chapterId) {
                existing
            } else {
                val preset = CharacterPresets.byId(characterId)
                GameState(
                    chapterId = chapterId,
                    characterId = characterId,
                    stats = preset.startingStats,
                )
            }
            gameState = state
            saveRepo.saveActiveGame(state)
            audio.startMusic()
            advance(soundEnabled = soundEnabled)
        }
    }

    fun applyChoice(direction: SwipeDirection) {
        val current = _state.value as? GameUiState.Playing ?: return
        val state = gameState ?: return
        val chapter = chapter ?: return

        val card = current.card
        val effects = if (direction == SwipeDirection.LEFT) card.leftEffects else card.rightEffects
        val item = if (direction == SwipeDirection.LEFT) card.leftItem else card.rightItem
        val outcomeText = if (direction == SwipeDirection.LEFT) card.leftText else card.rightText

        val outcome = engine.applyEffects(state.stats, state.cabinet, effects, item)

        val nextState = state.copy(
            stats = outcome.newStats,
            cabinet = outcome.newCabinet,
            usedCards = state.usedCards + card.id,
            week = (state.week + 1).coerceAtMost(chapter.maxWeeks + 1),
        )
        gameState = nextState

        audio.play(if (direction == SwipeDirection.LEFT) SoundId.SwipeLeft else SoundId.SwipeRight)

        viewModelScope.launch {
            // Show outcome text briefly, then advance.
            _state.value = current.copy(
                stats = outcome.newStats,
                cabinet = outcome.newCabinet,
                phase = GameUiState.Phase.Outcome,
                outcomeText = outcomeText,
            )
            saveRepo.saveActiveGame(nextState)
            kotlinx.coroutines.delay(OUTCOME_DISPLAY_MS)
            advance(soundEnabled = current.soundEnabled)
        }
    }

    fun toggleSound() {
        val current = _state.value as? GameUiState.Playing ?: return
        val newEnabled = !current.soundEnabled
        audio.enabled = newEnabled
        if (newEnabled) audio.startMusic() else audio.stopMusic()
        _state.value = current.copy(soundEnabled = newEnabled)
        viewModelScope.launch { saveRepo.setSoundEnabled(newEnabled) }
    }

    private suspend fun advance(soundEnabled: Boolean) {
        val state = gameState ?: return
        val chapter = chapter ?: return

        engine.checkEndCondition(state.stats)?.let { reason ->
            audio.play(SoundId.GameOver)
            saveRepo.clearActiveGame()
            _state.value = GameUiState.Finished
            _events.send(
                GameEvent.GameOver(
                    reason = chapter.gameOverMessages[reason.key]
                        ?: DefaultGameOverMessages.text[reason].orEmpty(),
                    weeksSurvived = state.week - 1,
                    chapterTitle = chapter.title,
                    cabinet = state.cabinet,
                )
            )
            return
        }

        if (state.week > chapter.maxWeeks) {
            audio.play(SoundId.Victory)
            saveRepo.markChapterCompleted(chapter.id)
            saveRepo.clearActiveGame()
            _state.value = GameUiState.Finished
            _events.send(GameEvent.ChapterComplete(chapter.id))
            return
        }

        val selection = engine.pickNextCard(chapter, state.week, state.usedCards.toSet())
        if (selection == null) {
            audio.play(SoundId.Victory)
            saveRepo.markChapterCompleted(chapter.id)
            saveRepo.clearActiveGame()
            _state.value = GameUiState.Finished
            _events.send(GameEvent.ChapterComplete(chapter.id))
            return
        }

        // The engine may have bumped the week internally (when the pool was
        // empty). Mirror that into the saved game state.
        val syncedState = state.copy(week = selection.week)
        gameState = syncedState
        saveRepo.saveActiveGame(syncedState)

        _state.value = GameUiState.Playing(
            chapterId = chapter.id,
            chapterTitle = chapter.title,
            week = selection.week,
            maxWeeks = chapter.maxWeeks,
            stats = syncedState.stats,
            cabinet = syncedState.cabinet,
            card = selection.card,
            soundEnabled = soundEnabled,
        )
    }

    override fun onCleared() {
        audio.stopMusic()
        super.onCleared()
    }

    companion object {
        private const val OUTCOME_DISPLAY_MS = 1400L
    }
}

enum class SwipeDirection { LEFT, RIGHT }
