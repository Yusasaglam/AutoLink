package com.yusa.autolink.ui.screens.listing

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.yusa.autolink.data.model.ChipType
import com.yusa.autolink.data.model.ServiceCenter
import com.yusa.autolink.ui.components.*
import com.yusa.autolink.ui.screens.dashboard.BottomNavBar
import com.yusa.autolink.ui.theme.*

private val sampleCenters = listOf(
    ServiceCenter("1", "Otonomi Servis", "Beşiktaş, İstanbul", 4.8f, 234, "1.2 km", 1.2f, true, true, listOf("Motor", "Fren", "Yağ")),
    ServiceCenter("2", "TeknoOto Merkezi", "Kadıköy, İstanbul", 4.5f, 187, "2.8 km", 2.8f, true, true, listOf("Elektronik", "Klima", "Lastik")),
    ServiceCenter("3", "ProGaraj Servis", "Şişli, İstanbul", 4.2f, 95, "3.5 km", 3.5f, false, false, listOf("Boya", "Kaporta", "Cam")),
    ServiceCenter("4", "Elit Oto Bakım", "Üsküdar, İstanbul", 4.9f, 312, "4.1 km", 4.1f, true, true, listOf("Motor", "Şanzıman", "Fren")),
    ServiceCenter("5", "Hızlı Servis", "Maltepe, İstanbul", 3.9f, 56, "5.7 km", 5.7f, true, false, listOf("Yağ Değişimi", "Filtre")),
)

private val filters = listOf("Tümü", "En Yakın", "En Yüksek Puan", "Açık", "Yetkili Servis")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingScreen(
    providerCenters: List<ServiceCenter> = emptyList(),
    onNavigateToDashboard: () -> Unit,
    onNavigateToMyVehicles: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAppointments: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Tümü") }
    var selectedCenter by remember { mutableStateOf<ServiceCenter?>(null) }

    val allCenters = remember(providerCenters) { sampleCenters + providerCenters }

    val filtered = allCenters
        .filter { center ->
            (searchQuery.isBlank() || center.name.contains(searchQuery, ignoreCase = true) ||
                    center.address.contains(searchQuery, ignoreCase = true)) &&
                    when (selectedFilter) {
                        "Açık" -> center.isOpen
                        "Yetkili Servis" -> center.isAuthorized
                        else -> true
                    }
        }
        .let { list ->
            when (selectedFilter) {
                "En Yakın" -> list.sortedBy { it.distanceKm }
                "En Yüksek Puan" -> list.sortedByDescending { it.rating }
                else -> list
            }
        }

    if (selectedCenter != null) {
        ServiceDetailSheet(
            center = selectedCenter!!,
            onDismiss = { selectedCenter = null },
            onNavigateToAppointments = {
                selectedCenter = null
                onNavigateToAppointments()
            }
        )
    }

    Scaffold(
        containerColor = NavyBg,
        topBar = { ListingTopBar() },
        bottomBar = {
            BottomNavBar(
                currentRoute = "listing",
                onListingClick = {},
                onHomeClick = onNavigateToDashboard,
                onMyVehiclesClick = onNavigateToMyVehicles,
                onProfileClick = onNavigateToProfile
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
            item {
                FilterRow(
                    filters = filters,
                    selected = selectedFilter,
                    onSelect = { selectedFilter = it }
                )
            }
            item {
                Text(
                    "${filtered.size} servis noktası bulundu",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextHint,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
            items(filtered) { center ->
                ServiceCenterCard(
                    center = center,
                    modifier = Modifier.padding(horizontal = 24.dp),
                    onClick = { selectedCenter = center }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServiceDetailSheet(center: ServiceCenter, onDismiss: () -> Unit, onNavigateToAppointments: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = NavySurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(DividerColor)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(center.name, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextHint, modifier = Modifier.size(14.dp))
                        Text(center.address, style = MaterialTheme.typography.bodyMedium, color = TextHint)
                    }
                }
                StatusChip(
                    if (center.isOpen) "Açık" else "Kapalı",
                    if (center.isOpen) ChipType.Success else ChipType.Error
                )
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = DividerColor.copy(alpha = 0.5f))
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (center.isNew) {
                    DetailStat(Icons.Default.Star, "Yeni", "Puan")
                } else {
                    DetailStat(Icons.Default.Star, "${center.rating}", "Puan")
                    DetailStat(Icons.Default.RateReview, "${center.reviewCount}", "Değerlendirme")
                }
                DetailStat(Icons.Default.NearMe, center.distance, "Mesafe")
                if (center.isAuthorized) DetailStat(Icons.Default.Verified, "Evet", "Yetkili")
            }

            Spacer(Modifier.height(20.dp))

            Text("Hizmetler", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                center.services.forEach { service ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NavyContainerHigh)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(service, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onNavigateToAppointments,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Blue500, contentColor = TextPrimary),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Randevu Al", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun DetailStat(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, contentDescription = null, tint = Blue300, modifier = Modifier.size(20.dp))
        Text(value, style = MaterialTheme.typography.labelLarge, color = TextPrimary)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextHint)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListingTopBar() {
    TopAppBar(
        title = {
            Text("Servis Noktaları", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(Icons.Default.FilterList, contentDescription = "Filtrele", tint = TextSecondary)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBg)
    )
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Servis ara...", color = TextHint) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextHint) },
        singleLine = true,
        shape = InputShape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Blue500,
            unfocusedBorderColor = DividerColor,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = NavyContainer,
            unfocusedContainerColor = NavyContainer,
            cursorColor = Blue500
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun FilterRow(filters: List<String>, selected: String, onSelect: (String) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            val isSelected = filter == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) Blue500 else NavyContainer)
                    .border(1.dp, if (isSelected) Blue500 else DividerColor, RoundedCornerShape(8.dp))
                    .clickable { onSelect(filter) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    filter,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) TextPrimary else TextSecondary
                )
            }
        }
    }
}

@Composable
private fun ServiceCenterCard(
    center: ServiceCenter,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    AutoLinkCard(modifier = modifier, onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(center.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextHint, modifier = Modifier.size(14.dp))
                    Text(center.address, style = MaterialTheme.typography.bodyMedium, color = TextHint)
                }
            }
            StatusChip(
                if (center.isOpen) "Açık" else "Kapalı",
                if (center.isOpen) ChipType.Success else ChipType.Error
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (center.isNew) {
                StatusChip("Yeni İşletme", ChipType.Warning)
            } else {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Orange300, modifier = Modifier.size(16.dp))
                    Text("${center.rating} (${center.reviewCount} değerlendirme)", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.NearMe, contentDescription = null, tint = TextHint, modifier = Modifier.size(14.dp))
                Text(center.distance, style = MaterialTheme.typography.labelLarge, color = TextHint)
            }
        }

        if (center.services.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                center.services.take(3).forEach { service ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NavyContainerHigh)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(service, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                }
            }
        }
    }
}
