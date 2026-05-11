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
import com.yusa.autolink.data.CAR_BRAND_NAMES
import com.yusa.autolink.data.modelsFor
import com.yusa.autolink.data.model.Vehicle
import com.yusa.autolink.ui.theme.*

// Yakıt tipleri — dropdown'da sabit liste, değişmediği için private val olarak tanımlandı
private val FUEL_TYPES = listOf("Benzin", "Dizel", "LPG", "Elektrik", "Hibrit")

// ============================================================
// MyVehiclesScreen — Kullanıcının araçlarını listeler (sekme 2)
//
// Özellikler:
//   • Araç listesi: her araç VehicleItemCard ile gösterilir
//   • "+" butonu veya "Düzenle" → VehicleForm açılır
//   • Yeni araç: AppState.addVehicle() ile eklenir
//   • Mevcut araç: AppState.updateVehicle() ile güncellenir
//   • Araç yoksa EmptyVehiclesState gösterilir
//
// editingVehicle = null   → form yeni araç ekleme modunda açılır
// editingVehicle = Vehicle → form düzenleme modunda, mevcut değerler dolu gelir
// ============================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyVehiclesScreen() {
    // toList() → AppState'in mutableList referansını kopyalar; form kaydedince tekrar okunur
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
                    // Sağ üstteki + butonu → yeni araç ekleme formu açar
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
            // Araç yoksa ve form açık değilse → boş durum ekranı
            if (vehicles.isEmpty() && !showForm) {
                EmptyVehiclesState(onAdd = { showForm = true })
            }

            // Mevcut araçları listele — forEach Compose'da LazyColumn'a göre basit ama
            // araç sayısı genellikle az olduğu için performans farkı önemsiz
            vehicles.forEach { vehicle ->
                VehicleItemCard(
                    vehicle = vehicle,
                    onEdit  = { editingVehicle = vehicle; showForm = true }
                )
            }

            // Araç varsa form kapalıyken "Yeni Araç Ekle" butonu gösterilir
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

            // Form açıksa araç ekleme/düzenleme formu gösterilir
            if (showForm) {
                VehicleForm(
                    initialVehicle = editingVehicle,
                    onSave = { brand, model, year, engine, fuelType ->
                        val yearInt = year.toIntOrNull() ?: 2020 // Geçersiz yıl girildiyse 2020 varsayılır
                        if (editingVehicle != null) {
                            // Düzenleme modu → copy() ile sadece değişen alanlar güncellenir
                            val updated = editingVehicle!!.copy(
                                brand    = brand,
                                model    = model,
                                year     = yearInt,
                                fuelType = fuelType,
                                engine   = engine
                            )
                            AppState.updateVehicle(updated)
                        } else {
                            // Yeni araç modu → plate boş bırakıldı (form'da plaka yok)
                            AppState.addVehicle(
                                brand    = brand,
                                model    = model,
                                year     = yearInt,
                                plate    = "",
                                fuelType = fuelType,
                                engine   = engine
                            )
                        }
                        // AppState güncellendi; listeyi tekrar oku ve formu kapat
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

// ── EmptyVehiclesState ────────────────────────────────────────────────────────
// Hiç araç yokken gösterilen teşvik ekranı
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

// ── VehicleItemCard ───────────────────────────────────────────────────────────
// Listede her aracı gösteren kart; sağda "Düzenle" butonu var
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
                // buildString → engine boşsa "·" ayracı çıkmaz
                Text(
                    buildString {
                        append("${vehicle.year}")
                        if (vehicle.engine.isNotBlank()) append(" · ${vehicle.engine}")
                        append(" · ${vehicle.fuelType}")
                    },
                    fontSize = 13.sp,
                    color    = TextSecondary
                )
                // Plaka sadece doldurulmuşsa gösterilir
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

// ── VehicleForm ───────────────────────────────────────────────────────────────
// Araç ekleme ve düzenleme formu
// initialVehicle = null   → yeni araç (boş alanlar)
// initialVehicle = Vehicle → düzenleme (mevcut değerler doldurulur)
// ExposedDropdownMenuBox → marka/model/yakıt için açılır liste bileşeni
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VehicleForm(
    initialVehicle: Vehicle?,
    onSave:   (brand: String, model: String, year: String, engine: String, fuelType: String) -> Unit,
    onCancel: () -> Unit
) {
    // ?: "" → initialVehicle null ise boş string başlangıç değeri
    var brand        by remember { mutableStateOf(initialVehicle?.brand    ?: "") }
    var model        by remember { mutableStateOf(initialVehicle?.model    ?: "") }
    var year         by remember { mutableStateOf(initialVehicle?.year?.toString() ?: "") }
    var engine       by remember { mutableStateOf(initialVehicle?.engine   ?: "") }
    var fuelType     by remember { mutableStateOf(initialVehicle?.fuelType ?: FUEL_TYPES[0]) }

    // Her dropdown'ın açık/kapalı durumu ayrı state ile yönetilir
    var brandExpanded by remember { mutableStateOf(false) }
    var modelExpanded by remember { mutableStateOf(false) }
    var fuelExpanded  by remember { mutableStateOf(false) }

    // CarData.kt'deki modelsFor() → seçili markaya ait model listesini döndürür
    val availableModels = modelsFor(brand)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Column(
            modifier            = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Form başlığı — yeni ekle mi, düzenle mi?
            Text(
                text       = if (initialVehicle != null) "Aracı Düzenle" else "Yeni Araç Ekle",
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold
            )

            // ── Marka dropdown ────────────────────────────────────────
            // ExposedDropdownMenuBox → hem manuel yazma hem listeden seçme destekler
            // Kullanıcı yazdıkça filteredBrands güncellenir (contains ile filtre)
            ExposedDropdownMenuBox(
                expanded         = brandExpanded,
                onExpandedChange = { brandExpanded = !brandExpanded }
            ) {
                OutlinedTextField(
                    value         = brand,
                    onValueChange = {
                        brand = it
                        model = "" // Marka değişince model sıfırlanır
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
                // Yazılan harfe göre filtrelenmiş marka listesi
                val filteredBrands = CAR_BRAND_NAMES.filter {
                    brand.isBlank() || it.contains(brand, ignoreCase = true)
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
                                    brand         = b
                                    model         = ""
                                    brandExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // ── Model dropdown ────────────────────────────────────────
            // modelsFor(brand) → CarData.kt'den markaya ait modeller
            // Marka seçilmeden model dropdown'ı açılmaz (availableModels boşsa)
            ExposedDropdownMenuBox(
                expanded         = modelExpanded,
                onExpandedChange = { if (availableModels.isNotEmpty() || model.isNotBlank()) modelExpanded = !modelExpanded }
            ) {
                OutlinedTextField(
                    value         = model,
                    onValueChange = { model = it; modelExpanded = true },
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
                    model.isBlank() || it.contains(model, ignoreCase = true)
                }
                if (filteredModels.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded         = modelExpanded,
                        onDismissRequest = { modelExpanded = false }
                    ) {
                        filteredModels.forEach { m ->
                            DropdownMenuItem(
                                text    = { Text(m) },
                                onClick = { model = m; modelExpanded = false }
                            )
                        }
                    }
                }
            }

            // Yıl → sadece sayı girilsin diye KeyboardType.Number
            VehicleFormField("Yıl",   year,   { year   = it }, KeyboardType.Number)
            // Motor → isteğe bağlı (ör. "1.5 dCi", "2.0 TDI")
            VehicleFormField("Motor", engine, { engine = it })

            // ── Yakıt tipi dropdown ───────────────────────────────────
            // readOnly = true → kullanıcı liste dışı bir şey yazamaz
            ExposedDropdownMenuBox(
                expanded         = fuelExpanded,
                onExpandedChange = { fuelExpanded = !fuelExpanded }
            ) {
                OutlinedTextField(
                    value         = fuelType,
                    onValueChange = {},
                    readOnly      = true,
                    label         = { Text("Yakıt Tipi") },
                    trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fuelExpanded) },
                    singleLine    = true,
                    shape         = RoundedCornerShape(12.dp),
                    modifier      = Modifier.fillMaxWidth().menuAnchor(),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = PrimaryBlue,
                        unfocusedBorderColor = CardBorder
                    )
                )
                ExposedDropdownMenu(
                    expanded         = fuelExpanded,
                    onDismissRequest = { fuelExpanded = false }
                ) {
                    FUEL_TYPES.forEach { type ->
                        DropdownMenuItem(
                            text    = { Text(type) },
                            onClick = { fuelType = type; fuelExpanded = false }
                        )
                    }
                }
            }

            // İptal / Kaydet butonları yan yana, weight(1f) ile eşit genişlik
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick  = onCancel,
                    modifier = Modifier.weight(1f),
                    shape    = RoundedCornerShape(12.dp)
                ) { Text("İptal") }

                // Kaydet butonu → marka ve model dolduğunda aktif olur
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

// ── VehicleFormField ──────────────────────────────────────────────────────────
// Araç formundaki tekrar eden OutlinedTextField'ları sadeleştiren yardımcı bileşen
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
