package com.example.edusphere.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusphere.ui.components.GlassCard
import com.example.edusphere.ui.components.PrimaryGradient

@Composable
fun ForgotPasswordScreen(onNavigateBack: () -> Unit) {
    var email by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryGradient)
    ) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.Center)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Reset Password", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Enter your email to receive a reset link.", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { /* TODO: Call ViewModel.sendResetLink(email) */ },
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Send Reset Link")
                }

                TextButton(onClick = onNavigateBack) {
                    Text("Back to Login")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordPreview() {
    ForgotPasswordScreen(onNavigateBack = {})
}