package com.yusa.autolink.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.yusa.autolink.data.model.ActivityItem
import com.yusa.autolink.data.model.ActivityType
import com.yusa.autolink.data.model.ChipType
import com.yusa.autolink.ui.components.*
import com.yusa.autolink.ui.theme.*

private val sampleActivities = listOf(
    ActivityItem("1", "Periyodik Bakım", "Mercedes-Benz C180", "15 Nis 2026", ActivityType.SERVICE),
    ActivityItem("2", "Servis Randevusu", "Otonomi Servis — 20 Nis", "18 Nis 2026", ActivityType.APPOINTMENT),
    ActivityItem("3", "Muayene Belgesi", "2026 yılı muayenesi tamamlandı", "10 Nis 2026", ActivityType.DOCUMENT),
    ActivityItem("4", "Sigorta Uyarısı", "Poliçeniz 15 gün içinde sona eriyor", "8 Nis 2026", ActivityType.ALERT),
)

@Composable
fun DashboardScreen(onNavigateToListing: () -> Unit) {
    Scaffold(
        containerColor = NavyBg,
        topBar = { DashboardTopBar() },
        bottomBar = { BottomNavBar(currentRoute = "dashboard", onListingClick = onNavigateToListing) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { VehicleCard() }
            item { QuickActionsRow(onListingClick = onNavigateToListing) }
            item {
                SectionHeader(
                    title = "Son Aktiviteler",
                    actionText = "Tümü",
                    onAction = {}
                )
            }
            items(sampleActivities) { ActivityRow(it) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopBar() {
    TopAppBar(
        title = {
            Column {
                Text(
                    "Merhaba, Ahmet",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Text(
                    "Aracınız sizi bekliyor",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextHint
                )
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(Icons.Default.Notifications, contentDescription = "Bildirimler", tint = TextSecondary)
            }
            Spacer(Modifier.width(4.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBg)
    )
}

@Composable
private fun VehicleCard() {
    AutoLinkCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text("Mercedes-Benz C180", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                Spacer(Modifier.height(4.dp))
                Text("34 ABC 1234", style = MaterialTheme.typography.bodyMedium, color = TextHint)
                Spacer(Modifier.height(4.dp))
                Text("2022 · 45.000 km", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            }
            StatusChip("Aktif", ChipType.Success)
        }

        Spacer(Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            VehicleStat(Icons.Default.LocalGasStation, "Yakıt", "75%", Modifier.weight(1f))
            VehicleStat(Icons.Default.Speed, "Kilometre", "45.000", Modifier.weight(1f))
            VehicleStat(Icons.Default.Build, "Sonraki Bakım", "2.000 km", Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))

        LinearProgressIndicator(
            progress = { 0.75f },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(androidx.compose.foundation.shape.RoundedCornerShape(3.dp)),
            color = Blue500,
            trackColor = NavyContainerHigh
        )
        Spacer(Modifier.height(4.dp))
        Text("Yakıt seviyesi: %75", style = MaterialTheme.typography.labelSmall, color = TextHint)
    }
}

@Composable
private fun VehicleStat(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = Blue300, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.labelLarge, color = TextPrimary)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextHint)
    }
}

@Composable
private fun QuickActionsRow(onListingClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickActionButton(Icons.Default.Build, "Servis", onListingClick, Modifier.weight(1f))
        QuickActionButton(Icons.Default.DateRange, "Randevu", {}, Modifier.weight(1f))
        QuickActionButton(Icons.Default.Description, "Belgeler", {}, Modifier.weight(1f))
        QuickActionButton(Icons.Default.HeadsetMic, "Yardım", {}, Modifier.weight(1f))
    }
}

@Composable
private fun ActivityRow(item: ActivityItem) {
    val (icon, chipType) = when (item.type) {
        ActivityType.SERVICE -> Icons.Default.Build to ChipType.Info
        ActivityType.APPOINTMENT -> Icons.Default.DateRange to ChipType.Warning
        ActivityType.DOCUMENT -> Icons.Default.Description to ChipType.Success
        ActivityType.ALERT -> Icons.Default.Warning to ChipType.Error
    }
    val chipLabel = when (item.type) {
        ActivityType.SERVICE -> "Servis"
        ActivityType.APPOINTMENT -> "Randevu"
        ActivityType.DOCUMENT -> "Belge"
        ActivityType.ALERT -> "Uyarı"
    }

    AutoLinkCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(NavyContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Blue300, modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Spacer(Modifier.height(2.dp))
                Text(item.subtitle, style = MaterialTheme.typography.bodyMedium, color = TextHint, maxLines = 1)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                StatusChip(chipLabel, chipType)
                Text(item.date, style = MaterialTheme.typography.labelSmall, color = TextHint)
            }
        }
    }
}

@Composable
fun BottomNavBar(
    currentRoute: String,
    onListingClick: () -> Unit,
    onHomeClick: () -> Unit = {}
) {
    NavigationBar(containerColor = NavySurface, tonalElevation = 0.dp) {
        NavigationBarItem(
            selected = currentRoute == "dashboard",
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, contentDescription = "Ana Sayfa") },
            label = { Text("Ana Sayfa") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Blue500,
                selectedTextColor = Blue300,
                indicatorColor = NavyContainerHigh,
                unselectedIconColor = TextHint,
                unselectedTextColor = TextHint
            )
        )
        NavigationBarItem(
            selected = currentRoute == "listing",
            onClick = onListingClick,
            icon = { Icon(Icons.Default.Build, contentDescription = "Servisler") },
            label = { Text("Servisler") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Blue500,
                selectedTextColor = Blue300,
                indicatorColor = NavyContainerHigh,
                unselectedIconColor = TextHint,
                unselectedTextColor = TextHint
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Randevular") },
            label = { Text("Randevular") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = TextHint,
                unselectedTextColor = TextHint,
                indicatorColor = NavyContainerHigh
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
            label = { Text("Profil") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = TextHint,
                unselectedTextColor = TextHint,
                indicatorColor = NavyContainerHigh
            )
        )
    }
}
