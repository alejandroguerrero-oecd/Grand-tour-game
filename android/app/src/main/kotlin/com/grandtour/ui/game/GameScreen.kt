package com.grandtour.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grandtour.R
import com.grandtour.data.model.Stat
import com.grandtour.ui.theme.InkLight
import com.grandtour.ui.theme.Parchment
import com.grandtour.ui.theme.ParchmentDark
import com.grandtour.ui.theme.Sepia
import com.grandtour.ui.theme.SepiaLight
import com.grandtour.ui.theme.StatHealth
import com.grandtour.ui.theme.StatKnowledge
import com.grandtour.ui.theme.StatPurse
import com.grandtour.ui.theme.StatReputation
import kotlinx.coroutines.flow.collectLatest

@Composable
fun GameScreen(
    chapterId: String,
    characterId: String,
    resume: Boolean,
    onGameOver: (reason: String, weeksSurvived: Int, chapterTitle: String, cabinet: List<String>) -> Unit,
    onChapterComplete: (chapterId: String) -> Unit,
    viewModel: GameSessionViewModel = hiltViewModel(),
) {
    LaunchedEffect(chapterId, characterId, resume) {
        viewModel.start(chapterId, characterId, resume)
    }
    LaunchedEffect(viewModel) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is GameEvent.GameOver -> onGameOver(
                    event.reason, event.weeksSurvived, event.chapterTitle, event.cabinet
                )
                is GameEvent.ChapterComplete -> onChapterComplete(event.chapterId)
            }
        }
    }

    val state by viewModel.state.collectAsState()
    when (val s = state) {
        is GameUiState.Loading, GameUiState.Finished -> LoadingIndicator()
        is GameUiState.Playing -> PlayingContent(
            state = s,
            onSwipe = viewModel::applyChoice,
            onToggleSound = viewModel::toggleSound,
        )
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize().background(Parchment),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = Sepia)
    }
}

@Composable
private fun PlayingContent(
    state: GameUiState.Playing,
    onSwipe: (SwipeDirection) -> Unit,
    onToggleSound: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().background(Parchment)) {
        // Header
        Box(modifier = Modifier.fillMaxWidth().background(ParchmentDark).padding(12.dp)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "✦ THE GRAND TOUR ✦",
                    style = MaterialTheme.typography.labelSmall,
                    color = SepiaLight,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = state.chapterTitle.uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Sepia,
                )
            }
            IconButton(
                onClick = onToggleSound,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(ParchmentDark),
            ) {
                Text(
                    text = if (state.soundEnabled) "♪" else "✕",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Sepia,
                )
            }
        }

        // Stats panel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ParchmentDark)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatBar(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.stat_purse),
                icon = "✦",
                value = state.stats[Stat.PURSE],
                color = StatPurse,
            )
            StatBar(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.stat_reputation),
                icon = "❦",
                value = state.stats[Stat.REPUTATION],
                color = StatReputation,
            )
            StatBar(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.stat_health),
                icon = "✚",
                value = state.stats[Stat.HEALTH],
                color = StatHealth,
            )
            StatBar(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.stat_knowledge),
                icon = "✑",
                value = state.stats[Stat.KNOWLEDGE],
                color = StatKnowledge,
            )
        }

        // Card area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center,
        ) {
            SwipeableCard(
                card = state.card,
                outcomeText = if (state.phase == GameUiState.Phase.Outcome) state.outcomeText else null,
                onSwipe = onSwipe,
            )
        }

        // Controls / week display
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ParchmentDark)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.control_hint),
                style = MaterialTheme.typography.bodySmall,
                color = InkLight,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(
                    R.string.week_display,
                    state.week,
                    state.maxWeeks,
                    state.chapterTitle,
                ),
                style = MaterialTheme.typography.labelMedium,
                color = Sepia,
            )
        }
    }
}
