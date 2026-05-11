package com.yusa.autolink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalCarWash
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yusa.autolink.data.AppState
import com.yusa.autolink.ui.components.VehicleCard
import com.yusa.autolink.ui.theme.*

// ============================================================
// HomeScreen — Müşterinin ana sayfası (sekme 0)
//
// Gösterilen içerik:
//   • Üst bar: "Merhaba, [isim]" — AppState.currentUserName'den gelir
//   • Araç kartı veya "Araç Ekle" kartı:
//       - Araç varsa → VehicleCard (mavi kart, ilk araç gösterilir)
//       - Yoksa      → NoVehicleCard (tıklanınca Araçlarım sekmesi açılır)
//   • Araba Yıkama kartı → "washing" parametresiyle işletme listesine gider
//   • Oto Bakım kartı    → "maintenance" parametresiyle işletme listesine gider
// ============================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToBusinessList: (String) -> Unit, // "washing" veya "maintenance"
    onAddVehicle: () -> Unit = {}               // MainScreen sekme = 2'ye geçer
) {
    // firstOrNull() → liste boşsa null döner; null kontrolü aşağıda yapılır
    val firstVehicle = AppState.userVehicles.firstOrNull()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        // split(" ").firstOrNull() → "Eren Tunç" → sadece "Eren" gösterir
                        Text(
                            text       = "Merhaba, ${AppState.currentUserName.split(" ").firstOrNull() ?: "Kullanıcı"}",
                            fontSize   = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text     = "Nasıl yardımcı olabiliriz?",
                            fontSize = 13.sp,
                            color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Araç durumuna göre farklı kart gösterilir
            if (firstVehicle != null) {
                // Components.kt'deki VehicleCard bileşeni — mavi arka planlı araç kartı
                VehicleCard(vehicle = firstVehicle)
            } else {
                // Araç eklenmemişse kullanıcıyı Araçlarım sekmesine yönlendiren kart
                NoVehicleCard(onAddVehicle = onAddVehicle)
            }

            Text(
                text       = "Hangi hizmete ihtiyacınız var?",
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary
            )

            // Mavi kart → yıkama işletmeleri listesine git
            ServiceTypeCard(
                title           = "Araba Yıkama",
                description     = "Hızlı ve detaylı yıkama hizmetleri",
                icon            = Icons.Filled.LocalCarWash,
                backgroundColor = PrimaryBlue,
                onClick         = { onNavigateToBusinessList("washing") }
            )

            // Yeşil kart → bakım işletmeleri listesine git
            ServiceTypeCard(
                title           = "Oto Bakım",
                description     = "Yağ değişimi, lastik ve genel bakım",
                icon            = Icons.Filled.Build,
                backgroundColor = SuccessGreen,
                onClick         = { onNavigateToBusinessList("maintenance") }
            )
        }
    }
}

// ── NoVehicleCard ─────────────────────────────────────────────────────────────
// Kullanıcının hiç aracı yokken gösterilen uyarı kartı.
// Tıklanınca MainScreen'deki onAddVehicle lambda'sı çağrılır
// ve selectedTab = 2 yapılarak Araçlarım sekmesi açılır.
@Composable
private fun NoVehicleCard(onAddVehicle: () -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth().clickable { onAddVehicle() },
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier          = Modifier.padding(20.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Soluk renk → "henüz eklenmedi" mesajını görsel olarak destekler
            Icon(
                Icons.Filled.DirectionsCar,
                contentDescription = null,
                tint     = TextSecondary.copy(alpha = 0.4f),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Araç Eklenmedi",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextPrimary
                )
                Text(
                    "Araçlarım sekmesinden araç ekleyebilirsiniz.",
                    fontSize = 12.sp,
                    color    = TextSecondary
                )
            }
            // + ikonu → tıklanabilir olduğunu görsel olarak belirtir
            Icon(
                Icons.Filled.Add,
                contentDescription = null,
                tint     = PrimaryBlue,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// ── ServiceTypeCard ───────────────────────────────────────────────────────────
// Ana sayfadaki büyük hizmet kartı (Araba Yıkama / Oto Bakım).
// backgroundColor parametresi ile her kart farklı renkte olabilir.
// 120dp sabit yükseklik → iki kart alt alta düzgün görünür.
@Composable
private fun ServiceTypeCard(
    title: String,
    description: String,
    icon: ImageVector,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        shape  = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier              = Modifier.fillMaxSize().padding(24.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(6.dp))
                // Açıklama metni hafif saydam → başlıktan görsel olarak ayrışır
                Text(description, fontSize = 13.sp, color = Color.White.copy(alpha = 0.85f))
            }
            // İkon sağda, hafif saydam
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = Color.White.copy(alpha = 0.9f),
                modifier           = Modifier.size(56.dp)
            )
        }
    }
}
