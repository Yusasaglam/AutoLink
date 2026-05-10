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

data class BusinessFilter(
    val valet:      Boolean = false,
    val onSite:     Boolean = false,
    val highRating: Boolean = false,
    val nearest:    Boolean = false,
    val cheapest:   Boolean = false,
    val available:  Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessListScreen(
    serviceType: String,
    onNavigateToAppointment: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    val title = if (serviceType == "washing") "Yıkama İşletmeleri" else "Bakım İşletmeleri"
    val type  = if (serviceType == "washing") BusinessType.WASHING else BusinessType.MAINTENANCE

    var filter by remember { mutableStateOf(BusinessFilter()) }

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
                else              -> list
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
        if (AppState.userCreatedBusinesses.none { it.type == type }) {
            EmptyBusinessState(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundLight)
                    .padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundLight)
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    LazyRow(
                        contentPadding        = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = filter.valet,
                                onClick  = { filter = filter.copy(valet = !filter.valet) },
                                label    = { Text("Vale Hizmeti") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = filter.onSite,
                                onClick  = { filter = filter.copy(onSite = !filter.onSite) },
                                label    = { Text("Yerinde Hizmet") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = filter.highRating,
                                onClick  = { filter = filter.copy(highRating = !filter.highRating, nearest = false, cheapest = false) },
                                label    = { Text("Yüksek Puan") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = filter.nearest,
                                onClick  = { filter = filter.copy(nearest = !filter.nearest, highRating = false, cheapest = false) },
                                label    = { Text("En Yakın") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = filter.cheapest,
                                onClick  = { filter = filter.copy(cheapest = !filter.cheapest, highRating = false, nearest = false) },
                                label    = { Text("En Uygun Fiyat") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = filter.available,
                                onClick  = { filter = filter.copy(available = !filter.available) },
                                label    = { Text("Müsait Olanlar") }
                            )
                        }
                    }
                }

                item {
                    Text(
                        text     = "${filteredList.size} işletme bulundu",
                        fontSize = 12.sp,
                        color    = TextSecondary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }

                items(filteredList) { business ->
                    BusinessCard(
                        business           = business,
                        onAppointmentClick = { onNavigateToAppointment(business.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyBusinessState(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier            = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Filled.Store,
                contentDescription = null,
                tint     = TextSecondary.copy(alpha = 0.4f),
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text       = "Henüz kayıtlı işletme yok",
                fontSize   = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color      = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text      = "Bu kategoride işletmeler uygulama üzerinden\nkayıt oldukça burada görünecektir.",
                fontSize  = 14.sp,
                color     = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}
