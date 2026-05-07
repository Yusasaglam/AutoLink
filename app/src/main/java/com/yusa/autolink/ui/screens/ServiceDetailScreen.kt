package com.yusa.autolink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yusa.autolink.data.DemoData
import com.yusa.autolink.ui.components.PrimaryButton
import com.yusa.autolink.ui.components.getServiceIcon
import com.yusa.autolink.ui.theme.*

// Seçilen hizmetin detaylarını gösteren ekran.
// Hizmet açıklaması, ortalama fiyat ve süre bilgisi yer alır.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailScreen(
    serviceId: Int,
    onNavigateToBusinessList: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    // Seçilen hizmeti DemoData'dan bul
    val service = DemoData.services.find { it.id == serviceId } ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(service.name) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Hizmet ikon ve adı - mavi kart
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(16.dp),
                    colors   = CardDefaults.cardColors(containerColor = PrimaryBlue)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector        = getServiceIcon(service.iconName),
                            contentDescription = null,
                            tint               = Color.White,
                            modifier           = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text       = service.name,
                            fontSize   = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color.White
                        )
                    }
                }

                // Hizmet açıklaması
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(16.dp),
                    colors   = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Hizmet Hakkında", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text       = service.description,
                            fontSize   = 14.sp,
                            color      = TextSecondary,
                            lineHeight = 22.sp
                        )
                    }
                }

                // Ortalama fiyat ve süre - yan yana iki kart
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoCard(
                        label    = "Ortalama Fiyat",
                        value    = "₺${service.averagePrice}",
                        icon     = Icons.Filled.Payments,
                        modifier = Modifier.weight(1f)
                    )
                    InfoCard(
                        label    = "Tahmini Süre",
                        value    = service.duration,
                        icon     = Icons.Filled.AccessTime,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // "İşletmeleri Gör" butonu
            PrimaryButton(
                text    = "İşletmeleri Gör",
                onClick = { onNavigateToBusinessList(serviceId) }
            )
        }
    }
}

// Fiyat veya süre gibi tek bir bilgiyi gösteren küçük kart
@Composable
fun InfoCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Column(
            modifier            = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = PrimaryBlue,
                modifier           = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, fontSize = 11.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text       = value,
                fontSize   = 15.sp,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary,
                textAlign  = TextAlign.Center
            )
        }
    }
}
