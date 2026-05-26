package com.example.edusphere.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

data class StudentProfile(val name: String, val email: String, val uid: String)

@OptIn(ExperimentalMaterial3Api::class) // ✅ Placed AFTER package/imports
@Composable
fun ManageUsersScreen() {
    val db = FirebaseFirestore.getInstance()
    var students by remember { mutableStateOf<List<StudentProfile>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val snapshot = db.collection("users").whereEqualTo("role", "student").get().await()
        students = snapshot.documents.mapNotNull { doc ->
            StudentProfile(
                name = doc.getString("name") ?: "Unknown",
                email = doc.getString("email") ?: "",
                uid = doc.id
            )
        }
        isLoading = false
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Manage Students 🎓") }) }) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF8FAFC)).padding(16.dp)) {
                items(students) { student ->
                    GlassCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            CircleAvatar(initials = student.name.take(2))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(student.name, fontWeight = FontWeight.Bold)
                                Text(student.email, fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CircleAvatar(initials: String) {
    Box(
        modifier = Modifier.size(40.dp).background(Color(0xFF6366F1), shape = androidx.compose.foundation.shape.CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(initials.uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
    }
}