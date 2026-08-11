package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CanvasNovaDarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = Color.Black,
    primaryContainer = DeepViolet,
    onPrimaryContainer = Color.White,
    secondary = NeonMagenta,
    onSecondary = Color.White,
    tertiary = AmberGlow,
    background = DarkCanvas,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = GlassCardBorder
)

@Composable
fun CanvasNovaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CanvasNovaDarkColorScheme,
        typography = Typography,
        content = content
    )
}

