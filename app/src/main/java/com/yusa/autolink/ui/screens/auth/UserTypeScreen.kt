package com.yusa.autolink.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yusa.autolink.data.model.UserType
import com.yusa.autolink.ui.theme.*

@Composable
fun UserTypeScreen(onSelectType: (UserType) -> Unit) {
    var selected by remember { mutableStateOf<UserType?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBg)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("AutoLink", style = MaterialTheme.typography.displayMedium, color = Blue500)
        Spacer(Modifier.height(8.dp))
        Text(
            "Hesabınız nasıl kullanılacak?",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Hesap türünüzü seçerek devam edin",
            style = MaterialTheme.typography.bodyMedium,
            color = TextHint,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(48.dp))

        UserTypeCard(
            icon = Icons.Default.DirectionsCar,
            title = "Araç Sahibiyim",
            description = "Araçlarınızı yönetin, servis randevusu alın ve belgelerinizi takip edin",
            isSelected = selected == UserType.VEHICLE_OWNER,
            onClick = { selected = UserType.VEHICLE_OWNER }
        )

        Spacer(Modifier.height(16.dp))

        UserTypeCard(
            icon = Icons.Default.Build,
            title = "Servis Sağlayıcıyım",
            description = "Servis işletmenizi yönetin, randevuları takip edin ve müşteri kazanın",
            isSelected = selected == UserType.SERVICE_PROVIDER,
            onClick = { selected = UserType.SERVICE_PROVIDER }
        )

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = { selected?.let { onSelectType(it) } },
            enabled = selected != null,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Blue500,
                contentColor = TextPrimary,
                disabledContainerColor = NavyContainerHigh,
                disabledContentColor = TextHint
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Devam Et", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun UserTypeCard(
    icon: ImageVector,
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Blue500 else DividerColor
    val bgColor = if (isSelected) Blue500.copy(alpha = 0.08f) else NavySurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(if (isSelected) Blue500.copy(alpha = 0.2f) else NavyContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isSelected) Blue300 else TextHint,
                modifier = Modifier.size(26.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text(description, style = MaterialTheme.typography.bodySmall, color = TextHint)
        }
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Blue500),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", style = MaterialTheme.typography.labelSmall, color = TextPrimary)
            }
        }
    }
}
