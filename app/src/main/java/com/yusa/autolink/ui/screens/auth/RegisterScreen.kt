package com.yusa.autolink.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.yusa.autolink.data.model.UserType
import com.yusa.autolink.ui.components.AutoLinkButton
import com.yusa.autolink.ui.components.AutoLinkTextField
import com.yusa.autolink.ui.theme.*

@Composable
fun RegisterScreen(
    userType: UserType,
    onNavigateBack: () -> Unit,
    onRegisterComplete: (name: String, email: String, phone: String) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val passwordMismatch = confirmPassword.isNotEmpty() && password != confirmPassword
    val emailInvalid = email.isNotEmpty() && !email.contains("@")
    val isFormValid = fullName.isNotBlank() && email.isNotBlank() && !emailInvalid &&
            phone.isNotBlank() && password.length >= 6 && !passwordMismatch

    val title = if (userType == UserType.SERVICE_PROVIDER) "Servis Hesabı" else "Kayıt Ol"
    val subtitle = if (userType == UserType.SERVICE_PROVIDER)
        "Servis sağlayıcı hesabı oluşturun" else "Yeni hesap oluşturun"
    val nameLabel = if (userType == UserType.SERVICE_PROVIDER) "Yetkili Ad Soyad" else "Ad Soyad"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(56.dp))

        IconButton(onClick = onNavigateBack, modifier = Modifier.size(40.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = TextPrimary)
        }

        Spacer(Modifier.height(24.dp))

        Text(title, style = MaterialTheme.typography.headlineLarge, color = TextPrimary)
        Spacer(Modifier.height(4.dp))
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextHint)

        Spacer(Modifier.height(32.dp))

        AutoLinkTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = nameLabel,
            leadingIcon = Icons.Default.Person
        )
        Spacer(Modifier.height(16.dp))
        AutoLinkTextField(
            value = email,
            onValueChange = { email = it },
            label = "E-posta",
            leadingIcon = Icons.Default.Email,
            keyboardType = KeyboardType.Email,
            errorMessage = if (emailInvalid) "Geçerli bir e-posta adresi girin" else null
        )
        Spacer(Modifier.height(16.dp))
        AutoLinkTextField(
            value = phone,
            onValueChange = { phone = it },
            label = "Telefon",
            leadingIcon = Icons.Default.Phone,
            keyboardType = KeyboardType.Phone
        )
        Spacer(Modifier.height(16.dp))
        AutoLinkTextField(
            value = password,
            onValueChange = { password = it },
            label = "Şifre",
            leadingIcon = Icons.Default.Lock,
            isPassword = true,
            errorMessage = if (password.isNotEmpty() && password.length < 6) "Şifre en az 6 karakter olmalı" else null
        )
        Spacer(Modifier.height(16.dp))
        AutoLinkTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Şifre Tekrar",
            leadingIcon = Icons.Default.Lock,
            isPassword = true,
            errorMessage = if (passwordMismatch) "Şifreler eşleşmiyor" else null
        )

        Spacer(Modifier.height(32.dp))

        AutoLinkButton(
            text = "Kayıt Ol",
            onClick = {
                val formattedName = fullName.trim()
                    .split(" ")
                    .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
                onRegisterComplete(formattedName, email.trim(), phone.trim())
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid
        )

        Spacer(Modifier.height(24.dp))

        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(color = TextSecondary)) { append("Zaten hesabınız var mı?  ") }
                withStyle(SpanStyle(color = Blue300)) { append("Giriş Yap") }
            },
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().clickable(onClick = onNavigateBack)
        )

        Spacer(Modifier.height(32.dp))
    }
}
