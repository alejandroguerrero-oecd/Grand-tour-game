package com.grandtour.ui.character

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.grandtour.R
import com.grandtour.data.model.CharacterPreset
import com.grandtour.data.model.CharacterPresets
import com.grandtour.data.model.Stat
import com.grandtour.data.model.StatBlock
import com.grandtour.ui.theme.InkLight
import com.grandtour.ui.theme.Parchment
import com.grandtour.ui.theme.ParchmentDark
import com.grandtour.ui.theme.Sepia
import com.grandtour.ui.theme.SepiaLight
import com.grandtour.ui.theme.StatHealth
import com.grandtour.ui.theme.StatKnowledge
import com.grandtour.ui.theme.StatPurse
import com.grandtour.ui.theme.StatReputation

@Composable
fun CharacterSelectScreen(
    onPick: (String) -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Parchment)
            .padding(horizontal = 30.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.character_select_title),
            style = MaterialTheme.typography.displayMedium,
            color = Sepia,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        CharacterPresets.all.forEach { preset ->
            CharacterCard(preset, onClick = { onPick(preset.id) })
            Spacer(Modifier.height(12.dp))
        }
        Spacer(Modifier.height(20.dp))
        TextButton(onClick = onBack) {
            Text("← Back", color = SepiaLight, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun CharacterCard(preset: CharacterPreset, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(ParchmentDark, RoundedCornerShape(4.dp))
            .border(2.dp, SepiaLight, RoundedCornerShape(4.dp))
            .padding(16.dp),
    ) {
        Text(
            text = stringResource(preset.nameRes),
            style = MaterialTheme.typography.headlineMedium,
            color = Sepia,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(preset.descriptionRes),
            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
            color = InkLight,
        )
        Spacer(Modifier.height(12.dp))
        StatPreview(preset.startingStats)
    }
}

@Composable
private fun StatPreview(stats: StatBlock) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        StatChip(stringResource(R.string.stat_purse),     stats[Stat.PURSE],     StatPurse)
        StatChip(stringResource(R.string.stat_reputation), stats[Stat.REPUTATION], StatReputation)
        StatChip(stringResource(R.string.stat_health),    stats[Stat.HEALTH],    StatHealth)
        StatChip(stringResource(R.string.stat_knowledge), stats[Stat.KNOWLEDGE], StatKnowledge)
    }
}

@Composable
private fun StatChip(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = InkLight)
        Spacer(Modifier.height(2.dp))
        Text(value.toString(), style = MaterialTheme.typography.labelMedium, color = color)
    }
}
