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
import com.yusa.autolink.data.model.AccountType
import com.yusa.autolink.ui.theme.*

// Kayıt ekranı - müşteri veya işletme hesabı seçimi yapılır
// Demo: Her zaman başarılı kayıt, gerçek sunucu yok
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit
) {
    // Seçilen hesap türü (müşteri veya işletme)
    var selectedType by remember { mutableStateOf(AccountType.CUSTOMER) }
    var fullName     by remember { mutableStateOf("") }
    var email        by remember { mutableStateOf("") }
    var phone        by remember { mutableStateOf("") }
    var businessName by remember { mutableStateOf("") }
    var password     by remember { mutableStateOf("") }

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
            // Hesap türü seçimi
            Text("Hesap Türünüzü Seçin", fontSize = 16.sp, fontWeight = FontWeight.Bold)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Müşteri hesabı kartı
                AccountTypeCard(
                    title      = "Müşteri",
                    subtitle   = "Araç sahibiyim",
                    icon       = Icons.Filled.Person,
                    isSelected = selectedType == AccountType.CUSTOMER,
                    onClick    = { selectedType = AccountType.CUSTOMER },
                    modifier   = Modifier.weight(1f)
                )
                // İşletme hesabı kartı
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

            // Form alanları
            RegisterField("Ad Soyad", fullName, { fullName = it }, Icons.Filled.Person)
            RegisterField("E-posta", email, { email = it }, Icons.Filled.Email, KeyboardType.Email)
            RegisterField("Telefon", phone, { phone = it }, Icons.Filled.Phone, KeyboardType.Phone)

            // İşletme adı - yalnızca işletme hesabı seçilince göster
            if (selectedType == AccountType.BUSINESS) {
                RegisterField("İşletme Adı", businessName, { businessName = it }, Icons.Filled.Store)
            }

            // Şifre alanı
            OutlinedTextField(
                value         = password,
                onValueChange = { password = it },
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

            Spacer(modifier = Modifier.height(8.dp))

            // Kayıt ol butonu - demo: her zaman çalışır
            Button(
                onClick  = onNavigateToHome,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape    = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (selectedType == AccountType.CUSTOMER)
                        "Müşteri Olarak Kayıt Ol" else "İşletme Olarak Kayıt Ol",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// Hesap türü seçim kartı - tıklanınca seçili hale gelir
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
            .border(
                width = 2.dp,
                color = if (isSelected) PrimaryBlue else CardBorder,
                shape = RoundedCornerShape(16.dp)
            ),
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryBlue.copy(alpha = 0.05f) else SurfaceWhite
        )
    ) {
        Column(
            modifier            = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint     = if (isSelected) PrimaryBlue else TextSecondary,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text       = title,
                fontWeight = FontWeight.Bold,
                color      = if (isSelected) PrimaryBlue else TextPrimary
            )
            Text(text = subtitle, fontSize = 11.sp, color = TextSecondary)
        }
    }
}

// Tekrar kullanılan form alanı
@Composable
private fun RegisterField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        label         = { Text(label) },
        leadingIcon   = { Icon(icon, null, tint = TextSecondary) },
        singleLine    = true,
        shape         = RoundedCornerShape(12.dp),
        modifier      = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = PrimaryBlue,
            unfocusedBorderColor = CardBorder
        )
    )
}
