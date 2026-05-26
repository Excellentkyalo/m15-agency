package com.example.edusphere.ui.screens.student

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
import java.text.SimpleDateFormat
import java.util.*

data class ClassSession(val day: String, val time: String, val subject: String, val room: String)

@OptIn(ExperimentalMaterial3Api::class) // ✅ Added OptIn to suppress experimental warning
@Composable
fun TimetableScreen() {
    val db = FirebaseFirestore.getInstance()
    var sessions by remember { mutableStateOf<List<ClassSession>>(emptyList()) }
    var currentDay by remember { mutableStateOf("") }
    var currentTime by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            // Get Current Day and Time
            val cal = Calendar.getInstance()
            val days = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
            currentDay = days[cal.get(Calendar.DAY_OF_WEEK)]
            currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

            // Fetch Timetable
            val snapshot = db.collection("timetable").get().await()
            sessions = snapshot.documents.mapNotNull { doc ->
                ClassSession(
                    day = doc.getString("day") ?: "",
                    time = doc.getString("time") ?: "",
                    subject = doc.getString("subject") ?: "",
                    room = doc.getString("room") ?: ""
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("My Timetable 📅") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF8FAFC)).padding(16.dp)) {

            // Current Status Card
            GlassCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Current Time", fontSize = 12.sp, color = Color.Gray)
                    Text("$currentDay, $currentTime", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }

            Text("Schedule", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(sessions) { session ->
                    val isToday = session.day == currentDay
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = if (isToday) Color(0xFFE0E7FF) else Color.White)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(session.subject, fontWeight = FontWeight.Bold, color = if(isToday) Color(0xFF6366F1) else Color.Black)
                                Text("${session.time} • ${session.room}", fontSize = 12.sp, color = Color.Gray)
                            }
                            if (isToday) Text("TODAY", color = Color(0xFF6366F1), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}