package com.grandtour.ui.title

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grandtour.R
import com.grandtour.data.model.GameState
import com.grandtour.ui.theme.Ink
import com.grandtour.ui.theme.InkLight
import com.grandtour.ui.theme.Parchment
import com.grandtour.ui.theme.ParchmentDark
import com.grandtour.ui.theme.Sepia

@Composable
fun TitleScreen(
    onBegin: () -> Unit,
    onContinue: (GameState) -> Unit,
    viewModel: TitleViewModel = hiltViewModel(),
) {
    val activeGame by viewModel.activeGame.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Parchment, ParchmentDark))
            )
            .padding(horizontal = 30.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Ornamental crest placeholder.
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .shadow(2.dp, RoundedCornerShape(60.dp))
                    .background(ParchmentDark, RoundedCornerShape(60.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "✦",
                    style = MaterialTheme.typography.displayLarge,
                    color = Sepia,
                )
            }
            Spacer(Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.title_main),
                style = MaterialTheme.typography.displayLarge,
                color = Sepia,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.title_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = InkLight,
            )
            Spacer(Modifier.height(28.dp))
            Text(
                text = stringResource(R.string.title_description),
                style = MaterialTheme.typography.bodyMedium,
                color = Ink,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(36.dp))
            Button(
                onClick = onBegin,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Sepia,
                    contentColor = Parchment,
                ),
                shape = RoundedCornerShape(4.dp),
            ) {
                Text(
                    text = stringResource(R.string.title_begin),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp),
                )
            }
            activeGame?.let { game ->
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { onContinue(game) },
                    shape = RoundedCornerShape(4.dp),
                ) {
                    Text(
                        text = stringResource(R.string.title_continue),
                        style = MaterialTheme.typography.labelMedium,
                        color = Sepia,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
                    )
                }
            }
        }
    }
}
