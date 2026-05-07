package com.yusa.autolink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yusa.autolink.ui.theme.*

// Giriş ekranı - demo modda her zaman başarılı giriş yapılır
@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email          by remember { mutableStateOf("") }
    var password       by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(0.4f))

        // Logo ve uygulama adı
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(PrimaryBlue, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.DirectionsCar,
                    contentDescription = null,
                    tint     = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("OtoGüven", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Güvenilir servis, net fiyat.", fontSize = 14.sp, color = TextSecondary)
        }

        Spacer(modifier = Modifier.height(48.dp))

        // E-posta alanı
        Text("E-posta", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value         = email,
            onValueChange = { email = it },
            placeholder   = { Text("ornek@email.com", color = TextSecondary) },
            leadingIcon   = { Icon(Icons.Filled.Email, null, tint = TextSecondary) },
            singleLine    = true,
            shape         = RoundedCornerShape(12.dp),
            modifier      = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = PrimaryBlue,
                unfocusedBorderColor = CardBorder
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Şifre alanı - göster/gizle butonu ile
        Text("Şifre", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value         = password,
            onValueChange = { password = it },
            placeholder   = { Text("••••••••", color = TextSecondary) },
            leadingIcon   = { Icon(Icons.Filled.Lock, null, tint = TextSecondary) },
            trailingIcon  = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine    = true,
            shape         = RoundedCornerShape(12.dp),
            modifier      = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = PrimaryBlue,
                unfocusedBorderColor = CardBorder
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Giriş Yap butonu - demo: her zaman çalışır, gerçek doğrulama yok
        Button(
            onClick  = onNavigateToHome,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors   = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape    = RoundedCornerShape(16.dp)
        ) {
            Text("Giriş Yap", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Kayıt ol linki
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = TextSecondary)) { append("Hesabınız yok mu?  ") }
                withStyle(SpanStyle(color = PrimaryBlue, fontWeight = FontWeight.SemiBold)) {
                    append("Kayıt Ol")
                }
            },
            fontSize  = 14.sp,
            textAlign = TextAlign.Center,
            modifier  = Modifier.fillMaxWidth().clickable { onNavigateToRegister() }
        )

        Spacer(modifier = Modifier.weight(0.6f))
    }
}
