package com.trollmaster.pro.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = GreenAccent,
    onPrimary = BgDark,
    primaryContainer = GreenDark,
    onPrimaryContainer = BgDark,
    secondary = PurpleAccent,
    onSecondary = BgDark,
    tertiary = RedAccent,
    onTertiary = BgDark,
    background = BgDark,
    onBackground = TextPrimary,
    surface = BgSurface,
    onSurface = TextPrimary,
    surfaceVariant = BgCard,
    onSurfaceVariant = TextSecondary,
    outline = Divider
)

@Composable
fun TrollMasterTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
