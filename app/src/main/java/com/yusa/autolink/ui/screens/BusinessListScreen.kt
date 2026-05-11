package com.yusa.autolink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yusa.autolink.data.AppState
import com.yusa.autolink.data.model.BusinessType
import com.yusa.autolink.ui.components.BusinessCard
import com.yusa.autolink.ui.theme.*

// ============================================================
// BusinessListScreen — İşletme listesi ekranı
//
// HomeScreen'den "washing" veya "maintenance" parametresiyle açılır.
// İki veri kaynağı birleştirilir:
//   • AppState.userCreatedBusinesses → kullanıcıların kayıt sırasında
//     oluşturduğu işletmeler (id >= 100)
//   Sadece serviceType ile eşleşen işletmeler gösterilir.
//
// Filtreleme:
//   BusinessFilter data class'ındaki boolean alanlar FilterChip ile
//   toggle edilir. Zincirlenmiş .filter() çağrıları listeyi daraltır,
//   sortedBy/sortedByDescending ise sıralamayı değiştirir.
//
// UI yapısı:
//   LazyRow  → yatay kaydırmalı filtre chip satırı
//   LazyColumn → dikey işletme listesi (sadece görünenler render edilir)
// ============================================================

// Filtre seçeneklerini tutan data class
// Her alan false başlar; kullanıcı chip'e basınca true olur
data class BusinessFilter(
    val valet:      Boolean = false,  // Sadece vale hizmeti olan işletmeler
    val onSite:     Boolean = false,  // Sadece yerinde hizmet verenler
    val highRating: Boolean = false,  // En yüksek puana göre sırala
    val nearest:    Boolean = false,  // En yakına göre sırala (distanceKm)
    val cheapest:   Boolean = false,  // En ucuza göre sırala (startingPrice)
    val available:  Boolean = false   // Sadece müsait ("Meşgul" olmayanlar)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessListScreen(
    serviceType: String,                      // "washing" veya "maintenance"
    onNavigateToAppointment: (Int) -> Unit,   // BusinessId ile randevu ekranına git
    onNavigateBack: () -> Unit
) {
    // Ekran başlığı ve filtre türü serviceType'a göre belirlenir
    val title = if (serviceType == "washing") "Yıkama İşletmeleri" else "Bakım İşletmeleri"
    val type  = if (serviceType == "washing") BusinessType.WASHING else BusinessType.MAINTENANCE

    // filter state'i → chip tıklamalarında güncellenir, liste otomatik yenilenir
    var filter by remember { mutableStateOf(BusinessFilter()) }

    // Zincirlenmiş filtreleme + sıralama:
    // 1. Sadece doğru tiptekileri al
    // 2. Aktif boolean filtrelerini uygula (false ise atlanır)
    // 3. Sıralama seçiliyse listeyi sırala
    val filteredList = AppState.userCreatedBusinesses
        .filter { it.type == type }
        .filter { !filter.valet     || it.hasValet }
        .filter { !filter.onSite    || it.onSiteService }
        .filter { !filter.available || it.isAvailable }
        .let { list ->
            when {
                filter.highRating -> list.sortedByDescending { it.rating }
                filter.nearest    -> list.sortedBy { it.distanceKm }
                filter.cheapest   -> list.sortedBy { it.startingPrice }
                else              -> list  // Sıralama seçili değilse orijinal sıra
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(paddingValues)
        ) {
            // ── Filtre chip'leri (yatay kaydırmalı) ──────────────────
            // LazyRow → tüm chip'ler sığmazsa yatay kaydırma aktif olur
            LazyRow(
                contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    // FilterChip → seçili ise dolu, değilse outline görünür
                    FilterChip(
                        selected = filter.valet,
                        onClick  = { filter = filter.copy(valet = !filter.valet) },
                        label    = { Text("Vale") }
                    )
                }
                item {
                    FilterChip(
                        selected = filter.onSite,
                        onClick  = { filter = filter.copy(onSite = !filter.onSite) },
                        label    = { Text("Yerinde") }
                    )
                }
                item {
                    FilterChip(
                        selected = filter.highRating,
                        onClick  = { filter = filter.copy(highRating = !filter.highRating) },
                        label    = { Text("Yüksek Puan") }
                    )
                }
                item {
                    FilterChip(
                        selected = filter.nearest,
                        onClick  = { filter = filter.copy(nearest = !filter.nearest) },
                        label    = { Text("En Yakın") }
                    )
                }
                item {
                    FilterChip(
                        selected = filter.cheapest,
                        onClick  = { filter = filter.copy(cheapest = !filter.cheapest) },
                        label    = { Text("Uygun Fiyat") }
                    )
                }
                item {
                    FilterChip(
                        selected = filter.available,
                        onClick  = { filter = filter.copy(available = !filter.available) },
                        label    = { Text("Müsait") }
                    )
                }
            }

            // ── İşletme listesi ───────────────────────────────────────
            if (filteredList.isEmpty()) {
                // Filtre sonucu boş gelirse bilgilendirme gösterilir
                Box(
                    modifier         = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.Store,
                            contentDescription = null,
                            tint     = TextSecondary.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text      = "İşletme bulunamadı",
                            fontSize  = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color     = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text      = "Farklı filtreler deneyebilirsiniz.",
                            fontSize  = 14.sp,
                            color     = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // LazyColumn → sadece görünen kartları render eder (performans için)
                // key = { it.id } → Compose hangi kartın güncellendiğini takip eder
                LazyColumn(
                    contentPadding      = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items(filteredList, key = { it.id }) { business ->
                        // BusinessCard → Components.kt'den gelen yeniden kullanılabilir bileşen
                        BusinessCard(
                            business           = business,
                            onAppointmentClick = { onNavigateToAppointment(business.id) }
                        )
                    }
                }
            }
        }
    }
}
