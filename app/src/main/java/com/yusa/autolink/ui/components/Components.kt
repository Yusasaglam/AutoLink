package com.yusa.autolink.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.yusa.autolink.data.model.ButtonVariant
import com.yusa.autolink.data.model.ChipType
import com.yusa.autolink.ui.theme.*

val CardShape = RoundedCornerShape(16.dp)
val ButtonShape = RoundedCornerShape(12.dp)
val InputShape = RoundedCornerShape(12.dp)
val ChipShape = RoundedCornerShape(8.dp)

@Composable
fun AutoLinkButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.Primary
) {
    when (variant) {
        ButtonVariant.Primary -> Button(
            onClick = onClick,
            enabled = enabled,
            shape = ButtonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Blue500,
                contentColor = TextPrimary,
                disabledContainerColor = NavyContainerHigh,
                disabledContentColor = TextHint
            ),
            modifier = modifier.height(52.dp)
        ) {
            Text(text, style = MaterialTheme.typography.labelLarge)
        }

        ButtonVariant.Outline -> OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            shape = ButtonShape,
            border = BorderStroke(1.dp, if (enabled) Blue500 else DividerColor),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Blue500),
            modifier = modifier.height(52.dp)
        ) {
            Text(text, style = MaterialTheme.typography.labelLarge)
        }

        ButtonVariant.Ghost -> TextButton(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier.height(52.dp)
        ) {
            Text(text, style = MaterialTheme.typography.labelLarge, color = Blue300)
        }
    }
}

@Composable
fun AutoLinkTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: ImageVector? = null,
    errorMessage: String? = null,
    singleLine: Boolean = true
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        isError = errorMessage != null,
        shape = InputShape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Blue500,
            unfocusedBorderColor = DividerColor,
            errorBorderColor = ErrorRed,
            focusedLabelColor = Blue300,
            unfocusedLabelColor = TextHint,
            cursorColor = Blue500,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = NavyContainer,
            unfocusedContainerColor = NavyContainer,
            errorContainerColor = NavyContainer
        ),
        visualTransformation = if (isPassword && !passwordVisible)
            PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null, tint = TextHint) } },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = TextHint
                    )
                }
            }
        } else null,
        supportingText = errorMessage?.let { { Text(it, color = ErrorRed) } }
    )
}

@Composable
fun AutoLinkCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val baseModifier = modifier
        .clip(CardShape)
        .background(NavySurface)
        .border(1.dp, DividerColor.copy(alpha = 0.5f), CardShape)
        .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)

    Column(modifier = baseModifier.padding(16.dp), content = content)
}

@Composable
fun StatusChip(text: String, type: ChipType) {
    val (bg, fg) = when (type) {
        ChipType.Info -> Blue500.copy(alpha = 0.15f) to Blue300
        ChipType.Warning -> Orange500.copy(alpha = 0.15f) to Orange300
        ChipType.Success -> SuccessGreen.copy(alpha = 0.15f) to SuccessGreen
        ChipType.Error -> ErrorRed.copy(alpha = 0.15f) to ErrorRed
    }
    Box(
        modifier = Modifier
            .clip(ChipShape)
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelSmall, color = fg)
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        if (actionText != null && onAction != null) {
            Text(
                actionText,
                style = MaterialTheme.typography.labelLarge,
                color = Blue300,
                modifier = Modifier.clickable(onClick = onAction)
            )
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(CardShape)
            .background(NavyContainer)
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, contentDescription = label, tint = Blue300, modifier = Modifier.size(24.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}
