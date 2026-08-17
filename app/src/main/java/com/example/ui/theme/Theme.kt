package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AmberPrimary,
    onPrimary = Color(0xFF1E140A),
    primaryContainer = Color(0xFF4A3416),
    onPrimaryContainer = Color(0xFFFFD54F),
    secondary = AmberSecondary,
    onSecondary = Color(0xFF1E140A),
    tertiary = CyanAccent,
    onTertiary = Color(0xFF002230),
    background = CosmicDark,
    onBackground = Color(0xFFEDE7F6),
    surface = DarkSurface,
    onSurface = Color(0xFFEDE7F6),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFCFD8DC),
    error = RedAccent,
    onError = Color.White
)

@Composable
fun CivilizationInAJarTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
