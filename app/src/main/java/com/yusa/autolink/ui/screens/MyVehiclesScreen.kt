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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yusa.autolink.data.AppState
import com.yusa.autolink.data.model.Vehicle
import com.yusa.autolink.ui.theme.*

private val FUEL_TYPES = listOf("Benzin", "Dizel", "LPG", "Elektrik", "Hibrit")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyVehiclesScreen() {
    var vehicles       by remember { mutableStateOf(AppState.userVehicles.toList()) }
    var editingVehicle by remember { mutableStateOf<Vehicle?>(null) }
    var showForm       by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title  = { Text("Araçlarım") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    IconButton(onClick = { editingVehicle = null; showForm = true }) {
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
            if (vehicles.isEmpty() && !showForm) {
                EmptyVehiclesState(onAdd = { showForm = true })
            }

            vehicles.forEach { vehicle ->
                VehicleItemCard(
                    vehicle = vehicle,
                    onEdit  = { editingVehicle = vehicle; showForm = true }
                )
            }

            if (!showForm && vehicles.isNotEmpty()) {
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

            if (showForm) {
                VehicleForm(
                    initialVehicle = editingVehicle,
                    onSave = { brand, model, year, engine, fuelType ->
                        val yearInt = year.toIntOrNull() ?: 2020
                        if (editingVehicle != null) {
                            val updated = editingVehicle!!.copy(
                                brand    = brand,
                                model    = model,
                                year     = yearInt,
                                fuelType = fuelType,
                                engine   = engine
                            )
                            AppState.updateVehicle(updated)
                        } else {
                            AppState.addVehicle(
                                brand    = brand,
                                model    = model,
                                year     = yearInt,
                                plate    = "",
                                fuelType = fuelType,
                                engine   = engine
                            )
                        }
                        vehicles       = AppState.userVehicles.toList()
                        showForm       = false
                        editingVehicle = null
                    },
                    onCancel = { showForm = false; editingVehicle = null }
                )
            }
        }
    }
}

@Composable
private fun EmptyVehiclesState(onAdd: () -> Unit) {
    Column(
        modifier            = Modifier.fillMaxWidth().padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Filled.DirectionsCar,
            contentDescription = null,
            tint     = TextSecondary.copy(alpha = 0.4f),
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text      = "Kayıtlı araç yok",
            fontSize  = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color     = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text      = "Araçlarınızı ekleyerek randevu alırken\nkolayca seçebilirsiniz.",
            fontSize  = 14.sp,
            color     = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAdd,
            colors  = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape   = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Araç Ekle")
        }
    }
}

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VehicleForm(
    initialVehicle: Vehicle?,
    onSave:   (brand: String, model: String, year: String, engine: String, fuelType: String) -> Unit,
    onCancel: () -> Unit
) {
    var brand    by remember { mutableStateOf(initialVehicle?.brand    ?: "") }
    var model    by remember { mutableStateOf(initialVehicle?.model    ?: "") }
    var year     by remember { mutableStateOf(initialVehicle?.year?.toString() ?: "") }
    var engine   by remember { mutableStateOf(initialVehicle?.engine   ?: "") }
    var fuelType by remember { mutableStateOf(initialVehicle?.fuelType ?: FUEL_TYPES[0]) }
    var expanded by remember { mutableStateOf(false) }

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

            VehicleFormField("Marka",  brand,  { brand  = it })
            VehicleFormField("Model",  model,  { model  = it })
            VehicleFormField("Yıl",    year,   { year   = it }, KeyboardType.Number)
            VehicleFormField("Motor",  engine, { engine = it })

            // Yakıt tipi dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value         = fuelType,
                    onValueChange = {},
                    readOnly      = true,
                    label         = { Text("Yakıt Tipi") },
                    trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    singleLine    = true,
                    shape         = RoundedCornerShape(12.dp),
                    modifier      = Modifier.fillMaxWidth().menuAnchor(),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = PrimaryBlue,
                        unfocusedBorderColor = CardBorder
                    )
                )
                ExposedDropdownMenu(
                    expanded         = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    FUEL_TYPES.forEach { type ->
                        DropdownMenuItem(
                            text    = { Text(type) },
                            onClick = { fuelType = type; expanded = false }
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick  = onCancel,
                    modifier = Modifier.weight(1f),
                    shape    = RoundedCornerShape(12.dp)
                ) { Text("İptal") }

                Button(
                    onClick  = { onSave(brand, model, year, engine, fuelType) },
                    modifier = Modifier.weight(1f),
                    enabled  = brand.isNotBlank() && model.isNotBlank(),
                    colors   = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape    = RoundedCornerShape(12.dp)
                ) { Text("Kaydet") }
            }
        }
    }
}

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
