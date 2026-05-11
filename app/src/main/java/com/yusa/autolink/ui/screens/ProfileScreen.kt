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
import com.yusa.autolink.data.AppState
import com.yusa.autolink.ui.theme.*

// ============================================================
// ProfileScreen — Kullanıcı profili ve destek seçenekleri (sekme 3)
//
// İçerik:
//   • Kullanıcı kartı: mavi arka plan, ismin baş harfi avatar olarak
//     gösterilir (profil fotoğrafı yoktur), ad, telefon, e-posta
//   • Destek menüsü: Bizi Arayın, E-posta, Hakkımızda (demo)
//   • Çıkış Yap butonu: AppState.logout() → oturum kapatılır,
//     giriş ekranına yönlendirilir
// ============================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    val userName  = AppState.currentUserName
    val userPhone = AppState.currentUserPhone
    val userEmail = AppState.currentUserEmail
    // firstOrNull() → isim boşsa "?" gösterilir; uppercase yoksa büyük harf için toString() yeterli
    val initial   = userName.firstOrNull()?.toString() ?: "?"

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
            // ── Kullanıcı bilgi kartı ─────────────────────────────────
            // Mavi arka plan + daire içinde baş harf → basit ama etkili avatar
            // copy(alpha = 0.20f) → hafif saydam beyaz daire, mavi üstünde görünür
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp),
                colors   = CardDefaults.cardColors(containerColor = PrimaryBlue)
            ) {
                Row(
                    modifier          = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // CircleShape → daire şeklinde arka plan
                    Box(
                        modifier         = Modifier
                            .size(64.dp)
                            .background(Color.White.copy(alpha = 0.20f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = initial,
                            fontSize   = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(userName,  fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        // isNotBlank() → boş veya sadece boşluk içeren telefon/e-posta gösterilmez
                        if (userPhone.isNotBlank()) {
                            Text(userPhone, fontSize = 14.sp, color = Color.White.copy(alpha = 0.85f))
                        }
                        if (userEmail.isNotBlank()) {
                            Text(userEmail, fontSize = 12.sp, color = Color.White.copy(alpha = 0.70f))
                        }
                    }
                }
            }

            Text("Destek", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

            // ── Destek menüsü ─────────────────────────────────────────
            // Tek Card içinde birden fazla satır + HorizontalDivider ile ayrılır
            // Bu destek butonları demo amaçlı, gerçek işlev bağlanmadı
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

            // ── Çıkış Yap butonu ─────────────────────────────────────
            // AppState.logout() → kullanıcı verilerine dokunmaz,
            // sadece aktif oturum referansları sıfırlanır ve SharedPreferences güncellenir.
            // onLogout lambda'sı MainScreen üzerinden Login ekranına yönlendirir.
            Button(
                onClick  = onLogout,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                shape    = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Çıkış Yap", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }
    }
}

// ── ProfileMenuItem ───────────────────────────────────────────────────────────
// Destek menüsündeki her satır: ikon + metin + sağ ok ikonu
// weight(1f) → metin mevcut alanı doldurur, sağ ok sağa yaslanır
@Composable
private fun ProfileMenuItem(icon: ImageVector, text: String) {
    Row(
        modifier          = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, fontSize = 15.sp, modifier = Modifier.weight(1f))
        // ChevronRight → "bu satır tıklanabilir" mesajı verir kullanıcıya
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextSecondary)
    }
}
