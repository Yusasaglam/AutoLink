package com.yusa.autolink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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
import com.yusa.autolink.ui.theme.*

// Profil ekranı - kullanıcı bilgisi, destek seçenekleri ve çıkış
// Randevular ve araçlar artık alt menüdeki kendi sekmelerinde
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    val user = DemoData.currentUser

    Scaffold(
        topBar = {
            TopAppBar(
                title  = { Text("Profilim") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Kullanıcı bilgi kartı - isim, telefon, e-posta
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp),
                colors   = CardDefaults.cardColors(containerColor = PrimaryBlue)
            ) {
                Row(
                    modifier          = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // İsmin baş harfinden profil avatarı
                    Box(
                        modifier         = Modifier
                            .size(64.dp)
                            .background(Color.White.copy(alpha = 0.20f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = user.name.first().toString(),
                            fontSize   = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(user.name,  fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(user.phone, fontSize = 14.sp, color = Color.White.copy(alpha = 0.85f))
                        Text(user.email, fontSize = 12.sp, color = Color.White.copy(alpha = 0.70f))
                    }
                }
            }

            // Destek bölümü
            Text("Destek", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp),
                colors   = CardDefaults.cardColors(containerColor = SurfaceWhite)
            ) {
                Column {
                    ProfileMenuItem(icon = Icons.Filled.Phone, text = "Bizi Arayın")
                    HorizontalDivider()
                    ProfileMenuItem(icon = Icons.Filled.Email, text = "E-posta Gönderin")
                    HorizontalDivider()
                    ProfileMenuItem(icon = Icons.Filled.Info,  text = "Hakkımızda")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Çıkış yap butonu - tıklanınca AppNavigation'da LOGIN ekranına yönlendirir
            Button(
                onClick  = onLogout,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F)
                ),
                shape    = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Çıkış Yap", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }
    }
}

// Profil menü satırı
@Composable
private fun ProfileMenuItem(icon: ImageVector, text: String) {
    Row(
        modifier          = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, fontSize = 15.sp, modifier = Modifier.weight(1f))
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextSecondary)
    }
}
