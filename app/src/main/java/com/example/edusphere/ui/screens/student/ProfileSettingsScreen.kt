package com.example.edusphere.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edusphere.ui.components.GlassCard
import com.example.edusphere.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val profile by viewModel.profile.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    var isEditing by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(profile.name) }

    Scaffold(topBar = { TopAppBar(title = { Text("Profile 👤") }) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = androidx.compose.foundation.shape.CircleShape,
                        color = Color(0xFF6366F1)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(profile.name.take(2).uppercase(), color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    if (isEditing) {
                        OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth(0.8f))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { viewModel.updateUserName(newName); isEditing = false }) { Text("Save") }
                            TextButton(onClick = { isEditing = false }) { Text("Cancel") }
                        }
                    } else {
                        Text(profile.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text(profile.email, fontSize = 14.sp, color = Color.Gray)
                        TextButton(onClick = { isEditing = true }) { Text("Edit Profile") }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Preferences", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DarkMode, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Dark Mode")
                        }
                        Switch(checked = isDarkMode, onCheckedChange = { viewModel.toggleDarkMode(it) })
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(modifier = Modifier.fillMaxWidth().clickable { /* TODO */ }, horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Notifications")
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.toggleDarkMode(false) // Reset theme on logout if desired
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Logout")
            }
        }
    }
}