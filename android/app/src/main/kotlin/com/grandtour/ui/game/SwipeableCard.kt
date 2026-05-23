package com.grandtour.ui.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.grandtour.data.model.CardDef
import com.grandtour.illustration.IllustrationRegistry
import com.grandtour.ui.theme.Crimson
import com.grandtour.ui.theme.Ink
import com.grandtour.ui.theme.InkLight
import com.grandtour.ui.theme.Parchment
import com.grandtour.ui.theme.Prussian
import com.grandtour.ui.theme.Sepia
import com.grandtour.ui.theme.SepiaLight
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Sepia color matrix applied to illustrations so engravings (color or
 * grayscale) blend with the parchment-and-ink theme. Classic conversion
 * weights — same matrix you'd see in any vintage photo filter.
 */
private val SepiaFilter: ColorFilter = ColorFilter.colorMatrix(
    ColorMatrix(floatArrayOf(
        0.393f, 0.769f, 0.189f, 0f, 0f,
        0.349f, 0.686f, 0.168f, 0f, 0f,
        0.272f, 0.534f, 0.131f, 0f, 0f,
        0f,     0f,     0f,     1f, 0f,
    ))
)

/**
 * Swipe-driven card. Matches the web semantics at index.html:1763–1824:
 * - Commit threshold 80dp horizontal travel
 * - Card rotation = (offset * 0.05) degrees, clamped to ±15°
 * - Tap on the outer 35% / 65% horizontal zones commits left/right
 * - Snap-back animation on release if under threshold
 */
@Composable
fun SwipeableCard(
    card: CardDef,
    outcomeText: String?,
    onSwipe: (SwipeDirection) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val screenWidthPx = with(density) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val threshold = with(density) { 80.dp.toPx() }
    val offsetX = remember(card.id) { Animatable(0f) }
    val scope = rememberCoroutineScope()

    // Card entry animation: fade in from slight scale-down.
    LaunchedEffect(card.id) {
        // Fresh card resets offset (Animatable is keyed on card.id anyway).
        offsetX.snapTo(0f)
    }

    val rotationDeg = (offsetX.value * 0.05f).coerceIn(-15f, 15f)
    val progress = (offsetX.value / threshold).coerceIn(-1f, 1f)

    Box(modifier = modifier) {
        // The card itself.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .graphicsLayer {
                    translationX = offsetX.value
                    rotationZ = rotationDeg
                }
                .shadow(6.dp, RoundedCornerShape(8.dp))
                .background(Parchment, RoundedCornerShape(8.dp))
                .border(3.dp, Sepia, RoundedCornerShape(8.dp))
                .pointerInput(card.id, outcomeText) {
                    if (outcomeText != null) return@pointerInput  // locked during outcome
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                val current = offsetX.value
                                if (abs(current) > threshold) {
                                    val target = if (current < 0) -screenWidthPx * 1.5f else screenWidthPx * 1.5f
                                    offsetX.animateTo(target, tween(durationMillis = 300))
                                    onSwipe(if (current < 0) SwipeDirection.LEFT else SwipeDirection.RIGHT)
                                } else {
                                    offsetX.animateTo(
                                        0f,
                                        spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                                    )
                                }
                            }
                        },
                        onDragCancel = {
                            scope.launch {
                                offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            scope.launch { offsetX.snapTo(offsetX.value + dragAmount) }
                        },
                    )
                }
                .pointerInput(card.id, outcomeText) {
                    if (outcomeText != null) return@pointerInput
                    detectTapGestures { tapOffset ->
                        // Outer 35% / 65% zones (matches index.html:1819-1822).
                        val width = size.width.toFloat()
                        when {
                            tapOffset.x < width * 0.35f -> commit(scope, offsetX, screenWidthPx, SwipeDirection.LEFT, onSwipe)
                            tapOffset.x > width * 0.65f -> commit(scope, offsetX, screenWidthPx, SwipeDirection.RIGHT, onSwipe)
                            else -> Unit
                        }
                    }
                }
        ) {
            if (outcomeText != null) {
                OutcomeContent(card = card, outcomeText = outcomeText)
            } else {
                CardContent(card = card)
            }
        }

        // Indicators on left/right edges.
        ChoiceIndicator(
            text = card.leftChoice,
            isLeft = true,
            alpha = if (progress < 0) -progress else 0f,
            color = Crimson,
        )
        ChoiceIndicator(
            text = card.rightChoice,
            isLeft = false,
            alpha = if (progress > 0) progress else 0f,
            color = Prussian,
        )
    }
}

private fun commit(
    scope: kotlinx.coroutines.CoroutineScope,
    offsetX: Animatable<Float, *>,
    screenWidthPx: Float,
    direction: SwipeDirection,
    onSwipe: (SwipeDirection) -> Unit,
) {
    scope.launch {
        val target = if (direction == SwipeDirection.LEFT) -screenWidthPx * 1.5f else screenWidthPx * 1.5f
        offsetX.animateTo(target, tween(durationMillis = 300))
        onSwipe(direction)
    }
}

@Composable
private fun CardContent(card: CardDef) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Week ${card.week}".uppercase(),
                style = MaterialTheme.typography.headlineSmall,
                color = SepiaLight,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = card.character,
                style = MaterialTheme.typography.headlineMedium,
                color = Sepia,
                textAlign = TextAlign.Center,
            )
        }
        // Illustration
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(SepiaLight.copy(alpha = 0.06f)),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(IllustrationRegistry.resOf(card.illustration)),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                colorFilter = SepiaFilter,
            )
        }
        // Body — dialogue
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .height(100.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = card.dialogue,
                style = MaterialTheme.typography.bodyLarge,
                color = Ink,
                textAlign = TextAlign.Center,
            )
        }
        // Footer — choice hints
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "← ${card.leftChoice}",
                style = MaterialTheme.typography.bodySmall,
                color = InkLight,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start,
            )
            Spacer(Modifier.size(12.dp))
            Text(
                text = "${card.rightChoice} →",
                style = MaterialTheme.typography.bodySmall,
                color = InkLight,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End,
            )
        }
    }
}

@Composable
private fun OutcomeContent(card: CardDef, outcomeText: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = card.character,
            style = MaterialTheme.typography.headlineSmall,
            color = SepiaLight,
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = outcomeText,
            style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
            color = Ink,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun BoxScope.ChoiceIndicator(
    text: String,
    isLeft: Boolean,
    alpha: Float,
    color: Color,
) {
    Box(
        modifier = Modifier
            .align(if (isLeft) Alignment.CenterStart else Alignment.CenterEnd)
            .padding(horizontal = 4.dp)
            .alpha(alpha)
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
            .border(1.dp, color, RoundedCornerShape(4.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            textAlign = TextAlign.Center,
        )
    }
}
