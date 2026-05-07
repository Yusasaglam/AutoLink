package com.yusa.autolink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalCarWash
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yusa.autolink.data.DemoData
import com.yusa.autolink.ui.components.VehicleCard
import com.yusa.autolink.ui.theme.*

// Ana ekran - sadece 2 büyük seçenek: Araba Yıkama ve Oto Bakım
// Profil artık alt menüde olduğu için burada ikon yok
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToBusinessList: (String) -> Unit   // "washing" veya "maintenance" geçilir
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text       = "Merhaba, ${DemoData.currentUser.name.split(" ").first()}",
                            fontSize   = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text     = "Nasıl yardımcı olabiliriz?",
                            fontSize = 13.sp,
                            color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Kayıtlı araç kartı
            VehicleCard(vehicle = DemoData.userVehicle)

            // Bölüm başlığı
            Text(
                text       = "Hangi hizmete ihtiyacınız var?",
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary
            )

            // Araba Yıkama - büyük tıklanabilir kart
            ServiceTypeCard(
                title           = "Araba Yıkama",
                description     = "Hızlı ve detaylı yıkama hizmetleri",
                icon            = Icons.Filled.LocalCarWash,
                backgroundColor = PrimaryBlue,
                onClick         = { onNavigateToBusinessList("washing") }
            )

            // Oto Bakım - büyük tıklanabilir kart
            ServiceTypeCard(
                title           = "Oto Bakım",
                description     = "Periyodik bakım, lastik değişimi ve daha fazlası",
                icon            = Icons.Filled.Build,
                backgroundColor = SuccessGreen,
                onClick         = { onNavigateToBusinessList("maintenance") }
            )
        }
    }
}

// Ana hizmet kartı - tam genişlik, renkli arka plan
@Composable
private fun ServiceTypeCard(
    title:           String,
    description:     String,
    icon:            ImageVector,
    backgroundColor: Color,
    onClick:         () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        shape  = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier              = Modifier.fillMaxSize().padding(24.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = title,
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text     = description,
                    fontSize = 13.sp,
                    color    = Color.White.copy(alpha = 0.85f)
                )
            }
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint     = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(56.dp)
            )
        }
    }
}
