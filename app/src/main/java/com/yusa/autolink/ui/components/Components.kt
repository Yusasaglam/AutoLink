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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yusa.autolink.data.model.Business
import com.yusa.autolink.data.model.Service
import com.yusa.autolink.data.model.Vehicle
import com.yusa.autolink.ui.theme.*

// ============================================================
// ARAÇ KARTI
// Kullanıcının kayıtlı aracını mavi kart üzerinde gösterir.
// Kullanım: HomeScreen, ProfileScreen
// ============================================================
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
            Icon(Icons.Filled.DirectionsCar, null, tint = Color.White, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Kayıtlı Aracım", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                Text(
                    text       = "${vehicle.brand} ${vehicle.model}",
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                )
                Text(
                    text     = "${vehicle.year} · ${vehicle.plate} · ${vehicle.fuelType}",
                    fontSize = 13.sp,
                    color    = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

// ============================================================
// HİZMET KARTI (küçük, yatay listede)
// ServiceDetailScreen ve eski HomeScreen için uyumluluk amaçlı.
// ============================================================
@Composable
fun ServiceCard(service: Service, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(150.dp).clickable { onClick() },
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier            = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(PrimaryBlue.copy(alpha = 0.10f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(getServiceIcon(service.iconName), null, tint = PrimaryBlue, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(service.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(4.dp))
            Text("~₺${service.averagePrice}", fontSize = 12.sp, color = TextSecondary)
        }
    }
}

// ============================================================
// İŞLETME KARTI
// İşletme adı, puan, mesafe, fiyat, vale, müsaitlik, onay rozeti gösterir.
// Kullanım: BusinessListScreen, HomeScreen
// ============================================================
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
        Column(modifier = Modifier.padding(16.dp)) {

            // Satır 1: İşletme adı + yıldız puan
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text       = business.name,
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary,
                    modifier   = Modifier.weight(1f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, null, tint = RatingYellow, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(business.rating.toString(), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Satır 2: Mesafe + fiyat
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(business.distanceText, fontSize = 12.sp, color = TextSecondary)
                }
                Text("₺${business.startingPrice}'den başlar", fontSize = 12.sp, color = TextSecondary)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Satır 3: Onaylı + Vale + Müsaitlik rozetleri
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (business.isVerified) {
                    TrustBadge("Onaylı İşletme", VerifiedBadgeColor.copy(alpha = 0.10f), VerifiedBadgeColor)
                }
                if (business.hasValet) {
                    TrustBadge("Vale", Color(0xFF6A1B9A).copy(alpha = 0.10f), Color(0xFF6A1B9A))
                }
                // Müsaitlik durumu her zaman gösterilir
                val availColor = if (business.isAvailable) SuccessGreen else Color(0xFFD32F2F)
                TrustBadge(
                    text             = if (business.isAvailable) "Müsait" else "Meşgul",
                    backgroundColor  = availColor.copy(alpha = 0.10f),
                    textColor        = availColor
                )
            }

            // Satır 4: Yerinde hizmet rozeti (ayrı satır - fazla yer kaplamaz)
            if (business.onSiteService) {
                Spacer(modifier = Modifier.height(6.dp))
                TrustBadge(
                    text            = "Yerinde Hizmet",
                    backgroundColor = Color(0xFFE65100).copy(alpha = 0.10f),
                    textColor       = Color(0xFFE65100)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Randevu al butonu
            Button(
                onClick  = onAppointmentClick,
                modifier = Modifier.fillMaxWidth(),
                colors   = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape    = RoundedCornerShape(12.dp)
            ) {
                Text("Randevu Al", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ============================================================
// GÜVEN ROZETİ
// Küçük renkli etiket - onaylı, vale, müsait vb. için
// ============================================================
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

// ============================================================
// ANA BUTON
// Ekranlardaki birincil eylem butonu.
// ============================================================
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

// iconName değerini Material Icon'a çevirir
fun getServiceIcon(iconName: String): ImageVector = when (iconName) {
    "wash"     -> Icons.Filled.LocalCarWash
    "build"    -> Icons.Filled.Build
    "settings" -> Icons.Filled.Settings
    "search"   -> Icons.Filled.Search
    else       -> Icons.Filled.DirectionsCar
}
