package com.yusa.autolink.ui.screens.serviceprovider

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.yusa.autolink.data.model.Appointment
import com.yusa.autolink.data.model.AppointmentStatus
import com.yusa.autolink.data.model.BusinessProfile
import com.yusa.autolink.data.model.ChipType
import com.yusa.autolink.ui.components.AutoLinkCard
import com.yusa.autolink.ui.components.StatusChip
import com.yusa.autolink.ui.theme.*

private val pendingRequests = listOf(
    Appointment("1", "34 ABC 1234", "Periyodik Bakım", "—", "Bugün", "10:00", AppointmentStatus.PENDING),
    Appointment("2", "06 XYZ 5678", "Yağ Değişimi", "—", "Bugün", "11:30", AppointmentStatus.PENDING),
    Appointment("3", "34 DEF 9012", "Fren Balataları", "—", "Yarın", "09:00", AppointmentStatus.PENDING),
    Appointment("4", "35 GHI 3456", "Klima Bakımı", "—", "Yarın", "14:30", AppointmentStatus.PENDING),
)
private val confirmedAppointments = listOf(
    Appointment("5", "41 JKL 7890", "Motor Bakımı", "—", "23 May 2026", "13:00", AppointmentStatus.UPCOMING),
)
private val pastAppointments = listOf(
    Appointment("6", "34 MNO 2345", "Rot-Balans", "—", "10 May 2026", "10:00", AppointmentStatus.COMPLETED),
    Appointment("7", "06 PQR 6789", "Şanzıman Bakımı", "—", "5 May 2026", "15:00", AppointmentStatus.COMPLETED),
)

@Composable
fun ProviderAppointmentsScreen(
    businessProfile: BusinessProfile?,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToListing: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Bekleyen (${pendingRequests.size})", "Onaylı", "Geçmiş")

    var acceptedIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var rejectedIds by remember { mutableStateOf<Set<String>>(emptySet()) }

    val visiblePending = pendingRequests.filter { it.id !in acceptedIds && it.id !in rejectedIds }
    val visibleConfirmed = confirmedAppointments + pendingRequests.filter { it.id in acceptedIds }

    Scaffold(
        containerColor = NavyBg,
        topBar = { ProviderAppointmentsTopBar(onNavigateBack = onNavigateBack) },
        bottomBar = {
            ProviderBottomNavBar(
                currentRoute = "provider_appointments",
                onHomeClick = onNavigateToHome,
                onAppointmentsClick = {},
                onServicesClick = onNavigateToListing,
                onProfileClick = onNavigateToProfile
            )
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
                                style = MaterialTheme.typography.labelLarge,
                                color = if (selectedTab == index) Blue300 else TextHint
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    if (visiblePending.isEmpty()) {
                        EmptyProviderState(
                            message = "Bekleyen randevu yok",
                            subtitle = "Yeni talepler burada görünecek",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(visiblePending, key = { it.id }) { appointment ->
                                PendingAppointmentCard(
                                    appointment = appointment,
                                    onAccept = { acceptedIds = acceptedIds + appointment.id },
                                    onReject = { rejectedIds = rejectedIds + appointment.id }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    if (visibleConfirmed.isEmpty()) {
                        EmptyProviderState(
                            message = "Onaylı randevu yok",
                            subtitle = "Bekleyen talepleri onaylayarak başlayın",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(visibleConfirmed, key = { it.id }) { appointment ->
                                ConfirmedAppointmentCard(appointment)
                            }
                        }
                    }
                }
                2 -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(pastAppointments, key = { it.id }) { appointment ->
                            ConfirmedAppointmentCard(appointment)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProviderAppointmentsTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = { Text("Randevular", style = MaterialTheme.typography.titleLarge, color = TextPrimary) },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = TextPrimary)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBg)
    )
}

@Composable
private fun PendingAppointmentCard(
    appointment: Appointment,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    AutoLinkCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(NavyContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Blue300, modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(appointment.serviceName, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                Spacer(Modifier.height(2.dp))
                Text(appointment.vehicleName, style = MaterialTheme.typography.bodySmall, color = TextHint)
                Spacer(Modifier.height(2.dp))
                Text("${appointment.date} · ${appointment.time}", style = MaterialTheme.typography.labelSmall, color = Blue300)
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = onReject,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
            ) {
                Text("Reddet", style = MaterialTheme.typography.labelMedium)
            }
            Button(
                onClick = onAccept,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Blue500, contentColor = TextPrimary)
            ) {
                Text("Onayla", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun ConfirmedAppointmentCard(appointment: Appointment) {
    val (label, chipType) = when (appointment.status) {
        AppointmentStatus.UPCOMING -> "Onaylı" to ChipType.Info
        AppointmentStatus.COMPLETED -> "Tamamlandı" to ChipType.Success
        AppointmentStatus.CANCELLED -> "İptal" to ChipType.Error
        AppointmentStatus.PENDING -> "Bekliyor" to ChipType.Warning
    }
    AutoLinkCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(NavyContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Build, contentDescription = null, tint = Blue300, modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(appointment.serviceName, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                Spacer(Modifier.height(2.dp))
                Text(appointment.vehicleName, style = MaterialTheme.typography.bodySmall, color = TextHint)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                StatusChip(label, chipType)
                Text("${appointment.date} ${appointment.time}", style = MaterialTheme.typography.labelSmall, color = TextHint)
            }
        }
    }
}

@Composable
private fun EmptyProviderState(message: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.DateRange, contentDescription = null, tint = TextHint, modifier = Modifier.size(56.dp))
        Spacer(Modifier.height(12.dp))
        Text(message, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Spacer(Modifier.height(4.dp))
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextHint)
    }
}
