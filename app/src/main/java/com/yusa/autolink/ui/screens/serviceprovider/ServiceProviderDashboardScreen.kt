package com.yusa.autolink.ui.screens.serviceprovider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.yusa.autolink.data.model.BusinessProfile
import com.yusa.autolink.data.model.ChipType
import com.yusa.autolink.ui.components.AutoLinkCard
import com.yusa.autolink.ui.components.SectionHeader
import com.yusa.autolink.ui.components.StatusChip
import com.yusa.autolink.ui.theme.*

@Composable
fun ServiceProviderDashboardScreen(
    businessProfile: BusinessProfile,
    onNavigateToProfile: () -> Unit,
    onNavigateToProviderAppointments: () -> Unit,
    onNavigateToListing: () -> Unit
) {
    Scaffold(
        containerColor = NavyBg,
        topBar = { ProviderTopBar(businessProfile = businessProfile) },
        bottomBar = {
            ProviderBottomNavBar(
                currentRoute = "provider_dashboard",
                onHomeClick = {},
                onAppointmentsClick = onNavigateToProviderAppointments,
                onServicesClick = onNavigateToListing,
                onProfileClick = onNavigateToProfile
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { ProviderStatsRow() }
            item {
                SectionHeader(
                    title = "Bugünkü Randevular",
                    actionText = "Tümü",
                    onAction = onNavigateToProviderAppointments
                )
            }
            item {
                ProviderAppointmentsSummary(onNavigateToProviderAppointments)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProviderTopBar(businessProfile: BusinessProfile) {
    val initials = businessProfile.businessName
        .trim()
        .split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(2)
        .joinToString("")

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Blue500.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        initials.ifEmpty { "S" },
                        style = MaterialTheme.typography.titleSmall,
                        color = Blue300
                    )
                }
                Column {
                    Text(businessProfile.businessName, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Text("Servis Sağlayıcı", style = MaterialTheme.typography.labelSmall, color = Blue300)
                }
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(Icons.Default.Notifications, contentDescription = "Bildirimler", tint = TextSecondary)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBg)
    )
}

@Composable
private fun ProviderStatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ProviderStatCard(Icons.Default.DateRange, "Bugün", "4", "Randevu", ChipType.Info, Modifier.weight(1f))
        ProviderStatCard(Icons.Default.Notifications, "Bekleyen", "4", "Onay", ChipType.Warning, Modifier.weight(1f))
        ProviderStatCard(Icons.Default.Star, "Bu Hafta", "₺12.500", "Gelir", ChipType.Success, Modifier.weight(1f))
    }
}

@Composable
private fun ProviderStatCard(
    icon: ImageVector,
    label: String,
    value: String,
    subtitle: String,
    chipType: ChipType,
    modifier: Modifier = Modifier
) {
    AutoLinkCard(modifier = modifier) {
        Icon(icon, contentDescription = null, tint = Blue300, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(8.dp))
        Text(value, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        Text(subtitle, style = MaterialTheme.typography.labelSmall, color = TextHint)
        Spacer(Modifier.height(8.dp))
        StatusChip(label, chipType)
    }
}

@Composable
private fun ProviderAppointmentsSummary(onSeeAll: () -> Unit) {
    val todayList = listOf(
        Triple("34 ABC 1234", "Periyodik Bakım", "10:00"),
        Triple("06 XYZ 5678", "Yağ Değişimi", "11:30"),
        Triple("34 DEF 9012", "Fren Balataları", "14:00"),
        Triple("35 GHI 3456", "Klima Bakımı", "15:30"),
    )

    AutoLinkCard {
        todayList.forEachIndexed { index, (plate, service, time) ->
            if (index > 0) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = DividerColor.copy(alpha = 0.5f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(NavyContainerHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Blue300, modifier = Modifier.size(18.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(service, style = MaterialTheme.typography.labelLarge, color = TextPrimary)
                    Text(plate, style = MaterialTheme.typography.bodySmall, color = TextHint)
                }
                Text(time, style = MaterialTheme.typography.titleSmall, color = Blue300)
            }
        }

        Spacer(Modifier.height(16.dp))
        HorizontalDivider(color = DividerColor.copy(alpha = 0.5f))
        Spacer(Modifier.height(12.dp))

        TextButton(
            onClick = onSeeAll,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tüm Randevuları Gör", color = Blue300, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Blue300, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun ProviderBottomNavBar(
    currentRoute: String,
    onHomeClick: () -> Unit = {},
    onAppointmentsClick: () -> Unit = {},
    onServicesClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    NavigationBar(containerColor = NavySurface, tonalElevation = 0.dp) {
        NavigationBarItem(
            selected = currentRoute == "provider_dashboard",
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, contentDescription = "Ana Sayfa") },
            label = { Text("Ana Sayfa") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Blue500, selectedTextColor = Blue300,
                indicatorColor = NavyContainerHigh, unselectedIconColor = TextHint, unselectedTextColor = TextHint
            )
        )
        NavigationBarItem(
            selected = currentRoute == "provider_appointments",
            onClick = onAppointmentsClick,
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Randevular") },
            label = { Text("Randevular") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Blue500, selectedTextColor = Blue300,
                indicatorColor = NavyContainerHigh, unselectedIconColor = TextHint, unselectedTextColor = TextHint
            )
        )
        NavigationBarItem(
            selected = currentRoute == "listing",
            onClick = onServicesClick,
            icon = { Icon(Icons.Default.Build, contentDescription = "Hizmetlerim") },
            label = { Text("Hizmetlerim") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Blue500, selectedTextColor = Blue300,
                indicatorColor = NavyContainerHigh, unselectedIconColor = TextHint, unselectedTextColor = TextHint
            )
        )
        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = onProfileClick,
            icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
            label = { Text("Profil") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Blue500, selectedTextColor = Blue300,
                indicatorColor = NavyContainerHigh, unselectedIconColor = TextHint, unselectedTextColor = TextHint
            )
        )
    }
}
