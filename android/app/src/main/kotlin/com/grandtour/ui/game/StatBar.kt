package com.grandtour.ui.game

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.grandtour.ui.theme.Ink
import com.grandtour.ui.theme.InkLight

@Composable
fun StatBar(
    label: String,
    icon: String,
    value: Int,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val warning = value <= 15 || value >= 85
    val animatedWidth by animateFloatAsState(
        targetValue = value / 100f,
        animationSpec = tween(durationMillis = 500),
        label = "stat-width",
    )
    val transition = rememberInfiniteTransition(label = "warning-pulse")
    val pulseAlpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = if (warning) 0.5f else 1f,
        animationSpec = infiniteRepeatable(tween(500)),
        label = "alpha",
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(icon, style = MaterialTheme.typography.headlineMedium)
        Text(label, style = MaterialTheme.typography.labelSmall, color = InkLight)
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.Black.copy(alpha = 0.15f))
                .border(0.5.dp, Color.Black.copy(alpha = 0.1f), RoundedCornerShape(3.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedWidth)
                    .fillMaxHeight()
                    .alpha(if (warning) pulseAlpha else 1f)
                    .background(color, RoundedCornerShape(2.dp))
            )
        }
        Spacer(Modifier.height(2.dp))
        Text(value.toString(), style = MaterialTheme.typography.labelMedium, color = Ink)
    }
}
