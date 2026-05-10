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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yusa.autolink.data.AppState
import com.yusa.autolink.data.model.BusinessService
import com.yusa.autolink.data.model.BusinessType
import com.yusa.autolink.ui.theme.*

private val FUEL_TYPES = listOf("Benzin", "Dizel", "LPG", "Elektrik", "Hibrit")

private val AVAILABLE_DATES = listOf(
    "12 Mayıs 2026", "13 Mayıs 2026", "14 Mayıs 2026",
    "15 Mayıs 2026", "16 Mayıs 2026", "19 Mayıs 2026", "20 Mayıs 2026"
)
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

    val firstVehicle = AppState.userVehicles.firstOrNull()
    var vehicleBrand    by remember { mutableStateOf(firstVehicle?.brand    ?: "") }
    var vehicleModel    by remember { mutableStateOf(firstVehicle?.model    ?: "") }
    var vehicleYear     by remember { mutableStateOf(firstVehicle?.year?.toString() ?: "") }
    var vehicleFuelType by remember { mutableStateOf(firstVehicle?.fuelType ?: FUEL_TYPES[0]) }
    var fuelDropdown    by remember { mutableStateOf(false) }

    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }

    val vehicleReady = vehicleBrand.isNotBlank() && vehicleModel.isNotBlank()
    val isFormReady  = vehicleReady && selectedDate.isNotEmpty() && selectedTime.isNotEmpty() &&
                       selectedService != null
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
            // İşletme bilgi kartı
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

            // Araç bilgileri bölümü
            Text("Araç Bilgileri", fontSize = 16.sp, fontWeight = FontWeight.Bold)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp),
                colors   = CardDefaults.cardColors(containerColor = SurfaceWhite)
            ) {
                Column(
                    modifier            = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        VehicleTextField(
                            label         = "Marka",
                            value         = vehicleBrand,
                            onValueChange = { vehicleBrand = it },
                            modifier      = Modifier.weight(1f)
                        )
                        VehicleTextField(
                            label         = "Model",
                            value         = vehicleModel,
                            onValueChange = { vehicleModel = it },
                            modifier      = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        VehicleTextField(
                            label         = "Yıl",
                            value         = vehicleYear,
                            onValueChange = { vehicleYear = it },
                            modifier      = Modifier.weight(1f)
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

            // Tarih seçimi
            Text("Tarih Seçin", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(AVAILABLE_DATES) { date ->
                    SelectableChip(
                        text       = date,
                        isSelected = selectedDate == date,
                        onClick    = { selectedDate = date }
                    )
                }
            }

            // Saat seçimi
            Text("Saat Seçin", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            val timeRows = AVAILABLE_TIMES.chunked(4)
            timeRows.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { time ->
                        SelectableChip(
                            text       = time,
                            isSelected = selectedTime == time,
                            onClick    = { selectedTime = time },
                            modifier   = Modifier.weight(1f)
                        )
                    }
                    repeat(4 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
                }
            }

            // Fiyat özeti - tarih ve saat seçilince göster
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
                        businessName = business.name,
                        serviceName  = selectedService?.name ?: "",
                        date         = selectedDate,
                        time         = selectedTime,
                        price        = totalPrice,
                        vehicleName  = vehicleName
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

@Composable
private fun VehicleTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        label         = { Text(label) },
        singleLine    = true,
        shape         = RoundedCornerShape(12.dp),
        modifier      = modifier,
        colors        = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = PrimaryBlue,
            unfocusedBorderColor = CardBorder
        )
    )
}

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
