package com.yusa.autolink.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.yusa.autolink.data.model.ButtonVariant
import com.yusa.autolink.ui.components.AutoLinkButton
import com.yusa.autolink.ui.components.AutoLinkTextField
import com.yusa.autolink.ui.theme.*

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToDashboard: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBg)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.weight(1f))

        // Brand
        Text(
            "AutoLink",
            style = MaterialTheme.typography.displayLarge,
            color = Blue500
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Premium Otomotiv Platformu",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )

        Spacer(Modifier.height(48.dp))

        // Form
        Text("Giriş Yap", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
        Spacer(Modifier.height(4.dp))
        Text("Hesabınıza giriş yapın", style = MaterialTheme.typography.bodyMedium, color = TextHint)

        Spacer(Modifier.height(32.dp))

        AutoLinkTextField(
            value = email,
            onValueChange = { email = it },
            label = "E-posta",
            leadingIcon = Icons.Default.Email,
            keyboardType = KeyboardType.Email
        )
        Spacer(Modifier.height(16.dp))
        AutoLinkTextField(
            value = password,
            onValueChange = { password = it },
            label = "Şifre",
            leadingIcon = Icons.Default.Lock,
            isPassword = true
        )

        Spacer(Modifier.height(8.dp))
        Text(
            "Şifremi Unuttum",
            style = MaterialTheme.typography.labelLarge,
            color = Blue300,
            modifier = Modifier.align(Alignment.End).clickable { }
        )

        Spacer(Modifier.height(32.dp))

        AutoLinkButton(
            text = "Giriş Yap",
            onClick = onNavigateToDashboard,
            modifier = Modifier.fillMaxWidth(),
            enabled = email.isNotBlank() && password.isNotBlank()
        )

        Spacer(Modifier.weight(1f))

        // Register link
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(color = TextSecondary)) { append("Hesabınız yok mu?  ") }
                withStyle(SpanStyle(color = Blue300)) { append("Kayıt Ol") }
            },
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().clickable(onClick = onNavigateToRegister)
        )

        Spacer(Modifier.height(32.dp))
    }
}
