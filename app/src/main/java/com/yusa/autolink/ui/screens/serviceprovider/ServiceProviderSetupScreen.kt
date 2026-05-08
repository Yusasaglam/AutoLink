package com.yusa.autolink.ui.screens.serviceprovider

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.yusa.autolink.data.model.BusinessProfile
import com.yusa.autolink.ui.components.AutoLinkButton
import com.yusa.autolink.ui.components.AutoLinkTextField
import com.yusa.autolink.ui.theme.*

private val allServices = listOf(
    "Motor Bakım", "Fren Sistemi", "Yağ Değişimi", "Lastik Servisi",
    "Elektronik", "Klima", "Boya & Kaporta", "Cam Değişimi",
    "Şanzıman", "Egzoz", "Süspansiyon", "Rot-Balans"
)

@Composable
fun ServiceProviderSetupScreen(
    ownerName: String,
    ownerPhone: String,
    onSetupComplete: (BusinessProfile) -> Unit
) {
    var businessName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf(ownerPhone) }
    var selectedServices by remember { mutableStateOf<Set<String>>(emptySet()) }
    var isAuthorized by remember { mutableStateOf(false) }

    val isValid = businessName.isNotBlank() && address.isNotBlank() &&
            phone.isNotBlank() && selectedServices.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(64.dp))

        Text(
            "İşletme Bilgileri",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Servis noktanızın detaylarını girin",
            style = MaterialTheme.typography.bodyMedium,
            color = TextHint
        )

        Spacer(Modifier.height(32.dp))

        AutoLinkTextField(
            value = businessName,
            onValueChange = { businessName = it },
            label = "İşletme / Firma Adı",
            leadingIcon = Icons.Default.Business
        )
        Spacer(Modifier.height(16.dp))
        AutoLinkTextField(
            value = address,
            onValueChange = { address = it },
            label = "Adres",
            leadingIcon = Icons.Default.LocationOn
        )
        Spacer(Modifier.height(16.dp))
        AutoLinkTextField(
            value = phone,
            onValueChange = { phone = it },
            label = "İşletme Telefonu",
            leadingIcon = Icons.Default.Phone,
            keyboardType = KeyboardType.Phone
        )

        Spacer(Modifier.height(24.dp))

        Text(
            "Sunulan Hizmetler",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "En az bir hizmet seçin",
            style = MaterialTheme.typography.bodySmall,
            color = TextHint
        )
        Spacer(Modifier.height(12.dp))

        allServices.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { service ->
                    val isSelected = service in selectedServices
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Blue500 else NavyContainer)
                            .border(
                                1.dp,
                                if (isSelected) Blue500 else DividerColor,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                selectedServices = if (isSelected)
                                    selectedServices - service
                                else
                                    selectedServices + service
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            service,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) TextPrimary else TextSecondary
                        )
                    }
                }
                repeat(3 - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(NavySurface)
                .border(1.dp, DividerColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Yetkili Servis",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary
                )
                Text(
                    "Resmi yetkili servis noktasıyım",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextHint
                )
            }
            Switch(
                checked = isAuthorized,
                onCheckedChange = { isAuthorized = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = TextPrimary,
                    checkedTrackColor = Blue500,
                    uncheckedThumbColor = TextHint,
                    uncheckedTrackColor = NavyContainerHigh
                )
            )
        }

        Spacer(Modifier.height(32.dp))

        AutoLinkButton(
            text = "İşletmeyi Kaydet",
            onClick = {
                onSetupComplete(
                    BusinessProfile(
                        id = System.currentTimeMillis().toString(),
                        businessName = businessName.trim(),
                        ownerName = ownerName,
                        address = address.trim(),
                        phone = phone.trim(),
                        services = selectedServices.toList(),
                        isAuthorized = isAuthorized
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isValid
        )

        Spacer(Modifier.height(32.dp))
    }
}
