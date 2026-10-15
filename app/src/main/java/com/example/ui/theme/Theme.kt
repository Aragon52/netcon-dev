package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CopperAccent,
    onPrimary = SteelDeepInk,
    primaryContainer = CopperBevel,
    onPrimaryContainer = CopperHighlight,
    secondary = CopperHighlight,
    background = SteelDark,
    onBackground = TextPrimary,
    surface = SteelBase,
    onSurface = TextPrimary,
    surfaceVariant = SteelTraySurface,
    onSurfaceVariant = TextSecondary,
    outline = SteelBorder
)

@Composable
fun NetconTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
