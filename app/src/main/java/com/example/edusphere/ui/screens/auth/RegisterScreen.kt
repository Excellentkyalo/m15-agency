package com.example.edusphere.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel // ✅ Important Import
import com.example.edusphere.navigation.UserRole
import com.example.edusphere.ui.components.GlassCard
import com.example.edusphere.ui.components.PrimaryGradient
import com.example.edusphere.ui.theme.EduSphereTheme
import com.example.edusphere.viewmodel.AuthViewModel // ✅ Important Import

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = viewModel() // ✅ Initialize ViewModel
) {
    var step by remember { mutableIntStateOf(1) }

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // ✅ Hardcoded Role
    val role = UserRole.STUDENT

    var isPasswordVisible by remember { mutableStateOf(false) }

    // ✅ Observe State
    val authState by viewModel.authState.collectAsState()

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        disabledContainerColor = Color.White,
        focusedBorderColor = Color(0xFF6366F1),
        unfocusedBorderColor = Color.LightGray
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryGradient)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )

        GlassCard(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .align(Alignment.Center)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Create Account", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E1E2E))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Join EduSphere Today", color = Color(0xFF4B5563), fontSize = 16.sp)
                Spacer(modifier = Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = { step / 2f },
                    modifier = Modifier.fillMaxWidth().height(6.dp),
                    color = Color(0xFF6366F1),
                    trackColor = Color.Gray.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Step 1: Personal Info
                if (step == 1) {
                    Text("Step 1: Personal Details", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text("First Name", fontSize = 16.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                        colors = textFieldColors
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Last Name", fontSize = 16.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                        colors = textFieldColors
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address", fontSize = 16.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                        colors = textFieldColors
                    )
                }

                // Step 2: Security
                if (step == 2) {
                    Text("Step 2: Set Password", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", fontSize = 16.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                                    tint = Color.Gray
                                )
                            }
                        },
                        colors = textFieldColors
                    )

                    // Show Error
                    authState.error?.let { error ->
                        Text(error, color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Navigation Buttons
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    if (step > 1) {
                        TextButton(onClick = { step-- }) {
                            Text("Back", fontSize = 16.sp)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Button(
                        onClick = {
                            if (step < 2) {
                                step++
                            } else {
                                // ✅ Call ViewModel Register
                                viewModel.register(email, password, "$firstName $lastName", role)
                            }
                        },
                        modifier = Modifier.height(50.dp),
                        enabled = !authState.isLoading
                    ) {
                        if (authState.isLoading && step == 2) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(if (step == 2) "Register" else "Next", fontSize = 16.sp)
                        }
                    }
                }

                // Navigate on Success
                LaunchedEffect(authState.isAuthenticated) {
                    if (authState.isAuthenticated) {
                        onNavigateToLogin()
                    }
                }

                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        "Already have an account? Login",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    EduSphereTheme {
        RegisterScreen(onNavigateToLogin = {})
    }
}