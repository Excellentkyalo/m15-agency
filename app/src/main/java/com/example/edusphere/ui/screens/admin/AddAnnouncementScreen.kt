package com.example.edusphere.ui.screens.admin

import android.util.Log // ✅ Added Import for Logging
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAnnouncementScreen(onNavigateBack: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Low") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text("New Announcement") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = message, onValueChange = { message = it }, label = { Text("Message") }, modifier = Modifier.fillMaxWidth().height(150.dp))
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Low", "Medium", "High").forEach { p ->
                    FilterChip(selected = priority == p, onClick = { priority = p }, label = { Text(p) })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                if (title.isBlank() || message.isBlank()) return@Button
                isLoading = true
                scope.launch {
                    try {
                        val announcement = hashMapOf(
                            "title" to title,
                            "message" to message,
                            "priority" to priority,
                            "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
                            "date" to SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
                        )
                        db.collection("announcements").add(announcement).await()
                        onNavigateBack()
                    } catch (e: Exception) {
                        isLoading = false
                        Log.e("AddAnnouncement", "Error posting announcement", e) // ✅ Fixed: Used parameter 'e'
                    }
                }
            }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading) {
                if (isLoading) CircularProgressIndicator(color = Color.White) else Text("Post Announcement")
            }
        }
    }
}