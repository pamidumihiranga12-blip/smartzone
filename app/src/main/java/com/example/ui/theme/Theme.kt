package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SmartZoneColorScheme = lightColorScheme(
    primary = SmartZoneBlue,
    onPrimary = Color.White,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = Color(0xFF21005D),
    secondary = Color(0xFF625B71),
    onSecondary = Color.White,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = Color(0xFF1D192B),
    tertiary = SmartZoneOrange,
    background = LightBackground,
    onBackground = TextDark,
    surface = LightSurface,
    onSurface = TextDark,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextMuted,
    outline = BorderColor
)

@Composable
fun SmartZoneTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SmartZoneColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    SmartZoneTheme(content = content)
}
