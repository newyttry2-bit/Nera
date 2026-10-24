package com.newyttry2bit.nera.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = VirexPrimary,
    onPrimary = VirexBlack,
    background = VirexBlack,
    surface = VirexSurface,
    onSurface = VirexText,
    onSurfaceVariant = VirexSubText
)

@Composable
fun NeraTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = Typography,
        content = content
    )
}
