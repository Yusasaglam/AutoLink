package com.yusa.autolink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yusa.autolink.data.DemoData
import com.yusa.autolink.data.model.Appointment
import com.yusa.autolink.data.model.AppointmentStatus
import com.yusa.autolink.ui.theme.*

// Randevularım ekranı - tüm randevular tarih sırasıyla listelenir
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppointmentsScreen() {
    val appointments = DemoData.appointments

    Scaffold(
        topBar = {
            TopAppBar(
                title  = { Text("Randevularım") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier       = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(appointments) { appointment ->
                AppointmentCard(appointment = appointment)
            }
        }
    }
}

// Randevu kartı - tüm detaylar ve sağ üstte durum rozeti
@Composable
private fun AppointmentCard(appointment: Appointment) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Satır 1: İşletme adı + durum rozeti
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text       = appointment.businessName,
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary,
                    modifier   = Modifier.weight(1f)
                )
                StatusBadge(status = appointment.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Hizmet türü, araç ve tarih/saat
            Text(appointment.serviceName, fontSize = 13.sp, color = TextSecondary)
            Text(appointment.vehicleName, fontSize = 13.sp, color = TextSecondary)
            Text(
                "${appointment.date} · ${appointment.time}",
                fontSize = 12.sp,
                color    = TextSecondary
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            // Toplam ücret
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Toplam Ücret", fontSize = 14.sp, color = TextSecondary)
                Text(
                    "₺${appointment.totalPrice}",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color      = SuccessGreen
                )
            }
        }
    }
}

// Durum rozeti - renk ve metin duruma göre değişir
@Composable
private fun StatusBadge(status: AppointmentStatus) {
    val (text, bgColor, textColor) = when (status) {
        AppointmentStatus.CONFIRMED -> Triple("Onaylandı",  Color(0xFF1B5E20).copy(alpha = 0.10f), Color(0xFF2E7D32))
        AppointmentStatus.PENDING   -> Triple("Beklemede",  Color(0xFFF57F17).copy(alpha = 0.12f), Color(0xFFF57F17))
        AppointmentStatus.COMPLETED -> Triple("Tamamlandı", Color(0xFF1565C0).copy(alpha = 0.10f), Color(0xFF1565C0))
    }

    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = textColor)
    }
}
