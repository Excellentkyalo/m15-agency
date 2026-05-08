package com.example.edusphere.ui.screens.admin

import android.net.Uri // ✅ Needed for imageUri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.* // ✅ Covers Spacer, Padding, FillMaxSize, etc.
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.* // ✅ Covers Scaffold, Text, Button, etc.
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusphere.data.repository.LostAndFoundRepository
import com.example.edusphere.data.repository.LostItem
import com.example.edusphere.ui.components.GlassCard
import com.example.edusphere.ui.theme.EduSphereTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLostItemScreen(onNavigateBack: () -> Unit) {
    val repo = remember { LostAndFoundRepository() }
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Lost Item", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("←", fontSize = 24.sp)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Item Details", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Item Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location Found") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (imageUri != null) "Image Selected ✅" else "Upload Image 📷")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (name.isBlank() || location.isBlank()) return@Button
                            isUploading = true

                            scope.launch {
                                val imageUrl = imageUri?.toString() ?: ""
                                val date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
                                val item = LostItem(
                                    name = name,
                                    description = desc,
                                    location = location,
                                    date = date,
                                    imageUrl = imageUrl
                                )

                                repo.addLostItem(item).onSuccess {
                                    isUploading = false
                                    onNavigateBack()
                                }.onFailure {
                                    isUploading = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isUploading
                    ) {
                        if (isUploading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Submit Report")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddLostItemPreview() {
    EduSphereTheme {
        AddLostItemScreen(onNavigateBack = {})
    }
}