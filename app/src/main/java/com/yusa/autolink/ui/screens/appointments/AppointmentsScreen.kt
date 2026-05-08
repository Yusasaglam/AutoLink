package com.yusa.autolink.ui.screens.appointments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.yusa.autolink.data.model.Appointment
import com.yusa.autolink.data.model.AppointmentStatus
import com.yusa.autolink.data.model.ChipType
import com.yusa.autolink.ui.components.AutoLinkCard
import com.yusa.autolink.ui.components.StatusChip
import com.yusa.autolink.ui.screens.dashboard.BottomNavBar
import com.yusa.autolink.ui.theme.*

@Composable
fun AppointmentsScreen(
    vehicleName: String,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToMyVehicles: () -> Unit,
    onNavigateToListing: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val appointments = remember(vehicleName) {
        val name = vehicleName.ifBlank { "Aracınız" }
        listOf(
            Appointment("1", name, "Periyodik Bakım", "Otonomi Servis", "22 May 2026", "14:00", AppointmentStatus.UPCOMING),
            Appointment("2", name, "Yağ Değişimi", "TeknoOto Merkezi", "10 Nis 2026", "10:30", AppointmentStatus.COMPLETED),
            Appointment("3", name, "Fren Balataları", "ProGaraj Servis", "28 Mar 2026", "09:00", AppointmentStatus.COMPLETED),
            Appointment("4", name, "Klima Bakımı", "Elit Oto Bakım", "15 Mar 2026", "15:30", AppointmentStatus.CANCELLED),
        )
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Yaklaşan", "Geçmiş")

    val filteredAppointments = appointments.filter { a ->
        when (selectedTab) {
            0 -> a.status == AppointmentStatus.UPCOMING || a.status == AppointmentStatus.PENDING
            else -> a.status == AppointmentStatus.COMPLETED || a.status == AppointmentStatus.CANCELLED
        }
    }

    Scaffold(
        containerColor = NavyBg,
        topBar = { AppointmentsTopBar(onNavigateBack = onNavigateBack) },
        bottomBar = {
            BottomNavBar(
                currentRoute = "appointments",
                onHomeClick = onNavigateToHome,
                onMyVehiclesClick = onNavigateToMyVehicles,
                onListingClick = onNavigateToListing,
                onProfileClick = onNavigateToProfile
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToListing,
                containerColor = Blue500,
                contentColor = TextPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Randevu Al")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SecondaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = NavyBg,
                contentColor = Blue300
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                color = if (selectedTab == index) Blue300 else TextHint
                            )
                        }
                    )
                }
            }

            if (filteredAppointments.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        tint = TextHint,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Randevu bulunamadı",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Servis randevusu almak için + butonuna basın",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextHint
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredAppointments, key = { it.id }) { appointment ->
                        AppointmentCard(appointment)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppointmentsTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Text("Randevularım", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Geri",
                    tint = TextPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBg)
    )
}

@Composable
private fun AppointmentCard(appointment: Appointment) {
    val (statusLabel, chipType) = when (appointment.status) {
        AppointmentStatus.PENDING -> "Onay Bekleniyor" to ChipType.Warning
        AppointmentStatus.UPCOMING -> "Yaklaşan" to ChipType.Info
        AppointmentStatus.COMPLETED -> "Tamamlandı" to ChipType.Success
        AppointmentStatus.CANCELLED -> "İptal" to ChipType.Error
    }

    AutoLinkCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(NavyContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Build,
                    contentDescription = null,
                    tint = Blue300,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    appointment.serviceName,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    appointment.serviceCenter,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextHint
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    appointment.vehicleName,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                StatusChip(statusLabel, chipType)
                Text(
                    "${appointment.date} ${appointment.time}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextHint
                )
            }
        }
    }
}
