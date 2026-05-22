package com.grandtour.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Font families. The web build uses Cinzel (display), Cormorant Garamond
 * (body), and IM Fell English (italics/flavor). Drop matching .ttf files
 * into `res/font/` and switch these to bundled fonts (Font(R.font.cinzel_regular, ...)).
 *
 * For now we fall back to system serif so the app builds without the .ttf
 * assets — the visual will be less period-accurate but functionally correct.
 */
val Cinzel: FontFamily = FontFamily.Serif
val Cormorant: FontFamily = FontFamily.Serif
val IMFell: FontFamily = FontFamily.Serif

val GrandTourTypography = Typography(
    // Title screen wordmark
    displayLarge = TextStyle(
        fontFamily = Cinzel,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        letterSpacing = 4.sp,
    ),
    displayMedium = TextStyle(
        fontFamily = Cinzel,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        letterSpacing = 3.sp,
    ),
    // Section headers, card character names
    headlineMedium = TextStyle(
        fontFamily = Cinzel,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        letterSpacing = 1.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = Cinzel,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        letterSpacing = 3.sp,
    ),
    // Card dialogue
    bodyLarge = TextStyle(
        fontFamily = IMFell,
        fontStyle = FontStyle.Italic,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = Cormorant,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    // Choice options, control hints
    bodySmall = TextStyle(
        fontFamily = Cormorant,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    // Stat labels, week counter
    labelSmall = TextStyle(
        fontFamily = Cinzel,
        fontWeight = FontWeight.SemiBold,
        fontSize = 8.sp,
        letterSpacing = 1.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = Cinzel,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
    ),
)
