package com.yusa.autolink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yusa.autolink.data.AppState
import com.yusa.autolink.data.model.Appointment
import com.yusa.autolink.data.model.AppointmentStatus
import com.yusa.autolink.ui.theme.*

// ============================================================
// MyAppointmentsScreen — Kullanıcının randevularını listeler (sekme 1)
//
// Özellikler:
//   • LazyColumn → uzun listelerde sadece ekranda görünen kartlar
//     render edilir, performans kazanılır
//   • refreshKey → int state, değeri değişince liste yeniden okunur
//     (iptal/puanlama sonrası ekranı güncellemek için kullanılır)
//   • PENDING randevularda "İptal Et" butonu
//   • COMPLETED randevularda 1-5 yıldız değerlendirme
//   • StatusBadge → durum renkli rozet olarak gösterilir
// ============================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppointmentsScreen() {
    // refreshKey her artışında remember(refreshKey) bloğu yeniden hesaplanır
    // → AppState.userAppointments listesi tekrar okunur, liste güncellenir
    var refreshKey by remember { mutableIntStateOf(0) }
    val appointments = remember(refreshKey) { AppState.userAppointments.sortedByDescending { it.id } }

    Scaffold(
        topBar = {
            TopAppBar(
                title  = { Text("Randevularım") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        if (appointments.isEmpty()) {
            // Hiç randevu yoksa bilgilendirme ekranı gösterilir
            EmptyAppointmentsState(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundLight)
                    .padding(paddingValues)
            )
        } else {
            // LazyColumn → tüm kartları tek seferde değil, görüntülendikçe çizer
            // key = { it.id } → Compose hangi kartın güncellendiğini id ile takip eder
            LazyColumn(
                modifier            = Modifier
                    .fillMaxSize()
                    .background(BackgroundLight)
                    .padding(paddingValues),
                contentPadding      = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(appointments, key = { it.id }) { appointment ->
                    AppointmentCard(
                        appointment = appointment,
                        onCancel    = {
                            // AppState.updateAppointmentStatus → tüm kullanıcılar taranır,
                            // ilgili randevu CANCELLED yapılır, SharedPreferences kaydedilir
                            AppState.updateAppointmentStatus(appointment.id, AppointmentStatus.CANCELLED)
                            refreshKey++ // Listeyi yenile
                        },
                        onRate      = { rating ->
                            // Puan verme → AppState'te randevu güncellenir,
                            // işletmenin ortalama puanı da yeniden hesaplanır
                            AppState.rateAppointment(appointment.id, rating)
                            refreshKey++
                        }
                    )
                }
            }
        }
    }
}

// ── EmptyAppointmentsState ────────────────────────────────────────────────────
// Hiç randevu yokken gösterilen boş durum ekranı.
// İkon + açıklama metni ile kullanıcıya ne yapması gerektiği anlatılır.
@Composable
private fun EmptyAppointmentsState(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier            = Modifier.padding(32.dp)
        ) {
            // Soluk ikon → "içerik yok" durumunu görsel olarak vurgular
            Icon(
                Icons.Filled.DateRange,
                contentDescription = null,
                tint     = TextSecondary.copy(alpha = 0.4f),
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text       = "Henüz randevunuz yok",
                fontSize   = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color      = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text      = "Ana sayfadan bir hizmet seçerek\nilk randevunuzu oluşturabilirsiniz.",
                fontSize  = 14.sp,
                color     = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

// ── AppointmentCard ───────────────────────────────────────────────────────────
// Tek bir randevuyu gösteren kart.
// İçerik duruma göre değişir:
//   PENDING   → "İptal Et" butonu
//   COMPLETED + puan=0  → yıldız seçim satırı
//   COMPLETED + puan>0  → verilen puan gösterilir (tıklanamaz)
@Composable
private fun AppointmentCard(
    appointment: Appointment,
    onCancel:    () -> Unit,
    onRate:      (Int) -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Üst satır: işletme adı (solda) + durum rozeti (sağda)
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text       = appointment.businessName,
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary,
                    modifier   = Modifier.weight(1f)
                )
                // Durum rozeti → rengi AppointmentStatus'a göre değişir
                StatusBadge(status = appointment.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Randevu detayları
            Text(appointment.serviceName, fontSize = 13.sp, color = TextSecondary)
            Text(appointment.vehicleName, fontSize = 13.sp, color = TextSecondary)
            Text(
                "${appointment.date} · ${appointment.time}",
                fontSize = 12.sp,
                color    = TextSecondary
            )

            // Vale veya yerinde hizmet adresi varsa mavi renkte göster
            if (appointment.hasValet && appointment.valetAddress.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Vale: ${appointment.valetAddress}",
                    fontSize   = 12.sp,
                    color      = PrimaryBlue,
                    fontWeight = FontWeight.Medium
                )
            }
            if (appointment.isOnSite && appointment.onSiteAddress.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Yerinde Hizmet: ${appointment.onSiteAddress}",
                    fontSize   = 12.sp,
                    color      = PrimaryBlue,
                    fontWeight = FontWeight.Medium
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            // Fiyat — yeşil renk, "Toplam Ücret" etiketiyle sağ alt köşede
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Toplam Ücret", fontSize = 14.sp, color = TextSecondary)
                Text(
                    "₺${appointment.totalPrice}",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color      = SuccessGreen
                )
            }

            // PENDING → kullanıcı iptal edebilir (Confirmed/Completed'da iptal yok)
            if (appointment.status == AppointmentStatus.PENDING) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick        = onCancel,
                    modifier       = Modifier.fillMaxWidth(),
                    shape          = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFD32F2F)
                    ),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(width = 1.dp)
                ) {
                    Text("Randevuyu İptal Et", fontSize = 13.sp)
                }
            }

            // COMPLETED → kullanıcı 1-5 yıldız verebilir
            // userRating = 0 → henüz puan verilmedi (yıldız seçimi gösterilir)
            // userRating > 0 → puan verilmiş (sadece gösterim, tıklanamaz)
            if (appointment.status == AppointmentStatus.COMPLETED) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = CardBorder)
                Spacer(modifier = Modifier.height(10.dp))

                if (appointment.userRating == 0) {
                    Text(
                        "Hizmeti değerlendirin",
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color      = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    // onRate null değil → tıklanabilir modda
                    StarRow(currentRating = 0, onRate = onRate)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Değerlendirmeniz: ", fontSize = 13.sp, color = TextSecondary)
                        // onRate = null → sadece gösterim modu (tıklanamaz)
                        StarRow(currentRating = appointment.userRating, onRate = null)
                    }
                }
            }
        }
    }
}

// ── StarRow ───────────────────────────────────────────────────────────────────
// 5 yıldızlı değerlendirme satırı.
// onRate = lambda  → interaktif mod: tıklanınca puan verilir
// onRate = null    → sadece gösterim modu: tıklanamaz, sadece mevcut puan gösterilir
@Composable
private fun StarRow(currentRating: Int, onRate: ((Int) -> Unit)?) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        for (i in 1..5) {
            val filled = i <= currentRating // Bu yıldız dolu mu?
            IconButton(
                onClick  = { onRate?.invoke(i) }, // onRate null ise hiçbir şey olmaz
                enabled  = onRate != null,         // null ise buton devre dışı
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    // Dolu yıldız → sarı, boş yıldız → soluk gri
                    imageVector = if (filled) Icons.Filled.Star else Icons.Filled.StarOutline,
                    contentDescription = "$i yıldız",
                    tint     = if (filled) Color(0xFFFFC107) else TextSecondary.copy(alpha = 0.4f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// ── StatusBadge ───────────────────────────────────────────────────────────────
// Randevu kartının sağ üstündeki renkli durum rozeti.
// Triple(metin, arka plan rengi, metin rengi) → destructuring ile alınır.
// Her durum kendi renk temasına sahiptir:
//   CONFIRMED → yeşil,  PENDING → turuncu,  COMPLETED → mavi,  CANCELLED → kırmızı
@Composable
private fun StatusBadge(status: AppointmentStatus) {
    val (text, bgColor, textColor) = when (status) {
        AppointmentStatus.CONFIRMED -> Triple("Onaylandı",  Color(0xFF1B5E20).copy(alpha = 0.10f), Color(0xFF2E7D32))
        AppointmentStatus.PENDING   -> Triple("Beklemede",  Color(0xFFF57F17).copy(alpha = 0.12f), Color(0xFFF57F17))
        AppointmentStatus.COMPLETED -> Triple("Tamamlandı", Color(0xFF1565C0).copy(alpha = 0.10f), Color(0xFF1565C0))
        AppointmentStatus.CANCELLED -> Triple("İptal",      Color(0xFFD32F2F).copy(alpha = 0.10f), Color(0xFFD32F2F))
    }

    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = textColor)
    }
}
