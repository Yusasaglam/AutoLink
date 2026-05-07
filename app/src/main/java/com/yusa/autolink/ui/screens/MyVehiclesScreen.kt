package com.yusa.autolink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yusa.autolink.data.DemoData
import com.yusa.autolink.data.model.Vehicle
import com.yusa.autolink.ui.theme.*

// Araçlarım ekranı - kayıtlı araçlar, ekleme ve düzenleme formu
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyVehiclesScreen() {
    // Araç listesi - başlangıçta demo aracı içerir
    var vehicles by remember { mutableStateOf(listOf(DemoData.userVehicle)) }

    // Düzenleme için seçilen araç (null ise yeni araç ekleniyor)
    var editingVehicle by remember { mutableStateOf<Vehicle?>(null) }

    // Form göster/gizle
    var showForm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title  = { Text("Araçlarım") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    // Sağ üstteki + butonu
                    IconButton(onClick = {
                        editingVehicle = null
                        showForm       = true
                    }) {
                        Icon(Icons.Filled.Add, contentDescription = "Araç Ekle")
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Kayıtlı araç kartları
            vehicles.forEach { vehicle ->
                VehicleItemCard(
                    vehicle = vehicle,
                    onEdit  = {
                        editingVehicle = vehicle
                        showForm       = true
                    }
                )
            }

            // Yeni araç ekle butonu (listenin altında)
            if (!showForm) {
                OutlinedButton(
                    onClick  = { editingVehicle = null; showForm = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Yeni Araç Ekle")
                }
            }

            // Ekleme/düzenleme formu - showForm true olduğunda görünür
            if (showForm) {
                VehicleForm(
                    initialVehicle = editingVehicle,
                    onSave         = { brand, model, year, engine, fuelType ->
                        val updatedVehicle = Vehicle(
                            id       = editingVehicle?.id ?: ((vehicles.maxOfOrNull { it.id } ?: 0) + 1),
                            brand    = brand,
                            model    = model,
                            year     = year.toIntOrNull() ?: 2020,
                            plate    = editingVehicle?.plate ?: "",
                            fuelType = fuelType,
                            engine   = engine
                        )
                        // Düzenleme ise mevcut aracı güncelle, yeni ekleme ise listeye ekle
                        vehicles = if (editingVehicle != null) {
                            vehicles.map { if (it.id == editingVehicle!!.id) updatedVehicle else it }
                        } else {
                            vehicles + updatedVehicle
                        }
                        showForm       = false
                        editingVehicle = null
                    },
                    onCancel = {
                        showForm       = false
                        editingVehicle = null
                    }
                )
            }
        }
    }
}

// Araç kartı - araç bilgisi ve düzenle butonu
@Composable
private fun VehicleItemCard(vehicle: Vehicle, onEdit: () -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier          = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.DirectionsCar,
                contentDescription = null,
                tint     = PrimaryBlue,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${vehicle.brand} ${vehicle.model}",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp
                )
                Text(
                    buildString {
                        append("${vehicle.year}")
                        if (vehicle.engine.isNotBlank()) append(" · ${vehicle.engine}")
                        append(" · ${vehicle.fuelType}")
                    },
                    fontSize = 13.sp,
                    color    = TextSecondary
                )
                if (vehicle.plate.isNotBlank()) {
                    Text(vehicle.plate, fontSize = 12.sp, color = TextSecondary)
                }
            }
            TextButton(onClick = onEdit) {
                Text("Düzenle", color = PrimaryBlue, fontSize = 13.sp)
            }
        }
    }
}

// Araç ekleme/düzenleme formu - 5 alan: Marka, Model, Yıl, Motor, Yakıt Tipi
@Composable
private fun VehicleForm(
    initialVehicle: Vehicle?,
    onSave:   (brand: String, model: String, year: String, engine: String, fuelType: String) -> Unit,
    onCancel: () -> Unit
) {
    // Düzenleme modundaysa mevcut bilgilerle form başlar
    var brand    by remember { mutableStateOf(initialVehicle?.brand              ?: "") }
    var model    by remember { mutableStateOf(initialVehicle?.model              ?: "") }
    var year     by remember { mutableStateOf(initialVehicle?.year?.toString()   ?: "") }
    var engine   by remember { mutableStateOf(initialVehicle?.engine             ?: "") }
    var fuelType by remember { mutableStateOf(initialVehicle?.fuelType           ?: "") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Column(
            modifier            = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text       = if (initialVehicle != null) "Aracı Düzenle" else "Yeni Araç Ekle",
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold
            )

            VehicleFormField("Marka",      brand,    { brand    = it })
            VehicleFormField("Model",      model,    { model    = it })
            VehicleFormField("Yıl",        year,     { year     = it }, KeyboardType.Number)
            VehicleFormField("Motor",      engine,   { engine   = it })
            VehicleFormField("Yakıt Tipi", fuelType, { fuelType = it })

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // İptal butonu
                OutlinedButton(
                    onClick  = onCancel,
                    modifier = Modifier.weight(1f),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    Text("İptal")
                }
                // Kaydet butonu - en az marka ve model dolu olmalı
                Button(
                    onClick  = { onSave(brand, model, year, engine, fuelType) },
                    modifier = Modifier.weight(1f),
                    enabled  = brand.isNotBlank() && model.isNotBlank(),
                    colors   = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    Text("Kaydet")
                }
            }
        }
    }
}

// Tekrar kullanılan form alanı
@Composable
private fun VehicleFormField(
    label:         String,
    value:         String,
    onValueChange: (String) -> Unit,
    keyboardType:  KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value           = value,
        onValueChange   = onValueChange,
        label           = { Text(label) },
        singleLine      = true,
        shape           = RoundedCornerShape(12.dp),
        modifier        = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors          = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = PrimaryBlue,
            unfocusedBorderColor = CardBorder
        )
    )
}
