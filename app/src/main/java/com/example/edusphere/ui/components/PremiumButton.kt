package com.example.edusphere.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun PremiumButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // ✅ Micro-interaction: Scale down slightly when pressed
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.95f else 1f, label = "scale")

    Box(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
    ) {
        Button(
            onClick = {}, // Handled by Box clickable
            modifier = Modifier.matchParentSize(),
            interactionSource = interactionSource,
            colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color(0xFF6366F1))
        ) {
            content()
        }
    }
}