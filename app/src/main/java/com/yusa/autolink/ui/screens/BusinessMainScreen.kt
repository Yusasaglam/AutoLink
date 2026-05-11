package com.yusa.autolink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yusa.autolink.data.AppState
import com.yusa.autolink.data.model.Appointment
import com.yusa.autolink.data.model.AppointmentStatus
import com.yusa.autolink.data.model.Business
import com.yusa.autolink.data.model.BusinessService
import com.yusa.autolink.data.model.BusinessType
import com.yusa.autolink.ui.theme.*

// ============================================================
// BusinessMainScreen — İşletme sahibinin yönetim paneli
//
// AccountType.BUSINESS ile kayıt olan kullanıcılar bu ekrana gelir.
// 3 sekme içerir:
//   0 → AppointmentsTab  : Gelen randevuları görüntüle/onayla/reddet
//   1 → PricingTab       : Hizmet ekle/sil ve fiyat yönet
//   2 → BusinessProfileTab: İşletme bilgileri, vale/yerinde hizmet ayarları
//
// business state'i PricingTab ve ProfileTab'dan güncellenince yeniden
// okunur; böylece anlık değişiklikler panele yansır.
// ============================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessMainScreen(onNavigateToLogin: () -> Unit) {
    var business by remember {
        mutableStateOf(AppState.userCreatedBusinesses.find { it.id == AppState.currentBusinessId })
    }
    var selectedTab by remember { mutableIntStateOf(0) }

    val isWashing   = business?.type == BusinessType.WASHING
    val accentColor = if (isWashing) PrimaryBlue else SuccessGreen
    val typeLabel   = if (isWashing) "Araba Yıkama" else "Oto Bakım"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text       = business?.name ?: "İşletme Paneli",
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text  = typeLabel,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { AppState.logout(); onNavigateToLogin() }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Çıkış Yap")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick  = { selectedTab = 0 },
                    icon     = { Icon(Icons.Filled.DateRange, null) },
                    label    = { Text("Randevular") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick  = { selectedTab = 1 },
                    icon     = { Icon(Icons.Filled.Payments, null) },
                    label    = { Text("Ücretlendirme") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick  = { selectedTab = 2 },
                    icon     = { Icon(Icons.Filled.Store, null) },
                    label    = { Text("İşletmem") }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(padding)
        ) {
            when (selectedTab) {
                0 -> AppointmentsTab(business, accentColor)
                1 -> PricingTab(business, accentColor) { updatedBiz ->
                    business = updatedBiz
                }
                2 -> BusinessProfileTab(business, accentColor)
            }
        }
    }
}

// ── AppointmentsTab (Sekme 0) ─────────────────────────────────────────────────
// İşletmeye gelen tüm randevuları listeler
// filterStatus → null = tümü, değer atanınca sadece o durum gösterilir
// refreshKey → onayla/reddet/tamamla sonrası listeyi zorla günceller
@Composable
private fun AppointmentsTab(business: Business?, accentColor: Color) {
    var refreshKey by remember { mutableIntStateOf(0) }
    val businessName = business?.name ?: ""
    val allAppts = remember(refreshKey) {
        AppState.allAppointments.filter { it.businessName == businessName }
    }

    var filterStatus by remember { mutableStateOf<AppointmentStatus?>(null) }
    val displayed = if (filterStatus == null) allAppts
                    else allAppts.filter { it.status == filterStatus }

    Column(modifier = Modifier.fillMaxSize()) {
        // Filtre chips
        Row(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(null to "Tümü",
                   AppointmentStatus.PENDING   to "Beklemede",
                   AppointmentStatus.CONFIRMED to "Onaylandı",
                   AppointmentStatus.COMPLETED to "Tamamlandı").forEach { (status, label) ->
                FilterChip(
                    selected = filterStatus == status,
                    onClick  = { filterStatus = status },
                    label    = { Text(label, fontSize = 12.sp) }
                )
            }
        }

        Text(
            text     = "${displayed.size} randevu",
            fontSize = 12.sp,
            color    = TextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        if (displayed.isEmpty()) {
            EmptyAppointmentsState(
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyColumn(
                contentPadding      = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier            = Modifier.fillMaxSize()
            ) {
                items(displayed, key = { it.id }) { appt ->
                    BusinessAppointmentCard(
                        appointment = appt,
                        accentColor = accentColor,
                        onStatusChange = { newStatus ->
                            AppState.updateAppointmentStatus(appt.id, newStatus)
                            refreshKey++
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyAppointmentsState(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier            = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Filled.DateRange,
                contentDescription = null,
                tint     = TextSecondary.copy(alpha = 0.35f),
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Henüz randevu yok",
                fontSize   = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color      = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text      = "Müşteriler işletmenize randevu\naldıkça burada görüntülenecek.",
                fontSize  = 14.sp,
                color     = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun BusinessAppointmentCard(
    appointment: Appointment,
    accentColor: Color,
    onStatusChange: (AppointmentStatus) -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text       = appointment.serviceName,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary,
                    modifier   = Modifier.weight(1f)
                )
                ProviderStatusBadge(appointment.status)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.DirectionsCar, null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(appointment.vehicleName, fontSize = 12.sp, color = TextSecondary)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Schedule, null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("${appointment.date} · ${appointment.time}", fontSize = 12.sp, color = TextSecondary)
            }

            if (appointment.hasValet) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocalShipping, null, tint = accentColor, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text       = "Vale: ${appointment.valetAddress}",
                        fontSize   = 12.sp,
                        color      = accentColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            if (appointment.isOnSite) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Home, null, tint = accentColor, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text       = "Yerinde: ${appointment.onSiteAddress}",
                        fontSize   = 12.sp,
                        color      = accentColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    "₺${appointment.totalPrice}",
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color      = accentColor
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    when (appointment.status) {
                        AppointmentStatus.PENDING -> {
                            OutlinedButton(
                                onClick = { onStatusChange(AppointmentStatus.CANCELLED) },
                                shape   = RoundedCornerShape(10.dp),
                                colors  = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) { Text("Reddet", fontSize = 12.sp) }
                            Button(
                                onClick = { onStatusChange(AppointmentStatus.CONFIRMED) },
                                shape   = RoundedCornerShape(10.dp),
                                colors  = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) { Text("Onayla", fontSize = 12.sp) }
                        }
                        AppointmentStatus.CONFIRMED -> {
                            Button(
                                onClick = { onStatusChange(AppointmentStatus.COMPLETED) },
                                shape   = RoundedCornerShape(10.dp),
                                colors  = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) { Text("Tamamlandı", fontSize = 12.sp) }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
private fun ProviderStatusBadge(status: AppointmentStatus) {
    val (text, bg, fg) = when (status) {
        AppointmentStatus.PENDING   -> Triple("Beklemede",   Color(0xFFF57F17).copy(alpha = 0.12f), Color(0xFFF57F17))
        AppointmentStatus.CONFIRMED -> Triple("Onaylandı",   Color(0xFF1B5E20).copy(alpha = 0.10f), Color(0xFF2E7D32))
        AppointmentStatus.COMPLETED -> Triple("Tamamlandı",  Color(0xFF1565C0).copy(alpha = 0.10f), Color(0xFF1565C0))
        AppointmentStatus.CANCELLED -> Triple("Reddedildi",  Color(0xFFD32F2F).copy(alpha = 0.10f), Color(0xFFD32F2F))
    }
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = fg)
    }
}

// ── PricingTab (Sekme 1) ──────────────────────────────────────────────────────
// İşletmenin hizmet listesini yönetir
// Mevcut hizmetler silinebilir; yeni hizmet adı+fiyatla eklenir
// AppState.addBusinessService() → startingPrice otomatik güncellenir (en düşük fiyat)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PricingTab(
    business: Business?,
    accentColor: Color,
    onBusinessUpdated: (Business) -> Unit
) {
    var refreshKey   by remember { mutableIntStateOf(0) }
    val services = remember(refreshKey) {
        AppState.userCreatedBusinesses
            .find { it.id == AppState.currentBusinessId }?.services?.toList() ?: emptyList()
    }

    var newName  by remember { mutableStateOf("") }
    var newPrice by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf("") }

    Column(
        modifier            = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Hizmetlerim & Fiyatlar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

        // Mevcut hizmetler listesi
        if (services.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp),
                colors   = CardDefaults.cardColors(containerColor = SurfaceWhite)
            ) {
                Box(
                    modifier          = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment  = Alignment.Center
                ) {
                    Text(
                        "Henüz hizmet eklemediniz.\nAşağıdan yeni hizmet ekleyin.",
                        fontSize  = 14.sp,
                        color     = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp),
                colors   = CardDefaults.cardColors(containerColor = SurfaceWhite)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    services.forEachIndexed { index, svc ->
                        Row(
                            modifier              = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(svc.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            }
                            Text(
                                "₺${svc.price}",
                                fontSize   = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color      = accentColor
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick  = {
                                    AppState.removeBusinessService(svc.id)
                                    val upd = AppState.userCreatedBusinesses.find { it.id == AppState.currentBusinessId }
                                    if (upd != null) onBusinessUpdated(upd)
                                    refreshKey++
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Filled.Delete, null, tint = Color(0xFFD32F2F), modifier = Modifier.size(18.dp))
                            }
                        }
                        if (index < services.lastIndex) HorizontalDivider(color = CardBorder)
                    }
                }
            }
        }

        // Yeni hizmet ekleme formu
        Text("Yeni Hizmet Ekle", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(16.dp),
            colors   = CardDefaults.cardColors(containerColor = SurfaceWhite)
        ) {
            Column(
                modifier            = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value         = newName,
                    onValueChange = { newName = it; formError = "" },
                    label         = { Text("Hizmet Adı  (ör. Dış Yıkama, Yağ Değişimi)") },
                    singleLine    = true,
                    shape         = RoundedCornerShape(12.dp),
                    modifier      = Modifier.fillMaxWidth(),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = accentColor,
                        unfocusedBorderColor = CardBorder
                    )
                )
                OutlinedTextField(
                    value           = newPrice,
                    onValueChange   = { newPrice = it; formError = "" },
                    label           = { Text("Ücret (₺)") },
                    singleLine      = true,
                    shape           = RoundedCornerShape(12.dp),
                    modifier        = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors          = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = accentColor,
                        unfocusedBorderColor = CardBorder
                    )
                )
                if (formError.isNotEmpty()) {
                    Text(formError, fontSize = 12.sp, color = Color(0xFFD32F2F))
                }
                Button(
                    onClick = {
                        when {
                            newName.isBlank()            -> formError = "Hizmet adı boş olamaz."
                            newPrice.toIntOrNull() == null || newPrice.toInt() <= 0
                                                         -> formError = "Geçerli bir ücret girin."
                            else -> {
                                AppState.addBusinessService(newName.trim(), newPrice.toInt())
                                val upd = AppState.userCreatedBusinesses.find { it.id == AppState.currentBusinessId }
                                if (upd != null) onBusinessUpdated(upd)
                                newName  = ""
                                newPrice = ""
                                refreshKey++
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors   = ButtonDefaults.buttonColors(containerColor = accentColor),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Hizmet Ekle", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ── BusinessProfileTab (Sekme 2) ──────────────────────────────────────────────
// İşletmenin profil bilgilerini ve hizmet ayarlarını gösterir
// Vale ve Yerinde Hizmet Switch'leri AppState.setBusinessValet/setBusinessOnSite ile
// anlık olarak kaydedilir; müşteri randevu ekranı bu değerleri görür

@Composable
private fun BusinessProfileTab(business: Business?, accentColor: Color) {
    var valetEnabled by remember { mutableStateOf(business?.hasValet == true) }

    Column(
        modifier            = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // İşletme bilgi kartı
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(16.dp),
            colors   = CardDefaults.cardColors(containerColor = accentColor)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Store, null, tint = Color.White, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            business?.name ?: "—",
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color.White
                        )
                        Text(
                            if (business?.type == BusinessType.WASHING) "Araba Yıkama" else "Oto Bakım",
                            fontSize = 13.sp,
                            color    = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }

        // Detay bilgiler
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(16.dp),
            colors   = CardDefaults.cardColors(containerColor = SurfaceWhite)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ProfileInfoRow(Icons.Filled.Star,       "Puan",    "${business?.rating ?: "—"} / 5.0")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = CardBorder)
                ProfileInfoRow(Icons.Filled.LocationOn, "Adres",   business?.address ?: "—")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = CardBorder)
                ProfileInfoRow(Icons.Filled.Phone,      "Telefon", business?.phone?.ifBlank { "—" } ?: "—")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = CardBorder)
                ProfileInfoRow(
                    Icons.Filled.VerifiedUser,
                    "Durum",
                    if (business?.isVerified == true) "Onaylı İşletme" else "Onay Bekliyor"
                )
            }
        }

        // Vale hizmeti ayarı — işletme sahibi buradan açıp kapatır
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(16.dp),
            colors   = CardDefaults.cardColors(containerColor = SurfaceWhite)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Hizmet Ayarları", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.LocalShipping,
                                contentDescription = null,
                                tint     = if (valetEnabled) accentColor else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Vale Hizmeti",
                                fontSize   = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color      = TextPrimary
                            )
                        }
                        Text(
                            if (valetEnabled) "Aktif — müşteriler vale talep edebilir"
                            else              "Pasif — müşterilere vale seçeneği çıkmaz",
                            fontSize = 12.sp,
                            color    = TextSecondary
                        )
                    }
                    Switch(
                        checked         = valetEnabled,
                        onCheckedChange = {
                            valetEnabled = it
                            AppState.setBusinessValet(it)
                        },
                        colors = SwitchDefaults.colors(checkedTrackColor = accentColor)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CardBorder)

                var onSiteEnabled by remember { mutableStateOf(business?.onSiteService == true) }
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Home,
                                contentDescription = null,
                                tint     = if (onSiteEnabled) accentColor else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Yerinde Hizmet",
                                fontSize   = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color      = TextPrimary
                            )
                        }
                        Text(
                            if (onSiteEnabled) "Aktif — ekip müşterinin adresine gider"
                            else              "Pasif — yerinde hizmet seçeneği çıkmaz",
                            fontSize = 12.sp,
                            color    = TextSecondary
                        )
                    }
                    Switch(
                        checked         = onSiteEnabled,
                        onCheckedChange = {
                            onSiteEnabled = it
                            AppState.setBusinessOnSite(it)
                        },
                        colors = SwitchDefaults.colors(checkedTrackColor = accentColor)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, null, tint = TextSecondary, modifier = Modifier.size(18.dp).padding(top = 1.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(label, fontSize = 11.sp, color = TextSecondary)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
        }
    }
}
