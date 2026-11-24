package com.example.sistemariego1.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldGreenLight,
    onPrimary = BackgroundDark,
    primaryContainer = EmeraldGreenDark,
    secondary = SkyBlueLight,
    onSecondary = BackgroundDark,
    tertiary = WaterCyanTertiary,
    background = BackgroundDark,
    surface = SurfaceDark
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldGreenPrimary,
    onPrimary = SurfaceLight,
    primaryContainer = EmeraldGreenLight,
    secondary = SkyBlueSecondary,
    onSecondary = SurfaceLight,
    tertiary = WaterCyanTertiary,
    background = BackgroundLight,
    surface = SurfaceLight
)

@Composable
fun SistemaRiegoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
