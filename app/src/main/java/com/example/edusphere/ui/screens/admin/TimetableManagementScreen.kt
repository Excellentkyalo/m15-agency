package com.example.edusphere.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ClassSession(
    val id: String = "",
    val day: String = "Monday",
    val time: String = "08:00",
    val subject: String = "",
    val room: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableManagementScreen() {
    val db = FirebaseFirestore.getInstance()
    val scope = rememberCoroutineScope()

    var sessions by remember { mutableStateOf<List<ClassSession>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var showDialog by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableStateOf("Monday") }
    var inputTime by remember { mutableStateOf("08:00") }
    var inputSubject by remember { mutableStateOf("") }
    var inputRoom by remember { mutableStateOf("") }

    // ✅ Helper function to fetch data cleanly
    suspend fun fetchTimetable(): List<ClassSession> {
        val snapshot = db.collection("timetable").get().await()
        return snapshot.documents.mapNotNull { doc ->
            ClassSession(
                id = doc.id,
                day = doc.getString("day") ?: "Monday",
                time = doc.getString("time") ?: "08:00",
                subject = doc.getString("subject") ?: "",
                room = doc.getString("room") ?: ""
            )
        }
    }

    // ✅ Fetch Data on Load
    LaunchedEffect(Unit) {
        try {
            sessions = fetchTimetable()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Manage Timetable 📅") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF8FAFC))) {

            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Button(onClick = { showDialog = true }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Class Session")
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
                    items(days) { day ->
                        val daySessions = sessions.filter { it.day == day }
                        if (daySessions.isNotEmpty()) {
                            Text(day, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp))
                            daySessions.forEach { session ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Column {
                                            Text(session.subject, fontWeight = FontWeight.Bold)
                                            Text("${session.time} • ${session.room}", fontSize = 12.sp, color = Color.Gray)
                                        }
                                        IconButton(onClick = {
                                            scope.launch {
                                                try {
                                                    db.collection("timetable").document(session.id).delete().await()
                                                    // ✅ Update state directly with returned list
                                                    sessions = fetchTimetable()
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("New Class Session") },
            text = {
                Column {
                    OutlinedTextField(value = selectedDay, onValueChange = { selectedDay = it }, label = { Text("Day") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = inputTime, onValueChange = { inputTime = it }, label = { Text("Time") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = inputSubject, onValueChange = { inputSubject = it }, label = { Text("Subject") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = inputRoom, onValueChange = { inputRoom = it }, label = { Text("Room") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (inputSubject.isBlank()) return@Button

                    // ✅ Capture values to avoid warnings
                    val dayVal = selectedDay
                    val timeVal = inputTime
                    val subjectVal = inputSubject
                    val roomVal = inputRoom

                    // ✅ Reset form immediately
                    showDialog = false
                    inputSubject = ""
                    inputRoom = ""
                    inputTime = "08:00"
                    selectedDay = "Monday"

                    scope.launch {
                        try {
                            val newSession = hashMapOf(
                                "day" to dayVal,
                                "time" to timeVal,
                                "subject" to subjectVal,
                                "room" to roomVal
                            )
                            db.collection("timetable").add(newSession).await()
                            // ✅ Update state directly with returned list
                            sessions = fetchTimetable()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } }
        )
    }
}