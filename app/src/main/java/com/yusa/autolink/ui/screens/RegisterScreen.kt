package com.yusa.autolink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yusa.autolink.data.AppState
import com.yusa.autolink.data.model.AccountType
import com.yusa.autolink.ui.theme.*

// ============================================================
// RegisterScreen — Hem müşteri hem işletme kaydı bu ekrandan yapılır
//
// Akış:
//   1. Hesap tipi seçilir: Müşteri veya İşletme
//   2. Müşteri → ad, e-posta, telefon, şifre
//      İşletme → ek olarak işletme adı, adres, hizmet türü (yıkama/bakım)
//   3. Validasyon: boş alan ve şifre uzunluğu kontrolü
//   4. AppState.register() → e-posta zaten kayıtlıysa false döner
//   5. İşletme kaydında otomatik olarak bir Business nesnesi de oluşturulur
//
// Hesap tipine göre form alanları dinamik olarak gösterilir/gizlenir.
// ============================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToHome: () -> Unit,   // Başarılı kayıtta çağrılır — AppNavigation yönlendirir
    onNavigateBack: () -> Unit      // Geri tuşu — Login'e döner
) {
    var selectedType        by remember { mutableStateOf(AccountType.CUSTOMER) }
    var selectedServiceType by remember { mutableStateOf("washing") }  // "washing" veya "maintenance"
    var fullName            by remember { mutableStateOf("") }
    var email               by remember { mutableStateOf("") }
    var phone               by remember { mutableStateOf("") }
    var businessName        by remember { mutableStateOf("") }
    var address             by remember { mutableStateOf("") }
    var password            by remember { mutableStateOf("") }
    var errorMessage        by remember { mutableStateOf("") }

    // E-posta ve şifre alanlarından Türkçe karakterleri siler
    fun stripTurkish(s: String) = s.filter { it !in "şŞğĞüÜöÖçÇıİ" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kayıt Ol") },
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hesap tipi seçimi — müşteri veya işletme
            Text("Hesap Türünüzü Seçin", fontSize = 16.sp, fontWeight = FontWeight.Bold)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AccountTypeCard(
                    title      = "Müşteri",
                    subtitle   = "Araç sahibiyim",
                    icon       = Icons.Filled.Person,
                    isSelected = selectedType == AccountType.CUSTOMER,
                    onClick    = { selectedType = AccountType.CUSTOMER },
                    modifier   = Modifier.weight(1f)
                )
                AccountTypeCard(
                    title      = "İşletme",
                    subtitle   = "Servis sahibiyim",
                    icon       = Icons.Filled.Store,
                    isSelected = selectedType == AccountType.BUSINESS,
                    onClick    = { selectedType = AccountType.BUSINESS },
                    modifier   = Modifier.weight(1f)
                )
            }

            HorizontalDivider()

            // Temel bilgiler — her iki hesap tipinde de gösterilir
            RegisterField("Ad Soyad", fullName, { fullName = it }, Icons.Filled.Person)
            RegisterField("E-posta",  email,    { email = stripTurkish(it); errorMessage = "" }, Icons.Filled.Email,  KeyboardType.Email)
            RegisterField("Telefon",  phone,    { phone = it }, Icons.Filled.Phone,  KeyboardType.Phone)

            // İşletme hesabına özgü alanlar
            if (selectedType == AccountType.BUSINESS) {
                RegisterField("İşletme Adı", businessName, { businessName = it }, Icons.Filled.Store)
                RegisterField("Adres",        address,      { address = it },       Icons.Filled.LocationOn)

                // İşletme türü: yıkama mı bakım mı?
                Text("Hizmet Türünüzü Seçin", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ServiceTypeCard(
                        title      = "Araba Yıkama",
                        icon       = Icons.Filled.LocalCarWash,
                        isSelected = selectedServiceType == "washing",
                        onClick    = { selectedServiceType = "washing" },
                        modifier   = Modifier.weight(1f)
                    )
                    ServiceTypeCard(
                        title      = "Oto Bakım",
                        icon       = Icons.Filled.Build,
                        isSelected = selectedServiceType == "maintenance",
                        onClick    = { selectedServiceType = "maintenance" },
                        modifier   = Modifier.weight(1f)
                    )
                }
            }

            // Şifre alanı
            OutlinedTextField(
                value         = password,
                onValueChange = { password = stripTurkish(it) },
                label         = { Text("Şifre") },
                leadingIcon   = { Icon(Icons.Filled.Lock, null, tint = TextSecondary) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine    = true,
                shape         = RoundedCornerShape(12.dp),
                modifier      = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = PrimaryBlue,
                    unfocusedBorderColor = CardBorder
                )
            )

            // Hata mesajı
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color(0xFFD32F2F), fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Kayıt ol butonu — doğrulama geçerse AppState.register() çağrılır
            Button(
                onClick = {
                    when {
                        fullName.isBlank() -> errorMessage = "Ad soyad boş bırakılamaz."
                        email.isBlank()    -> errorMessage = "E-posta adresi boş bırakılamaz."
                        password.length < 6 -> errorMessage = "Şifre en az 6 karakter olmalıdır."
                        selectedType == AccountType.BUSINESS && businessName.isBlank() ->
                            errorMessage = "İşletme adı boş bırakılamaz."
                        else -> {
                            val success = AppState.register(
                                name         = fullName,
                                email        = email,
                                phone        = phone,
                                password     = password,
                                accountType  = selectedType,
                                businessName = if (selectedType == AccountType.BUSINESS) businessName else "",
                                serviceType  = if (selectedType == AccountType.BUSINESS) selectedServiceType else "",
                                address      = if (selectedType == AccountType.BUSINESS) address else ""
                            )
                            if (success) onNavigateToHome()
                            else errorMessage = "Bu e-posta adresi zaten kayıtlı."
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape    = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (selectedType == AccountType.CUSTOMER) "Müşteri Olarak Kayıt Ol" else "İşletme Olarak Kayıt Ol",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ── AccountTypeCard ───────────────────────────────────────────────────────────
// Müşteri veya İşletme seçimindeki kart bileşeni
// isSelected → seçili kart mavi çerçeve + soluk mavi arka plan alır
// modifier = Modifier.weight(1f) → iki kart yan yana eşit genişlikte durur
@Composable
private fun AccountTypeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .border(2.dp, if (isSelected) PrimaryBlue else CardBorder, RoundedCornerShape(16.dp)),
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryBlue.copy(alpha = 0.05f) else SurfaceWhite
        )
    ) {
        Column(
            modifier            = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = if (isSelected) PrimaryBlue else TextSecondary, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold, color = if (isSelected) PrimaryBlue else TextPrimary)
            Text(subtitle, fontSize = 11.sp, color = TextSecondary)
        }
    }
}

// ── ServiceTypeCard ───────────────────────────────────────────────────────────
// İşletme tipini seçmek için kullanılan kart (Araba Yıkama / Oto Bakım)
// Sadece AccountType.BUSINESS seçildiğinde gösterilir
@Composable
private fun ServiceTypeCard(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .border(2.dp, if (isSelected) PrimaryBlue else CardBorder, RoundedCornerShape(16.dp)),
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryBlue.copy(alpha = 0.05f) else SurfaceWhite
        )
    ) {
        Column(
            modifier            = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = if (isSelected) PrimaryBlue else TextSecondary, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = if (isSelected) PrimaryBlue else TextPrimary)
        }
    }
}

// ── RegisterField ─────────────────────────────────────────────────────────────
// Kayıt formundaki tekrar eden OutlinedTextField'ları sadeleştiren yardımcı bileşen
// keyboardType parametresi → E-posta alanı için Email, telefon için Phone klavyesi açılır
@Composable
private fun RegisterField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value           = value,
        onValueChange   = onValueChange,
        label           = { Text(label) },
        leadingIcon     = { Icon(icon, null, tint = TextSecondary) },
        singleLine      = true,
        shape           = RoundedCornerShape(12.dp),
        modifier        = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = PrimaryBlue,
            unfocusedBorderColor = CardBorder
        )
    )
}
