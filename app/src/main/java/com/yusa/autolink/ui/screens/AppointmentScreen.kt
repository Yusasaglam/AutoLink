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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yusa.autolink.data.AppState
import com.yusa.autolink.data.DemoData
import com.yusa.autolink.ui.components.PrimaryButton
import com.yusa.autolink.ui.theme.*

// Randevu oluşturma ekranı.
// serviceType: "washing" → "Araba Yıkama", "maintenance" → "Oto Bakım"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentScreen(
    businessId: Int,
    serviceType: String,          // "washing" veya "maintenance"
    onNavigateToSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val business    = DemoData.businesses.find { it.id == businessId } ?: return
    val serviceName = if (serviceType == "washing") "Araba Yıkama" else "Oto Bakım"
    val vehicle     = DemoData.userVehicle

    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }

    val isFormReady = selectedDate.isNotEmpty() && selectedTime.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Randevu Al") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // İşletme, hizmet ve araç özet kartı
            AppointmentSummaryCard(
                businessName = business.name,
                serviceName  = serviceName,
                vehicleName  = "${vehicle.brand} ${vehicle.model} ${vehicle.year}"
            )

            Text("Tarih Seçin", fontSize = 16.sp, fontWeight = FontWeight.Bold)

            // Tarih seçenekleri yatay liste
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(DemoData.availableDates) { date ->
                    SelectableChip(
                        text       = date,
                        isSelected = selectedDate == date,
                        onClick    = { selectedDate = date }
                    )
                }
            }

            Text("Saat Seçin", fontSize = 16.sp, fontWeight = FontWeight.Bold)

            // Saatler 4'lü satırlar halinde grid görünümü
            val timeRows = DemoData.availableTimes.chunked(4)
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

            // Fiyat özeti - sadece tarih ve saat seçilince göster
            if (isFormReady) {
                NetPriceSummary(serviceName = serviceName, price = business.startingPrice)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Onayla butonu - form tamamlanmadan aktif olmaz
            PrimaryButton(
                text    = "Randevuyu Onayla",
                enabled = isFormReady,
                onClick = {
                    // Başarı ekranı bu verileri AppState'ten okuyacak
                    AppState.lastBusinessName = business.name
                    AppState.lastServiceName  = serviceName
                    AppState.lastDate         = selectedDate
                    AppState.lastTime         = selectedTime
                    AppState.lastPrice        = business.startingPrice
                    onNavigateToSuccess()
                }
            )
        }
    }
}

@Composable
private fun AppointmentSummaryCard(businessName: String, serviceName: String, vehicleName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Randevu Özeti", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
            SummaryRow("İşletme", businessName)
            SummaryRow("Hizmet",  serviceName)
            SummaryRow("Araç",    vehicleName)
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
            .border(
                width = 1.dp,
                color = if (isSelected) PrimaryBlue else CardBorder,
                shape = RoundedCornerShape(10.dp)
            )
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

@Composable
private fun NetPriceSummary(serviceName: String, price: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.06f))
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column {
                Text("Net Hizmet Bedeli", fontSize = 13.sp, color = TextSecondary)
                Text(serviceName, fontSize = 12.sp, color = TextSecondary)
            }
            Text(text = "₺$price", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
        }
    }
}
