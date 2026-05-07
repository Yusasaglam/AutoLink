package com.yusa.autolink.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// OtoGüven uygulaması için açık (light) renk şeması
private val OtoGuvenColorScheme = lightColorScheme(
    primary          = PrimaryBlue,
    onPrimary        = SurfaceWhite,
    background       = BackgroundLight,
    onBackground     = TextPrimary,
    surface          = SurfaceWhite,
    onSurface        = TextPrimary,
    secondary        = SuccessGreen,
    onSecondary      = SurfaceWhite,
)

// Tüm uygulamada kullanılan Compose tema sarmalayıcısı
@Composable
fun AutoLinkTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = OtoGuvenColorScheme,
        typography  = Typography,
        content     = content
    )
}
