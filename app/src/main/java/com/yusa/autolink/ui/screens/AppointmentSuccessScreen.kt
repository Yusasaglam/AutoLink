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

// Randevu başarıyla oluşturulduğunda gösterilen ekran.
// Kullanıcı randevularına gidebilir veya ana sayfaya dönebilir.
@Composable
fun AppointmentSuccessScreen(
    businessName:              String,
    serviceName:               String,
    date:                      String,
    time:                      String,
    price:                     Int,
    onNavigateToHome:          () -> Unit,
    onNavigateToMyAppointments: () -> Unit   // "Randevularımı Gör" butonu için
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Onay ikonu ve başlık
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier         = Modifier
                    .size(100.dp)
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

        // Randevu bilgi kartı
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
                SuccessDetailRow("İşletme", businessName)
                SuccessDetailRow("Hizmet",  serviceName)
                SuccessDetailRow("Tarih",   date)
                SuccessDetailRow("Saat",    time)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                // Net hizmet bedeli
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

        // Aksiyon butonları
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Randevularımı Gör - doğrudan Randevularım sekmesine gider
            PrimaryButton(
                text    = "Randevularımı Gör",
                onClick = onNavigateToMyAppointments
            )
            // Ana sayfaya dön - ikincil seçenek
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

// Bilgi satırı (etiket : değer)
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
