package com.yusa.autolink.ui.screens.documents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.yusa.autolink.data.model.ChipType
import com.yusa.autolink.ui.components.AutoLinkCard
import com.yusa.autolink.ui.components.StatusChip
import com.yusa.autolink.ui.screens.dashboard.BottomNavBar
import com.yusa.autolink.ui.theme.*

private data class VehicleDocument(
    val title: String,
    val detail: String,
    val expiry: String,
    val icon: ImageVector,
    val chipType: ChipType,
    val statusLabel: String
)

private val documents = listOf(
    VehicleDocument("Araç Ruhsatı", "TC Nüfus Müdürlüğü", "Süresiz", Icons.Default.Description, ChipType.Success, "Geçerli"),
    VehicleDocument("Zorunlu Trafik Sigortası", "Anadolu Sigorta", "30 Ara 2026", Icons.Default.Security, ChipType.Success, "Geçerli"),
    VehicleDocument("Kasko", "Allianz Sigorta", "15 Mar 2027", Icons.Default.Shield, ChipType.Success, "Geçerli"),
    VehicleDocument("Araç Muayenesi", "TÜVTÜRK", "Ağu 2027", Icons.Default.CheckCircle, ChipType.Success, "Geçerli"),
    VehicleDocument("Egzoz Emisyon", "Yetkili İstasyon", "Tem 2026", Icons.Default.Air, ChipType.Warning, "Yakında Sona Eriyor"),
)

@Composable
fun DocumentsScreen(
    vehicleName: String,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToMyVehicles: () -> Unit,
    onNavigateToListing: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    Scaffold(
        containerColor = NavyBg,
        topBar = { DocumentsTopBar(onNavigateBack = onNavigateBack) },
        bottomBar = {
            BottomNavBar(
                currentRoute = "documents",
                onHomeClick = onNavigateToHome,
                onMyVehiclesClick = onNavigateToMyVehicles,
                onListingClick = onNavigateToListing,
                onProfileClick = onNavigateToProfile
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (vehicleName.isNotBlank()) {
                item {
                    AutoLinkCard {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Blue500.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    tint = Blue300,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    vehicleName,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextPrimary
                                )
                                Text(
                                    "${documents.count { it.chipType == ChipType.Success }} belge geçerli",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextHint
                                )
                            }
                        }
                    }
                }
            }

            items(documents) { doc ->
                DocumentCard(doc)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DocumentsTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Text("Belgelerim", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
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
private fun DocumentCard(doc: VehicleDocument) {
    AutoLinkCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(NavyContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(doc.icon, contentDescription = null, tint = Blue300, modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(doc.title, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                Spacer(Modifier.height(2.dp))
                Text(doc.detail, style = MaterialTheme.typography.bodySmall, color = TextHint)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                StatusChip(doc.statusLabel, doc.chipType)
                Text(doc.expiry, style = MaterialTheme.typography.labelSmall, color = TextHint)
            }
        }
    }
}
