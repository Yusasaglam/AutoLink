package com.yusa.autolink.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.yusa.autolink.data.model.ChipType
import com.yusa.autolink.data.model.Vehicle
import com.yusa.autolink.ui.components.*
import com.yusa.autolink.ui.theme.*

@Composable
fun DashboardScreen(
    userName: String,
    vehicles: List<Vehicle>,
    onNavigateToListing: () -> Unit,
    onNavigateToMyVehicles: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToDocuments: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onAddVehicle: () -> Unit
) {
    val primaryVehicle = vehicles.firstOrNull()

    Scaffold(
        containerColor = NavyBg,
        topBar = { DashboardTopBar(userName = userName, hasVehicle = primaryVehicle != null) },
        bottomBar = {
            BottomNavBar(
                currentRoute = "dashboard",
                onListingClick = onNavigateToListing,
                onMyVehiclesClick = onNavigateToMyVehicles,
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
            item {
                if (primaryVehicle != null) {
                    VehicleCard(vehicle = primaryVehicle)
                } else {
                    AddVehicleCard(onAddVehicle = onAddVehicle)
                }
            }
            item {
                QuickActionsRow(
                    onListingClick = onNavigateToListing,
                    onMyVehiclesClick = onNavigateToMyVehicles,
                    onAppointmentsClick = onNavigateToAppointments,
                    onDocumentsClick = onNavigateToDocuments
                )
            }
            if (primaryVehicle != null) {
                item {
                    SectionHeader(
                        title = "Son Aktiviteler",
                        actionText = "Randevu Al",
                        onAction = onNavigateToAppointments
                    )
                }
                item {
                    EmptyActivitiesCard(onNavigateToListing = onNavigateToListing)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopBar(userName: String, hasVehicle: Boolean) {
    val firstName = userName.trim()
        .split(" ")
        .firstOrNull()
        ?.replaceFirstChar { it.uppercase() }

    TopAppBar(
        title = {
            Column {
                Text(
                    if (firstName != null) "Merhaba, $firstName" else "Merhaba",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Text(
                    if (hasVehicle) "Aracınız sizi bekliyor" else "Araç ekleyerek başlayın",
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
private fun VehicleCard(vehicle: Vehicle) {
    AutoLinkCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    vehicle.displayName,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(vehicle.plate, style = MaterialTheme.typography.bodyMedium, color = TextHint)
                Spacer(Modifier.height(4.dp))
                Text(
                    "${vehicle.year}${if (vehicle.mileage > 0) " · ${vehicle.formattedMileage}" else ""}",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
            }
            StatusChip("Aktif", ChipType.Success)
        }

        Spacer(Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            VehicleStat(
                Icons.Default.LocalGasStation, "Yakıt",
                if (vehicle.fuelLevel > 0) "%${vehicle.fuelLevel}" else "—",
                Modifier.weight(1f)
            )
            VehicleStat(Icons.Default.Speed, "Kilometre", vehicle.formattedMileage, Modifier.weight(1f))
            VehicleStat(Icons.Default.DateRange, "Model Yılı", vehicle.year.toString(), Modifier.weight(1f))
        }

        if (vehicle.fuelLevel > 0) {
            Spacer(Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { vehicle.fuelLevel / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Blue500,
                trackColor = NavyContainerHigh
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Yakıt seviyesi: %${vehicle.fuelLevel}",
                style = MaterialTheme.typography.labelSmall,
                color = TextHint
            )
        }
    }
}

@Composable
private fun AddVehicleCard(onAddVehicle: () -> Unit) {
    AutoLinkCard(onClick = onAddVehicle) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Blue500.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Blue300, modifier = Modifier.size(24.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Araç Ekle", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Spacer(Modifier.height(2.dp))
                Text(
                    "Aracınızı ekleyerek tüm özelliklere erişin",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextHint
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextHint, modifier = Modifier.size(20.dp))
        }
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
private fun QuickActionsRow(
    onListingClick: () -> Unit,
    onMyVehiclesClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onDocumentsClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickActionButton(Icons.Default.DirectionsCar, "Araçlarım", onMyVehiclesClick, Modifier.weight(1f))
        QuickActionButton(Icons.Default.Build, "Servis", onListingClick, Modifier.weight(1f))
        QuickActionButton(Icons.Default.DateRange, "Randevu", onAppointmentsClick, Modifier.weight(1f))
        QuickActionButton(Icons.Default.Description, "Belgeler", onDocumentsClick, Modifier.weight(1f))
    }
}

@Composable
private fun EmptyActivitiesCard(onNavigateToListing: () -> Unit) {
    AutoLinkCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Build,
                contentDescription = null,
                tint = TextHint,
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Servis geçmişi henüz yok",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "İlk servis randevunuzu alarak başlayın",
                style = MaterialTheme.typography.bodySmall,
                color = TextHint
            )
            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = onNavigateToListing,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Blue500),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Blue500)
            ) {
                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Servis Noktası Bul", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun BottomNavBar(
    currentRoute: String,
    onHomeClick: () -> Unit = {},
    onMyVehiclesClick: () -> Unit = {},
    onListingClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    NavigationBar(containerColor = NavySurface, tonalElevation = 0.dp) {
        NavigationBarItem(
            selected = currentRoute == "dashboard",
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, contentDescription = "Ana Sayfa") },
            label = { Text("Ana Sayfa") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Blue500, selectedTextColor = Blue300,
                indicatorColor = NavyContainerHigh, unselectedIconColor = TextHint, unselectedTextColor = TextHint
            )
        )
        NavigationBarItem(
            selected = currentRoute == "my_vehicles",
            onClick = onMyVehiclesClick,
            icon = { Icon(Icons.Default.DirectionsCar, contentDescription = "Araçlarım") },
            label = { Text("Araçlarım") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Blue500, selectedTextColor = Blue300,
                indicatorColor = NavyContainerHigh, unselectedIconColor = TextHint, unselectedTextColor = TextHint
            )
        )
        NavigationBarItem(
            selected = currentRoute == "listing",
            onClick = onListingClick,
            icon = { Icon(Icons.Default.Build, contentDescription = "Servisler") },
            label = { Text("Servisler") },
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
