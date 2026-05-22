package com.grandtour.ui.chaptercomplete

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grandtour.R
import com.grandtour.ui.theme.Ink
import com.grandtour.ui.theme.InkLight
import com.grandtour.ui.theme.Parchment
import com.grandtour.ui.theme.ParchmentDark
import com.grandtour.ui.theme.Sepia
import com.grandtour.ui.theme.SepiaLight

@Composable
fun ChapterCompleteScreen(
    chapterId: String,
    onContinue: () -> Unit,
    viewModel: ChapterCompleteViewModel = hiltViewModel(),
) {
    val data by viewModel.data(chapterId).collectAsState(initial = null)
    val payload = data ?: return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Parchment, ParchmentDark)))
            .padding(30.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text("✦", style = MaterialTheme.typography.displayLarge, color = Sepia)
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.chapter_complete_title_fmt, payload.chapterTitle),
                style = MaterialTheme.typography.displayMedium,
                color = Sepia,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = payload.completionText,
                style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                color = Ink,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(20.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.chapter_complete_acquisitions),
                    style = MaterialTheme.typography.labelMedium,
                    color = SepiaLight,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = if (payload.cabinet.isEmpty()) stringResource(R.string.cabinet_empty)
                        else payload.cabinet.joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                    color = Ink,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(
                    R.string.chapter_complete_stats_fmt,
                    payload.purse, payload.reputation, payload.health, payload.knowledge,
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = InkLight,
            )
            Spacer(Modifier.height(28.dp))
            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(containerColor = Sepia, contentColor = Parchment),
                shape = RoundedCornerShape(4.dp),
            ) {
                Text(
                    text = stringResource(R.string.chapter_complete_done),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp),
                )
            }
        }
    }
}
