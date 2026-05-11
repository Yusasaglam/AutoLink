package com.yusa.autolink.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yusa.autolink.data.model.Business
import com.yusa.autolink.data.model.BusinessType
import com.yusa.autolink.data.model.Vehicle
import com.yusa.autolink.ui.theme.*

// ============================================================
// Components.kt — Yeniden kullanılabilir UI bileşenleri
//
// Bu dosyadaki her fonksiyon @Composable ile işaretlidir —
// yani Compose tarafından ekrana çizilebilen birer UI parçasıdır.
// Birden fazla ekranda kullanılan ortak bileşenler burada toplandı.
//
// Bileşenler:
//   VehicleCard     → Kullanıcının aracını mavi kart üzerinde gösterir
//   BusinessCard    → İşletme listesindeki her işletme kartı
//   TrustBadge      → Renkli etiket (Onaylı, Vale, Müsait…)
//   PrimaryButton   → Ekranlardaki birincil aksiyon butonu
// ============================================================

// ── VehicleCard ───────────────────────────────────────────────────────────────
// Kullanıcının kayıtlı aracını mavi arka plan üzerinde gösterir.
// Kullanıldığı yerler: HomeScreen (ana sayfa araç kartı), ProfileScreen
// modifier parametresi → dışarıdan ek stil uygulanabilmesi için
@Composable
fun VehicleCard(vehicle: Vehicle, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = PrimaryBlue)
    ) {
        Row(
            modifier          = Modifier.padding(20.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Araba ikonu — büyük, beyaz
            Icon(Icons.Filled.DirectionsCar, null, tint = Color.White, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                // "Kayıtlı Aracım" etiketi — hafif saydam, ikincil metin gibi
                Text("Kayıtlı Aracım", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                // Marka + model — kalın, ana başlık
                Text(
                    text       = "${vehicle.brand} ${vehicle.model}",
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                )
                // Yıl · plaka · yakıt tipi — nokta ayracı ile yan yana
                Text(
                    text     = "${vehicle.year} · ${vehicle.plate} · ${vehicle.fuelType}",
                    fontSize = 13.sp,
                    color    = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

// ── BusinessCard ──────────────────────────────────────────────────────────────
// İşletme listesindeki her satırı gösteren kart.
// Kullanıldığı yer: BusinessListScreen
//
// İçerik (yukarıdan aşağıya):
//   0. Hizmet tipi rozeti (Araba Yıkama / Oto Bakım) + puan (sağda)
//   1. İşletme adı
//   2. Mesafe + başlangıç fiyatı
//   3. Rozetler: Onaylı, Vale, Müsait/Meşgul
//   4. Yerinde Hizmet rozeti (varsa)
//   5. "Randevu Al" butonu
@Composable
fun BusinessCard(
    business: Business,
    onAppointmentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        // Hizmet tipine göre renk ve ikon belirlenir
        // WASHING → mavi + yıkama ikonu,  MAINTENANCE → yeşil + çekiç ikonu
        val isWashing   = business.type == BusinessType.WASHING
        val typeColor   = if (isWashing) PrimaryBlue else SuccessGreen
        val typeIcon    = if (isWashing) Icons.Filled.LocalCarWash else Icons.Filled.Build
        val typeLabel   = if (isWashing) "Araba Yıkama" else "Oto Bakım"

        Column(modifier = Modifier.padding(16.dp)) {

            // Satır 0: hizmet tipi rozeti (solda) + puan (sağda)
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Hizmet tipi rozeti — copy(alpha=0.10f) ile soluk arka plan
                Row(
                    modifier = Modifier
                        .background(typeColor.copy(alpha = 0.10f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(typeIcon, null, tint = typeColor, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(typeLabel, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = typeColor)
                }
                // Yıldız + puan — toString() Float'ı "4.8" gibi gösterir
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, null, tint = RatingYellow, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(business.rating.toString(), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Satır 1: işletme adı
            Text(
                text       = business.name,
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Satır 2: mesafe + başlangıç fiyatı
            // services boşsa startingPrice kullanılır; services varsa en düşük fiyat alınır
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(business.distanceText, fontSize = 12.sp, color = TextSecondary)
                }
                val displayPrice = business.services.minOfOrNull { it.price } ?: business.startingPrice
                Text("₺$displayPrice'den başlar", fontSize = 12.sp, color = TextSecondary)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Satır 3: Onaylı + Vale + Müsait/Meşgul rozetleri
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (business.isVerified) {
                    TrustBadge("Onaylı İşletme", VerifiedBadgeColor.copy(alpha = 0.10f), VerifiedBadgeColor)
                }
                if (business.hasValet) {
                    TrustBadge("Vale", Color(0xFF6A1B9A).copy(alpha = 0.10f), Color(0xFF6A1B9A))
                }
                // Müsaitlik rozeti her zaman gösterilir — yeşil veya kırmızı
                val availColor = if (business.isAvailable) SuccessGreen else Color(0xFFD32F2F)
                TrustBadge(
                    text             = if (business.isAvailable) "Müsait" else "Meşgul",
                    backgroundColor  = availColor.copy(alpha = 0.10f),
                    textColor        = availColor
                )
            }

            // Satır 4: Yerinde Hizmet rozeti (varsa ayrı satırda, turuncu)
            if (business.onSiteService) {
                Spacer(modifier = Modifier.height(6.dp))
                TrustBadge(
                    text            = "Yerinde Hizmet",
                    backgroundColor = Color(0xFFE65100).copy(alpha = 0.10f),
                    textColor       = Color(0xFFE65100)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Satır 5: Randevu Al butonu — rengi hizmet tipine göre (mavi/yeşil)
            Button(
                onClick  = onAppointmentClick,
                modifier = Modifier.fillMaxWidth(),
                colors   = ButtonDefaults.buttonColors(containerColor = typeColor),
                shape    = RoundedCornerShape(12.dp)
            ) {
                Icon(typeIcon, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Randevu Al", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ── TrustBadge ────────────────────────────────────────────────────────────────
// Küçük renkli etiket — BusinessCard içinde tekrar kullanılır
// backgroundColor ve textColor parametresi ile her durumda farklı renk alır
@Composable
fun TrustBadge(text: String, backgroundColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = text, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = textColor)
    }
}

// ── PrimaryButton ─────────────────────────────────────────────────────────────
// Ekranlardaki birincil aksiyon butonu (mavi, tam genişlik, 56dp yükseklik)
// Kullanıldığı yerler: AppointmentSuccessScreen, ve başka ekranlarda
// enabled = false → buton grileşir ve tıklanamaz (form doldurulamadan önce)
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick  = onClick,
        modifier = modifier.fillMaxWidth().height(56.dp),
        enabled  = enabled,
        colors   = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
        shape    = RoundedCornerShape(16.dp)
    ) {
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}
