package com.example.edusphere.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val PrimaryGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6), Color(0xFFA855F7))
)

@Composable
fun GlassCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(16.dp), clip = false)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.15f)),
        color = Color.Transparent,
        shape = RoundedCornerShape(16.dp)
    ) {
        content()
    }
}

@Composable
fun GradientHeader(title: String, subtitle: String, modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.Box(modifier = modifier.then(Modifier.background(PrimaryGradient).padding(20.dp))) {
        androidx.compose.material3.Text(text = title, color = Color.White, fontSize = androidx.compose.ui.unit.TextUnit(24f, androidx.compose.ui.unit.TextUnitType.Sp))
        androidx.compose.material3.Text(text = subtitle, color = Color.White.copy(alpha = 0.9f), fontSize = androidx.compose.ui.unit.TextUnit(14f, androidx.compose.ui.unit.TextUnitType.Sp))
    }
}