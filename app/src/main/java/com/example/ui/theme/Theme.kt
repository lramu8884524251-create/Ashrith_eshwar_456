package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TvCyanPrimary,
    onPrimary = TvOnCyan,
    primaryContainer = TvCyanContainer,
    onPrimaryContainer = TvCyanPrimary,
    secondary = TvAmberAccent,
    onSecondary = Color.Black,
    secondaryContainer = TvAmberContainer,
    onSecondaryContainer = TvAmberAccent,
    tertiary = SafetyGreen,
    background = TvDarkBackground,
    onBackground = TvTextPrimary,
    surface = TvSurfaceDark,
    onSurface = TvTextPrimary,
    surfaceVariant = TvSurfaceElevated,
    onSurfaceVariant = TvTextSecondary,
    outline = TvSurfaceBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek dark TV theater theme
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
