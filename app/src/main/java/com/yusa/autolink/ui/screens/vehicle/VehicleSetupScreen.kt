package com.yusa.autolink.ui.screens.vehicle

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
import com.yusa.autolink.data.model.Vehicle
import com.yusa.autolink.ui.components.AutoLinkButton
import com.yusa.autolink.ui.components.AutoLinkTextField
import com.yusa.autolink.ui.theme.*
import java.util.Calendar

private val popularBrands = listOf(
    "Volkswagen", "Toyota", "Ford", "Renault", "Mercedes-Benz",
    "BMW", "Audi", "Hyundai", "Peugeot", "Fiat", "Honda", "Nissan"
)

@Composable
fun VehicleSetupScreen(
    onVehicleSaved: (Vehicle) -> Unit,
    onSkip: (() -> Unit)? = null
) {
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var plate by remember { mutableStateOf("") }
    var mileage by remember { mutableStateOf("") }

    val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR) }
    val maxYear = currentYear + 1

    val yearVal = year.toIntOrNull()
    val yearError = year.isNotEmpty() && (yearVal == null || yearVal < 1990 || yearVal > maxYear)
    val isValid = brand.isNotBlank() && model.isNotBlank() &&
            year.isNotBlank() && plate.isNotBlank() && !yearError

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(64.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Aracınızı Ekleyin",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Araç bilgilerini girin",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextHint
                )
            }
            if (onSkip != null) {
                TextButton(onClick = onSkip) {
                    Text("Atla", color = TextHint, style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Text(
            "Popüler Markalar",
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary
        )
        Spacer(Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 8.dp)
        ) {
            items(popularBrands) { b ->
                val isSelected = brand == b
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Blue500 else NavyContainer)
                        .border(
                            1.dp,
                            if (isSelected) Blue500 else DividerColor,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { brand = b }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        b,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) TextPrimary else TextSecondary
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        AutoLinkTextField(
            value = brand,
            onValueChange = { brand = it },
            label = "Marka",
            leadingIcon = Icons.Default.DirectionsCar
        )
        Spacer(Modifier.height(16.dp))
        AutoLinkTextField(
            value = model,
            onValueChange = { model = it },
            label = "Model",
            leadingIcon = Icons.Default.Build
        )
        Spacer(Modifier.height(16.dp))
        AutoLinkTextField(
            value = year,
            onValueChange = { if (it.length <= 4) year = it },
            label = "Model Yılı",
            leadingIcon = Icons.Default.DateRange,
            keyboardType = KeyboardType.Number,
            errorMessage = if (yearError) "Geçerli bir yıl girin (1990–$maxYear)" else null
        )
        Spacer(Modifier.height(16.dp))
        AutoLinkTextField(
            value = plate,
            onValueChange = { plate = it.uppercase() },
            label = "Plaka",
            leadingIcon = Icons.Default.Description
        )
        Spacer(Modifier.height(16.dp))
        AutoLinkTextField(
            value = mileage,
            onValueChange = { mileage = it },
            label = "Kilometre (opsiyonel)",
            leadingIcon = Icons.Default.Speed,
            keyboardType = KeyboardType.Number
        )

        Spacer(Modifier.height(32.dp))

        AutoLinkButton(
            text = "Aracı Kaydet",
            onClick = {
                onVehicleSaved(
                    Vehicle(
                        id = System.currentTimeMillis().toString(),
                        brand = brand.trim(),
                        model = model.trim(),
                        plate = plate.trim(),
                        year = year.toInt(),
                        fuelLevel = 0,
                        mileage = mileage.toIntOrNull() ?: 0
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isValid
        )

        Spacer(Modifier.height(32.dp))
    }
}
