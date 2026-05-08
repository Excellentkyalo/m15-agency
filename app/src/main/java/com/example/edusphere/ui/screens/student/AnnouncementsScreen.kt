package com.example.edusphere.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment // ✅ Added Import
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusphere.ui.components.GlassCard
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class) // ✅ Added OptIn
@Composable
fun AnnouncementsScreen() {
    val db = FirebaseFirestore.getInstance()
    var announcements by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val snapshot = db.collection("announcements").orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING).get().await()
        announcements = snapshot.documents.map { it.data ?: emptyMap() }
        isLoading = false
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Announcements 📢") }) }) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { // ✅ Fixed Alignment
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF8FAFC)).padding(16.dp)) {
                items(announcements) { item ->
                    GlassCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(item["title"] as? String ?: "No Title", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(item["date"] as? String ?: "", fontSize = 12.sp, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(item["message"] as? String ?: "", fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(color = Color(0xFF6366F1).copy(alpha = 0.1f), shape = MaterialTheme.shapes.small) {
                                Text("Priority: ${item["priority"]}", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 12.sp, color = Color(0xFF6366F1))
                            }
                        }
                    }
                }
            }
        }
    }
}