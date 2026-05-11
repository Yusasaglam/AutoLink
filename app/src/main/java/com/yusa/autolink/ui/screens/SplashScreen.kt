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

// ============================================================
// SplashScreen — Uygulama açıldığında ilk gösterilen ekran
//
// İki işlevi var:
//   1. OtoGüven logosunu 2 saniye fade-in animasyonuyla gösterir
//   2. Geçiş kararını AppNavigation'a bırakır (oturum var mı?)
//
// Ekran AppNavigation'daki onNavigateToOnboarding lambda'sı
// çağrıldığında oturum durumuna göre Login veya MAIN'e geçer.
// ============================================================
@Composable
fun SplashScreen(onNavigateToOnboarding: () -> Unit) {

    // alpha: 0.0 = tamamen görünmez, 1.0 = tamamen görünür
    // Başlangıçta 0, LaunchedEffect içinde 1'e set ediliyor
    var alpha by remember { mutableStateOf(0f) }

    // animateFloatAsState → alpha değeri değişince 800ms'de yumuşakça geçiş yapar (fade-in)
    val animatedAlpha by animateFloatAsState(
        targetValue     = alpha,
        animationSpec   = tween(durationMillis = 800),
        label           = "splash_alpha"
    )

    // LaunchedEffect(Unit) → ekran ilk yüklendiğinde bir kez çalışır
    // delay(2000L) → 2 saniye bekler, bu sürede animasyon tamamlanır
    // Sonra onNavigateToOnboarding() çağrılır → AppNavigation yönlendirme yapar
    LaunchedEffect(Unit) {
        alpha = 1f          // Fade-in animasyonunu başlat
        delay(2000L)        // 2 saniye bekle
        onNavigateToOnboarding()
    }

    // Tam ekran mavi arka plan
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBlue),
        contentAlignment = Alignment.Center
    ) {
        // Modifier.alpha(animatedAlpha) → tüm içerik animasyonla beliriyor
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(animatedAlpha)
        ) {
            // Uygulama adı — büyük ve kalın
            Text(
                text       = "OtoGüven",
                fontSize   = 44.sp,
                fontWeight = FontWeight.Bold,
                color      = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))
            // Slogan — biraz soluk beyaz renk (0.85 alpha) ile hiyerarşi sağlanıyor
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
