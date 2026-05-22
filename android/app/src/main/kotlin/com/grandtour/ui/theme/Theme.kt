package com.grandtour.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val GrandTourColors = lightColorScheme(
    primary = Sepia,
    onPrimary = Parchment,
    primaryContainer = SepiaLight,
    onPrimaryContainer = Parchment,
    secondary = Gold,
    onSecondary = Ink,
    tertiary = Crimson,
    onTertiary = Parchment,
    background = Parchment,
    onBackground = Ink,
    surface = ParchmentDark,
    onSurface = Ink,
    surfaceVariant = Parchment,
    onSurfaceVariant = InkLight,
    error = Crimson,
    onError = Parchment,
    outline = SepiaLight,
)

@Composable
fun GrandTourTheme(content: @Composable () -> Unit) {
    // Light scheme only — the parchment aesthetic has no dark counterpart.
    MaterialTheme(
        colorScheme = GrandTourColors,
        typography = GrandTourTypography,
        content = content,
    )
}
