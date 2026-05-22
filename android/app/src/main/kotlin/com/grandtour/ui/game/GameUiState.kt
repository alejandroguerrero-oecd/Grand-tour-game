package com.grandtour.ui.game

import com.grandtour.data.model.CardDef
import com.grandtour.data.model.StatBlock

sealed interface GameUiState {
    data object Loading : GameUiState

    data class Playing(
        val chapterId: String,
        val chapterTitle: String,
        val week: Int,
        val maxWeeks: Int,
        val stats: StatBlock,
        val cabinet: List<String>,
        val card: CardDef,
        val phase: Phase = Phase.Card,
        val outcomeText: String = "",
        val soundEnabled: Boolean = true,
    ) : GameUiState

    data object Finished : GameUiState

    enum class Phase { Card, Outcome }
}

sealed interface GameEvent {
    data class GameOver(
        val reason: String,
        val weeksSurvived: Int,
        val chapterTitle: String,
        val cabinet: List<String>,
    ) : GameEvent

    data class ChapterComplete(val chapterId: String) : GameEvent
}
