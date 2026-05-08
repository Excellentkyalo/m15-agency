package com.example.edusphere.ui.screens.admin

import androidx.compose.foundation.background // ✅ Added Missing Import
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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class UserStats(val total: Int, val students: Int, val admins: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAnalyticsScreen() {
    val db = FirebaseFirestore.getInstance()
    var users by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var stats by remember { mutableStateOf(UserStats(0, 0, 0)) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val snapshot = db.collection("users").get().await()
        val userList = snapshot.documents.map { it.data ?: emptyMap() }
        users = userList

        val students = userList.count { it["role"] == "student" }
        val admins = userList.count { it["role"] == "admin" }
        stats = UserStats(userList.size, students, admins)

        isLoading = false
    }

    Scaffold(topBar = { TopAppBar(title = { Text("User Analytics 📊") }) }) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            Column(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF8FAFC)).padding(16.dp)) {

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatBox(title = "Total Users", value = stats.total.toString(), color = Color(0xFF6366F1))
                    StatBox(title = "Students", value = stats.students.toString(), color = Color(0xFF10B981))
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text("User List", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(users) { user ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text(user["name"] as? String ?: "Unknown", fontWeight = FontWeight.Bold)
                                    Text(user["email"] as? String ?: "", fontSize = 12.sp, color = Color.Gray)
                                }
                                Surface(color = if (user["role"] == "admin") Color(0xFF6366F1) else Color(0xFF10B981), shape = MaterialTheme.shapes.small) {
                                    Text((user["role"] as? String ?: "user").uppercase(), color = Color.White, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(title: String, value: String, color: Color) {
    Card(modifier = Modifier.fillMaxWidth(0.5f).height(100.dp)) { // ✅ Fixed weight error
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
                Text(title, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}