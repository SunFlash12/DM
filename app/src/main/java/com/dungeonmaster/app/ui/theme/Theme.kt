package com.dungeonmaster.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Fantasy color palette
val PurplePrimary = Color(0xFF6B4E9B)
val PurplePrimaryDark = Color(0xFF4A3570)
val PurplePrimaryLight = Color(0xFF9B7BC7)

val GoldSecondary = Color(0xFFC9A032)
val GoldSecondaryDark = Color(0xFF9A7A20)
val GoldSecondaryLight = Color(0xFFE8C85A)

val BackgroundDark = Color(0xFF1A1625)
val SurfaceDark = Color(0xFF241F31)
val CardDark = Color(0xFF2E2841)

val TextPrimary = Color(0xFFF0E6D3)
val TextSecondary = Color(0xFFB8A99A)
val TextHint = Color(0xFF7A6F65)

val AccentRed = Color(0xFFC44536)
val AccentGreen = Color(0xFF4A9C6D)
val AccentBlue = Color(0xFF4A7C9B)

// Dice colors
val DiceD20 = Color(0xFFC44536)
val DiceD12 = Color(0xFFC9A032)
val DiceD10 = Color(0xFF4A9C6D)
val DiceD8 = Color(0xFF4A7C9B)
val DiceD6 = Color(0xFF9B4A9C)
val DiceD4 = Color(0xFF9C6B4A)

private val DarkColorScheme = darkColorScheme(
    primary = PurplePrimary,
    onPrimary = TextPrimary,
    primaryContainer = PurplePrimaryDark,
    onPrimaryContainer = TextPrimary,
    secondary = GoldSecondary,
    onSecondary = BackgroundDark,
    secondaryContainer = GoldSecondaryDark,
    onSecondaryContainer = TextPrimary,
    tertiary = AccentBlue,
    onTertiary = TextPrimary,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = CardDark,
    onSurfaceVariant = TextSecondary,
    error = AccentRed,
    onError = TextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = PurplePrimary,
    onPrimary = Color.White,
    primaryContainer = PurplePrimaryLight,
    onPrimaryContainer = PurplePrimaryDark,
    secondary = GoldSecondary,
    onSecondary = Color.White,
    secondaryContainer = GoldSecondaryLight,
    onSecondaryContainer = GoldSecondaryDark,
    tertiary = AccentBlue,
    onTertiary = Color.White,
    background = Color(0xFFF5F0E8),
    onBackground = Color(0xFF1A1625),
    surface = Color(0xFFFFFBF5),
    onSurface = Color(0xFF1A1625),
    surfaceVariant = Color(0xFFE8E0D5),
    onSurfaceVariant = Color(0xFF4A4540),
    error = AccentRed,
    onError = Color.White
)

@Composable
fun AIDungeonMasterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
