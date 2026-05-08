package com.yusa.autolink.ui.screens.vehicle

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
import com.yusa.autolink.data.model.ChipType
import com.yusa.autolink.data.model.Vehicle
import com.yusa.autolink.ui.components.AutoLinkButton
import com.yusa.autolink.ui.components.AutoLinkCard
import com.yusa.autolink.ui.components.StatusChip
import com.yusa.autolink.ui.screens.dashboard.BottomNavBar
import com.yusa.autolink.ui.theme.*

@Composable
fun MyVehiclesScreen(
    vehicles: List<Vehicle>,
    onNavigateToDashboard: () -> Unit,
    onNavigateToListing: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onAddVehicle: () -> Unit
) {
    Scaffold(
        containerColor = NavyBg,
        topBar = { MyVehiclesTopBar() },
        bottomBar = {
            BottomNavBar(
                currentRoute = "my_vehicles",
                onListingClick = onNavigateToListing,
                onHomeClick = onNavigateToDashboard,
                onMyVehiclesClick = {},
                onProfileClick = onNavigateToProfile
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddVehicle,
                containerColor = Blue500,
                contentColor = TextPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Araç Ekle")
            }
        }
    ) { innerPadding ->
        if (vehicles.isEmpty()) {
            EmptyVehiclesState(
                onAddVehicle = onAddVehicle,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(vehicles, key = { it.id }) { vehicle ->
                    VehicleDetailCard(
                        vehicle = vehicle,
                        isPrimary = vehicles.indexOf(vehicle) == 0
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyVehiclesTopBar() {
    TopAppBar(
        title = {
            Text("Araçlarım", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBg)
    )
}

@Composable
private fun VehicleDetailCard(vehicle: Vehicle, isPrimary: Boolean) {
    AutoLinkCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(NavyContainerHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = Blue300,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(vehicle.displayName, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Spacer(Modifier.height(2.dp))
                    Text(vehicle.plate, style = MaterialTheme.typography.bodyMedium, color = TextHint)
                }
            }
            if (isPrimary) StatusChip("Birincil", ChipType.Info)
        }

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            VehicleMiniStat(
                icon = Icons.Default.DateRange,
                label = "Yıl",
                value = vehicle.year.toString(),
                modifier = Modifier.weight(1f)
            )
            VehicleMiniStat(
                icon = Icons.Default.Speed,
                label = "Kilometre",
                value = vehicle.formattedMileage,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun VehicleMiniStat(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Blue300, modifier = Modifier.size(18.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextHint)
            Text(value, style = MaterialTheme.typography.labelMedium, color = TextPrimary)
        }
    }
}

@Composable
private fun EmptyVehiclesState(onAddVehicle: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.DirectionsCar,
            contentDescription = null,
            tint = TextHint,
            modifier = Modifier.size(72.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text("Henüz araç eklenmedi", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Spacer(Modifier.height(8.dp))
        Text("Aracınızı ekleyerek başlayın", style = MaterialTheme.typography.bodyMedium, color = TextHint)
        Spacer(Modifier.height(24.dp))
        AutoLinkButton(text = "Araç Ekle", onClick = onAddVehicle)
    }
}
