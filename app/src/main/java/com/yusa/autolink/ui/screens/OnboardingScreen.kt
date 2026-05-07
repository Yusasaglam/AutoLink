package com.yusa.autolink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yusa.autolink.data.DemoData
import com.yusa.autolink.ui.theme.BackgroundLight
import com.yusa.autolink.ui.theme.PrimaryBlue

// Onboarding ekranı - uygulamanın 3 temel özelliğini tanıtır.
// Kullanıcı "İleri" ile ilerler, son sayfada "Başla" ile ana sayfaya geçer.
@Composable
fun OnboardingScreen(onNavigateToHome: () -> Unit) {

    // Hangi tanıtım sayfasında olduğumuzu tutan durum değişkeni
    var currentPage by remember { mutableIntStateOf(0) }
    val pages      = DemoData.onboardingPages
    val isLastPage = currentPage == pages.size - 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Üst sağda "Atla" butonu
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onNavigateToHome) {
                Text("Atla", color = PrimaryBlue, fontSize = 14.sp)
            }
        }

        // Orta alan: ikon, başlık, açıklama
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = getOnboardingIcon(pages[currentPage].iconName),
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text       = pages[currentPage].title,
                fontSize   = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign  = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text      = pages[currentPage].description,
                fontSize  = 15.sp,
                color     = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }

        // Alt alan: sayfa noktaları ve buton
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Sayfa konumunu gösteren noktalar (aktif olanı daha uzun)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(pages.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(
                                width  = if (index == currentPage) 24.dp else 8.dp,
                                height = 8.dp
                            )
                            .background(
                                color = if (index == currentPage)
                                    PrimaryBlue else PrimaryBlue.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            // İleri veya Başla butonu
            Button(
                onClick = {
                    if (isLastPage) onNavigateToHome()
                    else currentPage++
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape    = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text       = if (isLastPage) "Başla" else "İleri",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// iconName değerini Material Icon'a çevirir
private fun getOnboardingIcon(iconName: String): ImageVector = when (iconName) {
    "price_check"   -> Icons.Filled.CheckCircle
    "directions_car"-> Icons.Filled.DirectionsCar
    "calendar_today"-> Icons.Filled.CalendarToday
    else            -> Icons.Filled.Star
}
