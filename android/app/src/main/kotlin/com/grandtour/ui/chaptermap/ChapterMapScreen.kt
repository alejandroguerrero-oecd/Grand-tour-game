package com.grandtour.ui.chaptermap

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grandtour.R
import com.grandtour.ui.theme.InkLight
import com.grandtour.ui.theme.Parchment
import com.grandtour.ui.theme.ParchmentDark
import com.grandtour.ui.theme.Sepia
import com.grandtour.ui.theme.SepiaLight

@Composable
fun ChapterMapScreen(
    characterId: String,
    onPick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: ChapterMapViewModel = hiltViewModel(),
) {
    val chapters by viewModel.chapters.collectAsState()
    val completed by viewModel.completedChapters.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Parchment)
            .padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.chapter_map_title),
            style = MaterialTheme.typography.displayMedium,
            color = Sepia,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(chapters, key = { it.id }) { chapter ->
                val unlocked = chapter.previousChapterId == null ||
                    chapter.previousChapterId in completed
                ChapterRow(
                    ordinal = chapter.ordinal,
                    title = chapter.title,
                    unlocked = unlocked,
                    completed = chapter.id in completed,
                    onClick = { if (unlocked) onPick(chapter.id) },
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        TextButton(onClick = onBack) {
            Text("← Back", color = SepiaLight, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ChapterRow(
    ordinal: Int,
    title: String,
    unlocked: Boolean,
    completed: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = unlocked, onClick = onClick)
            .background(if (unlocked) ParchmentDark else Parchment, RoundedCornerShape(4.dp))
            .border(2.dp, if (unlocked) SepiaLight else InkLight.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = romanNumeral(ordinal),
            style = MaterialTheme.typography.headlineMedium,
            color = if (unlocked) Sepia else InkLight.copy(alpha = 0.4f),
            modifier = Modifier.padding(end = 16.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = if (unlocked) Sepia else InkLight.copy(alpha = 0.4f),
            )
            if (!unlocked) {
                Text(
                    text = stringResource(R.string.chapter_locked),
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                    color = InkLight.copy(alpha = 0.6f),
                )
            } else if (completed) {
                Text(
                    text = "✦ Completed",
                    style = MaterialTheme.typography.bodySmall,
                    color = SepiaLight,
                )
            }
        }
    }
}

private fun romanNumeral(n: Int): String = when (n) {
    1 -> "I"
    2 -> "II"
    3 -> "III"
    4 -> "IV"
    5 -> "V"
    6 -> "VI"
    7 -> "VII"
    8 -> "VIII"
    9 -> "IX"
    10 -> "X"
    else -> n.toString()
}
