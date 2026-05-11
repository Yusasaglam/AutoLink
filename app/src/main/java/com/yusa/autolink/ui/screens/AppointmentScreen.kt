package com.yusa.autolink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yusa.autolink.data.AppState
import com.yusa.autolink.data.CAR_BRAND_NAMES
import com.yusa.autolink.data.modelsFor
import com.yusa.autolink.data.model.AppointmentStatus
import com.yusa.autolink.data.model.BusinessService
import com.yusa.autolink.data.model.BusinessType
import com.yusa.autolink.data.model.Vehicle
import com.yusa.autolink.ui.theme.*

// ============================================================
// AppointmentScreen — Randevu oluşturma ekranı
//
// BusinessListScreen'den businessId + serviceType parametresiyle açılır.
// Kullanıcı sırayla şunları seçer:
//   1. Hizmet (BusinessService listesinden)
//   2. Araç (kayıtlı araçlardan veya manuel giriş)
//   3. Teslimat tipi: Ben götüreceğim / Vale / Yerinde hizmet
//   4. Tarih (AVAILABLE_DATES listesi)
//   5. Saat  (AVAILABLE_TIMES listesi — dolu saatler "Dolu" gösterilir)
//
// Tüm seçimler tamamlanınca "Randevuyu Onayla" butonu aktifleşir.
// Onayda AppState.addAppointment() çağrılır, AppointmentSuccess'e geçilir.
//
// Dolu saat hesabı:
//   bookedTimes → seçili tarihte aynı işletmeye alınmış aktif randevuların saatleri
//   remember(selectedDate) → tarih değişince yeniden hesaplanır
// ============================================================

// Yakıt tipleri — hem kayıtlı araç seçiminde hem manuel girişte kullanılır
private val FUEL_TYPES = listOf("Benzin", "Dizel", "LPG", "Elektrik", "Hibrit")

// Randevu için seçilebilir tarihler (demo — gerçekte işletme takviminden gelirdi)
private val AVAILABLE_DATES = listOf(
    "12 Mayıs 2026", "13 Mayıs 2026", "14 Mayıs 2026",
    "15 Mayıs 2026", "16 Mayıs 2026", "19 Mayıs 2026", "20 Mayıs 2026"
)
// Seçilebilir saatler — 13:00 öncesi öğleden önce, sonrası öğleden sonra
// "chunked(4)" ile 4'lü satırlara bölünerek grid şeklinde gösterilir
private val AVAILABLE_TIMES = listOf(
    "09:00", "09:30", "10:00", "10:30",
    "11:00", "11:30", "13:00", "13:30",
    "14:00", "14:30", "15:00", "15:30"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentScreen(
    businessId: Int,
    serviceType: String,
    onNavigateToSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val business = AppState.userCreatedBusinesses.find { it.id == businessId } ?: return

    val isWashing   = business.type == BusinessType.WASHING
    val accentColor = if (isWashing) PrimaryBlue else SuccessGreen
    val services    = business.services

    var selectedService by remember { mutableStateOf<BusinessService?>(services.firstOrNull()) }

    // Araç seçimi — kayıtlı araçlardan seç veya manuel gir
    val savedVehicles = AppState.userVehicles.toList()
    var selectedVehicle  by remember { mutableStateOf<Vehicle?>(savedVehicles.firstOrNull()) }
    var showManualEntry  by remember { mutableStateOf(savedVehicles.isEmpty()) }

    // Manuel giriş alanları — seçili araç değişince otomatik dolar
    var vehicleBrand    by remember { mutableStateOf(selectedVehicle?.brand    ?: "") }
    var vehicleModel    by remember { mutableStateOf(selectedVehicle?.model    ?: "") }
    var vehicleYear     by remember { mutableStateOf(selectedVehicle?.year?.toString() ?: "") }
    var vehicleFuelType by remember { mutableStateOf(selectedVehicle?.fuelType ?: FUEL_TYPES[0]) }

    var brandExpanded by remember { mutableStateOf(false) }
    var modelExpanded by remember { mutableStateOf(false) }
    var fuelDropdown  by remember { mutableStateOf(false) }

    val availableModels = modelsFor(vehicleBrand)

    // Hizmet türü seçimi: "self" | "valet" | "onsite"
    var deliveryType    by remember { mutableStateOf("self") }
    var deliveryAddress by remember { mutableStateOf("") }

    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }

    // Seçili tarihte bu işletmede dolu olan saatler.
    // allAppointments bir SnapshotStateList olduğundan Compose değişiklikleri izler
    // ve yeni randevu eklenince ya da tarih değişince otomatik güncellenir.
    val bookedTimes = AppState.allAppointments
        .filter { appt ->
            appt.businessName == business.name &&
            appt.date         == selectedDate   &&
            appt.status != AppointmentStatus.CANCELLED
        }
        .map { it.time }
        .toSet()

    val vehicleReady   = vehicleBrand.isNotBlank() && vehicleModel.isNotBlank()
    val deliveryReady  = deliveryType == "self" || deliveryAddress.isNotBlank()
    val isFormReady    = vehicleReady && selectedDate.isNotEmpty() && selectedTime.isNotEmpty() &&
                         selectedService != null && deliveryReady
    val totalPrice   = selectedService?.price ?: 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Randevu Al") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Randevu özeti kartı
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp),
                colors   = CardDefaults.cardColors(containerColor = SurfaceWhite)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Randevu Özeti", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    SummaryRow("İşletme", business.name)
                    SummaryRow("Seçilen Hizmet", selectedService?.name ?: "—")
                    if (business.address.isNotBlank() && business.address != "Adres belirtilmedi") {
                        SummaryRow("Adres", business.address)
                    }
                }
            }

            // Hizmet seçimi
            Text("Hizmet Seçin", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            if (services.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Text(
                        "Bu işletme henüz hizmet tanımlamamıştır.",
                        fontSize  = 13.sp,
                        color     = TextSecondary,
                        modifier  = Modifier.padding(16.dp)
                    )
                }
            } else {
                services.forEach { svc ->
                    val isSelected = svc.id == selectedService?.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedService = svc }
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) accentColor else CardBorder,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        shape     = RoundedCornerShape(12.dp),
                        colors    = CardDefaults.cardColors(
                            containerColor = if (isSelected) accentColor.copy(alpha = 0.05f) else SurfaceWhite
                        ),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier              = Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text(
                                text       = svc.name,
                                fontSize   = 14.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color      = if (isSelected) accentColor else TextPrimary
                            )
                            Text(
                                text       = "₺${svc.price}",
                                fontSize   = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color      = if (isSelected) accentColor else TextSecondary
                            )
                        }
                    }
                }
            }

            // ── ARAÇ SEÇİMİ ──────────────────────────────────────────────
            Text("Araç Seçin", fontSize = 16.sp, fontWeight = FontWeight.Bold)

            if (savedVehicles.isNotEmpty()) {
                // Kayıtlı araçları listele
                savedVehicles.forEach { vehicle ->
                    val isSelected = vehicle.id == selectedVehicle?.id && !showManualEntry
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedVehicle  = vehicle
                                vehicleBrand     = vehicle.brand
                                vehicleModel     = vehicle.model
                                vehicleYear      = vehicle.year.toString()
                                vehicleFuelType  = vehicle.fuelType
                                showManualEntry  = false
                            }
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) accentColor else CardBorder,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        shape     = RoundedCornerShape(12.dp),
                        colors    = CardDefaults.cardColors(
                            containerColor = if (isSelected) accentColor.copy(alpha = 0.05f) else SurfaceWhite
                        ),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier          = Modifier.fillMaxWidth().padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.DirectionsCar,
                                contentDescription = null,
                                tint     = if (isSelected) accentColor else TextSecondary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${vehicle.brand} ${vehicle.model}",
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    fontSize   = 14.sp,
                                    color      = if (isSelected) accentColor else TextPrimary
                                )
                                Text(
                                    buildString {
                                        append("${vehicle.year} · ${vehicle.fuelType}")
                                        if (vehicle.engine.isNotBlank()) append(" · ${vehicle.engine}")
                                    },
                                    fontSize = 12.sp,
                                    color    = TextSecondary
                                )
                            }
                        }
                    }
                }

                // Farklı araç gir seçeneği
                val isManualSelected = showManualEntry
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showManualEntry = true
                            selectedVehicle = null
                            vehicleBrand    = ""
                            vehicleModel    = ""
                            vehicleYear     = ""
                            vehicleFuelType = FUEL_TYPES[0]
                        }
                        .border(
                            width = if (isManualSelected) 2.dp else 1.dp,
                            color = if (isManualSelected) accentColor else CardBorder,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    shape     = RoundedCornerShape(12.dp),
                    colors    = CardDefaults.cardColors(
                        containerColor = if (isManualSelected) accentColor.copy(alpha = 0.05f) else SurfaceWhite
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier          = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.DirectionsCar,
                            contentDescription = null,
                            tint     = if (isManualSelected) accentColor else TextSecondary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Farklı Araç Gir",
                            fontWeight = if (isManualSelected) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize   = 14.sp,
                            color      = if (isManualSelected) accentColor else TextSecondary
                        )
                    }
                }
            }

            // Manuel araç giriş formu — araç yoksa veya "Farklı Araç Gir" seçildiyse göster
            if (showManualEntry) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(16.dp),
                    colors   = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Column(
                        modifier            = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Marka dropdown
                        ExposedDropdownMenuBox(
                            expanded         = brandExpanded,
                            onExpandedChange = { brandExpanded = !brandExpanded }
                        ) {
                            OutlinedTextField(
                                value         = vehicleBrand,
                                onValueChange = {
                                    vehicleBrand  = it
                                    vehicleModel  = ""
                                    brandExpanded = true
                                },
                                label        = { Text("Marka") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandExpanded) },
                                singleLine   = true,
                                shape        = RoundedCornerShape(12.dp),
                                modifier     = Modifier.fillMaxWidth().menuAnchor(),
                                colors       = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor   = PrimaryBlue,
                                    unfocusedBorderColor = CardBorder
                                )
                            )
                            val filteredBrands = CAR_BRAND_NAMES.filter {
                                vehicleBrand.isBlank() || it.contains(vehicleBrand, ignoreCase = true)
                            }
                            if (filteredBrands.isNotEmpty()) {
                                ExposedDropdownMenu(
                                    expanded         = brandExpanded,
                                    onDismissRequest = { brandExpanded = false }
                                ) {
                                    filteredBrands.forEach { b ->
                                        DropdownMenuItem(
                                            text    = { Text(b) },
                                            onClick = {
                                                vehicleBrand  = b
                                                vehicleModel  = ""
                                                brandExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Model dropdown — markaya göre filtrelenir
                        ExposedDropdownMenuBox(
                            expanded         = modelExpanded,
                            onExpandedChange = { if (availableModels.isNotEmpty() || vehicleModel.isNotBlank()) modelExpanded = !modelExpanded }
                        ) {
                            OutlinedTextField(
                                value         = vehicleModel,
                                onValueChange = { vehicleModel = it; modelExpanded = true },
                                label        = { Text("Model") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modelExpanded) },
                                singleLine   = true,
                                shape        = RoundedCornerShape(12.dp),
                                modifier     = Modifier.fillMaxWidth().menuAnchor(),
                                colors       = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor   = PrimaryBlue,
                                    unfocusedBorderColor = CardBorder
                                )
                            )
                            val filteredModels = availableModels.filter {
                                vehicleModel.isBlank() || it.contains(vehicleModel, ignoreCase = true)
                            }
                            if (filteredModels.isNotEmpty()) {
                                ExposedDropdownMenu(
                                    expanded         = modelExpanded,
                                    onDismissRequest = { modelExpanded = false }
                                ) {
                                    filteredModels.forEach { m ->
                                        DropdownMenuItem(
                                            text    = { Text(m) },
                                            onClick = { vehicleModel = m; modelExpanded = false }
                                        )
                                    }
                                }
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value         = vehicleYear,
                                onValueChange = { vehicleYear = it },
                                label         = { Text("Yıl") },
                                singleLine    = true,
                                shape         = RoundedCornerShape(12.dp),
                                modifier      = Modifier.weight(1f),
                                colors        = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor   = PrimaryBlue,
                                    unfocusedBorderColor = CardBorder
                                )
                            )
                            // Yakıt tipi dropdown
                            ExposedDropdownMenuBox(
                                expanded         = fuelDropdown,
                                onExpandedChange = { fuelDropdown = !fuelDropdown },
                                modifier         = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value         = vehicleFuelType,
                                    onValueChange = {},
                                    readOnly      = true,
                                    label         = { Text("Yakıt") },
                                    trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fuelDropdown) },
                                    singleLine    = true,
                                    shape         = RoundedCornerShape(12.dp),
                                    modifier      = Modifier.fillMaxWidth().menuAnchor(),
                                    colors        = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor   = PrimaryBlue,
                                        unfocusedBorderColor = CardBorder
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded         = fuelDropdown,
                                    onDismissRequest = { fuelDropdown = false }
                                ) {
                                    FUEL_TYPES.forEach { type ->
                                        DropdownMenuItem(
                                            text    = { Text(type) },
                                            onClick = { vehicleFuelType = type; fuelDropdown = false }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── TESLİMAT TİPİ SEÇİMİ ────────────────────────────────────────
            val showValet  = business.hasValet
            val showOnSite = business.onSiteService
            if (showValet || showOnSite) {
                Text("Nasıl Hizmet Almak İstiyorsunuz?", fontSize = 16.sp, fontWeight = FontWeight.Bold)

                // "Ben Götüreceğim" — her zaman var
                DeliveryOptionCard(
                    icon        = Icons.Filled.DirectionsCar,
                    title       = "Ben Götüreceğim",
                    subtitle    = "Aracınızı işletmeye kendiniz teslim edersiniz",
                    isSelected  = deliveryType == "self",
                    accentColor = accentColor,
                    onClick     = { deliveryType = "self"; deliveryAddress = "" }
                )

                // "Vale Hizmeti" — sadece hasValet ise
                if (showValet) {
                    DeliveryOptionCard(
                        icon        = Icons.Filled.LocalShipping,
                        title       = "Vale Hizmeti",
                        subtitle    = "Vale aracınızı belirttiğiniz adresten alır",
                        isSelected  = deliveryType == "valet",
                        accentColor = accentColor,
                        onClick     = { deliveryType = "valet"; deliveryAddress = "" }
                    )
                }

                // "Yerinde Hizmet" — sadece onSiteService ise
                if (showOnSite) {
                    DeliveryOptionCard(
                        icon        = Icons.Filled.Home,
                        title       = "Yerinde Hizmet",
                        subtitle    = "Ekip aracınıza belirttiğiniz adrese gelir",
                        isSelected  = deliveryType == "onsite",
                        accentColor = accentColor,
                        onClick     = { deliveryType = "onsite"; deliveryAddress = "" }
                    )
                }

                // Adres girişi — vale veya yerinde seçildiyse
                if (deliveryType == "valet" || deliveryType == "onsite") {
                    val label = if (deliveryType == "valet") "Araç Teslim Adresi" else "Hizmet Adresi"
                    OutlinedTextField(
                        value         = deliveryAddress,
                        onValueChange = { deliveryAddress = it },
                        label         = { Text(label) },
                        placeholder   = { Text("Cadde, Mahalle, İlçe…", color = TextSecondary) },
                        singleLine    = true,
                        shape         = RoundedCornerShape(12.dp),
                        modifier      = Modifier.fillMaxWidth(),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = accentColor,
                            unfocusedBorderColor = CardBorder
                        )
                    )
                }
            }

            // Tarih seçimi
            Text("Tarih Seçin", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(AVAILABLE_DATES) { date ->
                    SelectableChip(
                        text       = date,
                        isSelected = selectedDate == date,
                        onClick    = {
                            selectedDate = date
                            selectedTime = ""
                        }
                    )
                }
            }

            // Saat seçimi
            Text("Saat Seçin", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            val timeRows = AVAILABLE_TIMES.chunked(4)
            timeRows.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { time ->
                        val isBooked = time in bookedTimes
                        if (isBooked) {
                            BookedChip(modifier = Modifier.weight(1f))
                        } else {
                            SelectableChip(
                                text       = time,
                                isSelected = selectedTime == time,
                                onClick    = { selectedTime = time },
                                modifier   = Modifier.weight(1f)
                            )
                        }
                    }
                    repeat(4 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
                }
            }

            // Fiyat özeti
            if (isFormReady) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(16.dp),
                    colors   = CardDefaults.cardColors(containerColor = accentColor.copy(alpha = 0.06f))
                ) {
                    Row(
                        modifier              = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Net Hizmet Bedeli", fontSize = 13.sp, color = TextSecondary)
                            Text(selectedService?.name ?: "", fontSize = 12.sp, color = TextSecondary)
                        }
                        Text("₺$totalPrice", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = accentColor)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (!isFormReady) return@Button
                    val vehicleName = buildString {
                        append("$vehicleBrand $vehicleModel")
                        if (vehicleYear.isNotBlank()) append(" $vehicleYear")
                        append(" ($vehicleFuelType)")
                    }
                    AppState.lastBusinessName = business.name
                    AppState.lastServiceName  = selectedService?.name ?: ""
                    AppState.lastDate         = selectedDate
                    AppState.lastTime         = selectedTime
                    AppState.lastPrice        = totalPrice
                    AppState.addAppointment(
                        businessName  = business.name,
                        serviceName   = selectedService?.name ?: "",
                        date          = selectedDate,
                        time          = selectedTime,
                        price         = totalPrice,
                        vehicleName   = vehicleName,
                        hasValet      = deliveryType == "valet",
                        valetAddress  = if (deliveryType == "valet") deliveryAddress else "",
                        isOnSite      = deliveryType == "onsite",
                        onSiteAddress = if (deliveryType == "onsite") deliveryAddress else ""
                    )
                    onNavigateToSuccess()
                },
                enabled  = isFormReady,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape    = RoundedCornerShape(16.dp)
            ) {
                Text("Randevuyu Onayla", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ── DeliveryOptionCard ────────────────────────────────────────────────────────
// Teslimat tipi seçimi için kart (Ben götüreceğim / Vale / Yerinde hizmet)
// RadioButton + border kombinasyonu seçili kartı görsel olarak vurgular
@Composable
private fun DeliveryOptionCard(
    icon:        androidx.compose.ui.graphics.vector.ImageVector,
    title:       String,
    subtitle:    String,
    isSelected:  Boolean,
    accentColor: Color,
    onClick:     () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) accentColor else CardBorder,
                shape = RoundedCornerShape(12.dp)
            ),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(
            containerColor = if (isSelected) accentColor.copy(alpha = 0.05f) else SurfaceWhite
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint     = if (isSelected) accentColor else TextSecondary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = title,
                    fontSize   = 14.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color      = if (isSelected) accentColor else TextPrimary
                )
                Text(
                    text     = subtitle,
                    fontSize = 12.sp,
                    color    = TextSecondary
                )
            }
            RadioButton(
                selected = isSelected,
                onClick  = onClick,
                colors   = RadioButtonDefaults.colors(selectedColor = accentColor)
            )
        }
    }
}

// ── SummaryRow ────────────────────────────────────────────────────────────────
// Randevu özet kartındaki her satır: solda etiket (gri), sağda değer (siyah)
@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextSecondary, fontSize = 14.sp)
        Text(text = value, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}

// ── BookedChip ────────────────────────────────────────────────────────────────
// Dolu olan saatlerde SelectableChip yerine gösterilen gri "Dolu" etiketi
// Kullanıcı tıklayamaz, sadece bilgi amaçlı
@Composable
private fun BookedChip(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                color = TextSecondary.copy(alpha = 0.08f),
                shape = RoundedCornerShape(10.dp)
            )
            .border(1.dp, TextSecondary.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text     = "Dolu",
            fontSize = 13.sp,
            color    = TextSecondary.copy(alpha = 0.45f)
        )
    }
}

// ── SelectableChip ────────────────────────────────────────────────────────────
// Tarih ve saat seçimi için tıklanabilir chip
// isSelected → mavi arka plan + beyaz metin; değilse → beyaz arka plan + siyah metin
@Composable
private fun SelectableChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable { onClick() }
            .background(
                color = if (isSelected) PrimaryBlue else SurfaceWhite,
                shape = RoundedCornerShape(10.dp)
            )
            .border(1.dp, if (isSelected) PrimaryBlue else CardBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = text,
            fontSize   = 13.sp,
            color      = if (isSelected) Color.White else TextPrimary,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
