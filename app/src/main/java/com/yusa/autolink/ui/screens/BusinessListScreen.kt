package com.yusa.autolink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yusa.autolink.data.DemoData
import com.yusa.autolink.data.model.BusinessType
import com.yusa.autolink.ui.components.BusinessCard
import com.yusa.autolink.ui.theme.BackgroundLight
import com.yusa.autolink.ui.theme.TextSecondary

// Aktif filtreler - hangi filtreler seçili?
data class BusinessFilter(
    val valet:       Boolean = false,  // Vale hizmeti olanlar
    val onSite:      Boolean = false,  // Yerinde hizmet verenler
    val highRating:  Boolean = false,  // Yüksek puan sıralaması
    val nearest:     Boolean = false,  // En yakın sıralaması
    val cheapest:    Boolean = false,  // En uygun fiyat sıralaması
    val available:   Boolean = false   // Sadece müsait olanlar
)

// İşletme listesi ekranı - filtreleme özellikli
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessListScreen(
    serviceType: String,               // "washing" veya "maintenance"
    onNavigateToAppointment: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    val title = if (serviceType == "washing") "Yıkama İşletmeleri" else "Bakım İşletmeleri"
    val type  = if (serviceType == "washing") BusinessType.WASHING else BusinessType.MAINTENANCE

    // Filtre durumu
    var filter by remember { mutableStateOf(BusinessFilter()) }

    // Filtreleme ve sıralama mantığı
    val filteredList = DemoData.businesses
        // 1. Hizmet türüne göre filtrele
        .filter { it.type == type }
        // 2. Seçili checkbox filtreleri uygula
        .filter { !filter.valet     || it.hasValet }
        .filter { !filter.onSite    || it.onSiteService }
        .filter { !filter.available || it.isAvailable }
        // 3. Seçili sıralama uygula (birbiriyle çakışmaz)
        .let { list ->
            when {
                filter.highRating -> list.sortedByDescending { it.rating }
                filter.nearest    -> list.sortedBy { it.distanceKm }
                filter.cheapest   -> list.sortedBy { it.startingPrice }
                else              -> list
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Filtre chip'leri - yatay kaydırmalı
            item {
                LazyRow(
                    contentPadding        = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Vale hizmeti filtresi (checkbox tarzı - seçilince sadece valeli olanları göster)
                    item {
                        FilterChip(
                            selected = filter.valet,
                            onClick  = { filter = filter.copy(valet = !filter.valet) },
                            label    = { Text("Vale Hizmeti") }
                        )
                    }
                    // Yerinde hizmet filtresi
                    item {
                        FilterChip(
                            selected = filter.onSite,
                            onClick  = { filter = filter.copy(onSite = !filter.onSite) },
                            label    = { Text("Yerinde Hizmet") }
                        )
                    }
                    // Yüksek puan sıralaması (diğer sıralama filtreleriyle çakışmaz)
                    item {
                        FilterChip(
                            selected = filter.highRating,
                            onClick  = {
                                filter = filter.copy(
                                    highRating = !filter.highRating,
                                    nearest    = false,
                                    cheapest   = false
                                )
                            },
                            label = { Text("Yüksek Puan") }
                        )
                    }
                    // En yakın sıralaması
                    item {
                        FilterChip(
                            selected = filter.nearest,
                            onClick  = {
                                filter = filter.copy(
                                    nearest    = !filter.nearest,
                                    highRating = false,
                                    cheapest   = false
                                )
                            },
                            label = { Text("En Yakın") }
                        )
                    }
                    // En uygun fiyat sıralaması
                    item {
                        FilterChip(
                            selected = filter.cheapest,
                            onClick  = {
                                filter = filter.copy(
                                    cheapest   = !filter.cheapest,
                                    highRating = false,
                                    nearest    = false
                                )
                            },
                            label = { Text("En Uygun Fiyat") }
                        )
                    }
                    // Müsait olanlar filtresi
                    item {
                        FilterChip(
                            selected = filter.available,
                            onClick  = { filter = filter.copy(available = !filter.available) },
                            label    = { Text("Müsait Olanlar") }
                        )
                    }
                }
            }

            // Kaç işletme bulundu
            item {
                Text(
                    text     = "${filteredList.size} işletme bulundu",
                    fontSize = 12.sp,
                    color    = TextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // İşletme kartları
            items(filteredList) { business ->
                BusinessCard(
                    business           = business,
                    onAppointmentClick = { onNavigateToAppointment(business.id) }
                )
            }
        }
    }
}
