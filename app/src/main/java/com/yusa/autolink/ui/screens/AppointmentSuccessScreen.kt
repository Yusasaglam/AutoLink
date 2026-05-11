package com.yusa.autolink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yusa.autolink.ui.components.PrimaryButton
import com.yusa.autolink.ui.theme.*

// ============================================================
// AppointmentSuccessScreen — Randevu onay ekranı
//
// Kullanıcı "Randevuyu Onayla" butonuna bastıktan sonra açılır.
// Randevu bilgileri AppNavigation üzerinden AppState'teki
// lastXxx alanlarından parametre olarak bu ekrana iletilir.
//
// Geri tuşu ile Randevu ekranına dönülemez — popUpTo(MAIN)
// sayesinde back stack temizlenmiştir.
//
// İki buton:
//   "Randevularımı Gör" → selectedTab = 1 yaparak MAIN açar
//   "Ana Sayfaya Dön"   → selectedTab = 0 yaparak MAIN açar
// ============================================================
@Composable
fun AppointmentSuccessScreen(
    businessName:              String,
    serviceName:               String,
    date:                      String,
    time:                      String,
    price:                     Int,
    onNavigateToHome:          () -> Unit,
    onNavigateToMyAppointments: () -> Unit
) {
    // SpaceBetween → içerik üst-orta-alt üçe bölünür, eşit boşlukla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // ── Başarı ikonu ve başlık ────────────────────────────────────
        // Yeşil daire içinde beyaz tik ikonu — standart "başarı" görsel dili
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier         = Modifier
                    .size(100.dp)
                    // copy(alpha = 0.10f) → soluk yeşil daire, ikon daha net görünür
                    .background(SuccessGreen.copy(alpha = 0.10f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint               = SuccessGreen,
                    modifier           = Modifier.size(72.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text       = "Randevunuz Oluşturuldu!",
                fontSize   = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign  = TextAlign.Center,
                color      = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text      = "İşletme sizi belirtilen saatte karşılayacak.",
                fontSize  = 14.sp,
                color     = TextSecondary,
                textAlign = TextAlign.Center
            )
        }

        // ── Randevu detay kartı ───────────────────────────────────────
        // elevation = 4.dp → diğer kartlara göre daha belirgin gölge, önem hissi verir
        Card(
            modifier  = Modifier.fillMaxWidth(),
            shape     = RoundedCornerShape(20.dp),
            colors    = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text       = "Randevu Bilgileri",
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))
                // SuccessDetailRow → etiket : değer çiftini yan yana gösterir
                SuccessDetailRow("İşletme", businessName)
                SuccessDetailRow("Hizmet",  serviceName)
                SuccessDetailRow("Tarih",   date)
                SuccessDetailRow("Saat",    time)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                // Net hizmet bedeli — büyük font, yeşil renk, öne çıkarılmış
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text("Net Hizmet Bedeli", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text       = "₺$price",
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color      = SuccessGreen
                    )
                }
            }
        }

        // ── Aksiyon butonları ─────────────────────────────────────────
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Birincil buton → randevular sekmesine direkt gider (selectedTab = 1)
            PrimaryButton(
                text    = "Randevularımı Gör",
                onClick = onNavigateToMyAppointments
            )
            // İkincil buton → ana sayfa (selectedTab = 0), outlined stil daha az ağırlıklı
            OutlinedButton(
                onClick  = onNavigateToHome,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(16.dp)
            ) {
                Text("Ana Sayfaya Dön", fontSize = 15.sp)
            }
        }
    }
}

// ── SuccessDetailRow ──────────────────────────────────────────────────────────
// Randevu detay kartındaki her satır: solda etiket (gri), sağda değer (siyah)
@Composable
private fun SuccessDetailRow(label: String, value: String) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextSecondary, fontSize = 14.sp)
        Text(text = value, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = TextPrimary)
    }
}
