package com.example.tegram.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.tegram.ui.theme.*

@Composable
fun TegramBackground(
    modifier: Modifier = Modifier,
    scrollable: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val scrollState = rememberScrollState()
    val baseModifier = modifier
        .fillMaxSize()
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(TegramDarkBlue, TegramMediumBlue, TegramLightBlue)
            )
        )
    
    val finalModifier = if (scrollable) baseModifier.verticalScroll(scrollState) else baseModifier

    Column(
        modifier = finalModifier.padding(20.dp),
        verticalArrangement = Arrangement.Top,
        content = content
    )
}

@Composable
fun TegramCard(
    modifier: Modifier = Modifier,
    isDark: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val contentColor = if (isDark) Color.White else TegramTextPrimary
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) TegramCardDark else TegramCardLight,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}

@Composable
fun TegramButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isOutlined: Boolean = false,
    onDarkBackground: Boolean = true
) {
    if (isOutlined) {
        val color = if (onDarkBackground) Color.White else TegramLightBlue
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            enabled = enabled,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = color
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
        ) {
            Text(text, fontWeight = FontWeight.SemiBold)
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            enabled = enabled,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TegramLightBlue,
                contentColor = Color.White
            )
        ) {
            Text(text, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TegramTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onDarkBackground: Boolean = true
) {
    val textColor = if (onDarkBackground) Color.White else TegramTextPrimary
    val borderColor = if (onDarkBackground) Color.White.copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.5f)
    val labelColor = if (onDarkBackground) Color.White.copy(alpha = 0.7f) else TegramTextSecondary

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TegramLightBlue,
            unfocusedBorderColor = borderColor,
            focusedLabelColor = TegramLightBlue,
            unfocusedLabelColor = labelColor,
            focusedTextColor = textColor,
            unfocusedTextColor = textColor
        )
    )
}

@Composable
fun TegramSectionTitle(
    text: String,
    color: Color = Color.White
) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
