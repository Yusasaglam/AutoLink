package com.yusa.autolink.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AutoLinkColorScheme = darkColorScheme(
    primary = Blue500,
    onPrimary = TextPrimary,
    primaryContainer = NavyContainerHigh,
    onPrimaryContainer = Blue300,
    secondary = Orange500,
    onSecondary = NavyBg,
    secondaryContainer = NavyContainerHigh,
    onSecondaryContainer = Orange300,
    background = NavyBg,
    onBackground = TextPrimary,
    surface = NavySurface,
    onSurface = TextPrimary,
    surfaceVariant = NavyContainer,
    onSurfaceVariant = TextSecondary,
    surfaceContainer = NavyContainer,
    surfaceContainerHigh = NavyContainerHigh,
    surfaceContainerHighest = NavyContainerHighest,
    outline = TextHint,
    outlineVariant = DividerColor,
    error = ErrorRed,
    errorContainer = ErrorContainer,
    onError = NavyBg
)

@Composable
fun AutoLinkTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AutoLinkColorScheme,
        typography = Typography,
        content = content
    )
}
