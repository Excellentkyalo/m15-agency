package com.example.edusphere.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusphere.ui.components.GlassCard
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(onNavigateToAddNote: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    var notes by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val snapshot = db.collection("notes").orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING).get().await()
            notes = snapshot.documents.map { it.data ?: emptyMap() }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Study Notes 📚") }) },
        // ✅ Added FAB to use the onNavigateToAddNote parameter
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddNote) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF8FAFC)).padding(16.dp)) {
                items(notes) { note ->
                    GlassCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF6366F1))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(note["subject"] as? String ?: "Subject", fontWeight = FontWeight.Bold)
                                    Text("Uploaded by Admin", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                            TextButton(onClick = {
                                // TODO: Open PDF Link using note["fileUrl"]
                            }) {
                                Text("View", color = Color(0xFF6366F1))
                            }
                        }
                    }
                }
            }
        }
    }
}