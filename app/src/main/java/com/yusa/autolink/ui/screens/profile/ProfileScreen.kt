package com.yusa.autolink.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.yusa.autolink.data.model.ChipType
import com.yusa.autolink.data.model.UserType
import com.yusa.autolink.ui.components.StatusChip
import com.yusa.autolink.ui.screens.dashboard.BottomNavBar
import com.yusa.autolink.ui.screens.serviceprovider.ProviderBottomNavBar
import com.yusa.autolink.ui.theme.*

@Composable
fun ProfileScreen(
    userName: String,
    userEmail: String,
    userPhone: String,
    userType: UserType,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToMyVehicles: () -> Unit,
    onNavigateToListing: () -> Unit,
    onNavigateToProviderAppointments: () -> Unit,
    onLogout: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showAccountInfo by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = NavySurface,
            title = { Text("Çıkış Yap", color = TextPrimary) },
            text = { Text("Hesabınızdan çıkış yapmak istediğinize emin misiniz?", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false; onLogout() }) {
                    Text("Çıkış Yap", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("İptal", color = Blue300)
                }
            }
        )
    }

    if (showAccountInfo) {
        AlertDialog(
            onDismissRequest = { showAccountInfo = false },
            containerColor = NavySurface,
            title = { Text("Hesap Bilgileri", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AccountInfoRow("Ad Soyad", userName.ifBlank { "—" })
                    AccountInfoRow("E-posta", userEmail.ifBlank { "—" })
                    AccountInfoRow("Telefon", userPhone.ifBlank { "—" })
                    AccountInfoRow(
                        "Hesap Türü",
                        if (userType == UserType.SERVICE_PROVIDER) "Servis Sağlayıcı" else "Araç Sahibi"
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAccountInfo = false }) {
                    Text("Kapat", color = Blue300)
                }
            }
        )
    }

    val isProvider = userType == UserType.SERVICE_PROVIDER

    Scaffold(
        containerColor = NavyBg,
        topBar = { ProfileTopBar(onNavigateBack = onNavigateBack) },
        bottomBar = {
            if (isProvider) {
                ProviderBottomNavBar(
                    currentRoute = "profile",
                    onHomeClick = onNavigateToHome,
                    onAppointmentsClick = onNavigateToProviderAppointments,
                    onServicesClick = onNavigateToListing,
                    onProfileClick = {}
                )
            } else {
                BottomNavBar(
                    currentRoute = "profile",
                    onHomeClick = onNavigateToHome,
                    onMyVehiclesClick = onNavigateToMyVehicles,
                    onListingClick = onNavigateToListing,
                    onProfileClick = {}
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            val initials = userName.trim()
                .split(" ")
                .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                .take(2)
                .joinToString("")
                .ifEmpty { "?" }

            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(Blue500.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(initials, style = MaterialTheme.typography.headlineMedium, color = Blue300)
            }

            Spacer(Modifier.height(16.dp))

            Text(
                userName.ifBlank { "Kullanıcı" },
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(userEmail.ifBlank { "—" }, style = MaterialTheme.typography.bodyMedium, color = TextHint)
            Spacer(Modifier.height(12.dp))

            StatusChip(
                text = if (isProvider) "Servis Sağlayıcı" else "Araç Sahibi",
                type = if (isProvider) ChipType.Warning else ChipType.Info
            )

            Spacer(Modifier.height(32.dp))
            HorizontalDivider(color = DividerColor.copy(alpha = 0.5f))
            Spacer(Modifier.height(8.dp))

            ProfileMenuItem(
                icon = Icons.Default.Person,
                title = "Hesap Bilgileri",
                subtitle = "Ad, e-posta, telefon",
                onClick = { showAccountInfo = true }
            )
            ProfileMenuItem(
                icon = Icons.Default.Notifications,
                title = "Bildirim Ayarları",
                subtitle = "Randevu ve servis bildirimleri",
                onClick = {}
            )
            ProfileMenuItem(
                icon = Icons.Default.Lock,
                title = "Gizlilik ve Güvenlik",
                subtitle = "Şifre ve gizlilik tercihleri",
                onClick = {}
            )
            ProfileMenuItem(
                icon = Icons.Default.Info,
                title = "Hakkımızda",
                subtitle = "AutoLink v1.0 · Tüm hakları saklıdır",
                onClick = {}
            )

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = DividerColor.copy(alpha = 0.5f))
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { showLogoutDialog = true }
                    .padding(vertical = 16.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(24.dp))
                Text("Çıkış Yap", style = MaterialTheme.typography.titleSmall, color = ErrorRed)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = { Text("Profil", style = MaterialTheme.typography.titleLarge, color = TextPrimary) },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = TextPrimary)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBg)
    )
}

@Composable
private fun ProfileMenuItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Blue300, modifier = Modifier.size(24.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextHint)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextHint, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun AccountInfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextHint)
        Text(value, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
    }
}
