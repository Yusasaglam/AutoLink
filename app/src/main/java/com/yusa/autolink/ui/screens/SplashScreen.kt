package com.yusa.autolink.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yusa.autolink.ui.theme.PrimaryBlue
import kotlinx.coroutines.delay

// Uygulama açıldığında gösterilen ilk ekran.
// Fade-in animasyonuyla OtoGüven logosunu gösterir, 2 saniye sonra devam eder.
@Composable
fun SplashScreen(onNavigateToOnboarding: () -> Unit) {

    // Fade-in için alpha değeri (0 = görünmez, 1 = tam görünür)
    var alpha by remember { mutableStateOf(0f) }

    val animatedAlpha by animateFloatAsState(
        targetValue     = alpha,
        animationSpec   = tween(durationMillis = 800),
        label           = "splash_alpha"
    )

    // Ekran yüklendiğinde animasyonu başlat ve 2 saniye bekle
    LaunchedEffect(Unit) {
        alpha = 1f
        delay(2000L)
        onNavigateToOnboarding()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBlue),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(animatedAlpha)
        ) {
            // Uygulama adı
            Text(
                text       = "OtoGüven",
                fontSize   = 44.sp,
                fontWeight = FontWeight.Bold,
                color      = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))
            // Uygulama sloganı
            Text(
                text       = "Aracınız için güvenilir servis, net fiyat.",
                fontSize   = 16.sp,
                color      = Color.White.copy(alpha = 0.85f),
                textAlign  = TextAlign.Center,
                modifier   = Modifier.padding(horizontal = 40.dp)
            )
        }
    }
}
