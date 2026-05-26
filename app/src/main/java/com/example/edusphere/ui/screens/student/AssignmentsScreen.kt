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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

data class AssignmentItem(
    val id: String,
    val title: String,
    val deadline: String,
    val fileUrl: String,
    val isSubmitted: Boolean
)

@OptIn(ExperimentalMaterial3Api::class) // ✅ Added OptIn to suppress experimental warning
@Composable
fun AssignmentsScreen() {
    val db = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val scope = rememberCoroutineScope()

    var assignments by remember { mutableStateOf<List<AssignmentItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(uid) {
        if (uid == null) return@LaunchedEffect

        try {
            // Fetch Assignments
            val assignSnapshot = db.collection("assignments").get().await()
            val assignList = assignSnapshot.documents.mapNotNull { doc ->
                doc.id to mapOf(
                    "title" to (doc.getString("title") ?: ""),
                    "deadline" to (doc.getString("deadline") ?: ""),
                    "fileUrl" to (doc.getString("fileUrl") ?: "")
                )
            }.toMap()

            // Fetch Submissions
            val subSnapshot = db.collection("submissions").whereEqualTo("studentUid", uid).get().await()
            val submittedIds = subSnapshot.documents.mapNotNull { it.getString("assignmentId") }.toSet()

            assignments = assignList.map { (id, data) ->
                AssignmentItem(
                    id = id,
                    title = data["title"] as String,
                    deadline = data["deadline"] as String,
                    fileUrl = data["fileUrl"] as String,
                    isSubmitted = submittedIds.contains(id)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("My Assignments 📝") }) }) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF8FAFC)).padding(16.dp)) {
                items(assignments) { item ->
                    val isOverdue = isDatePassed(item.deadline) && !item.isSubmitted
                    GlassCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(item.title, fontWeight = FontWeight.Bold)
                                if (item.isSubmitted) {
                                    Text("Submitted ✅", color = Color.Green, fontWeight = FontWeight.Bold)
                                } else if (isOverdue) {
                                    Text("Overdue ❌", color = Color.Red, fontWeight = FontWeight.Bold)
                                } else {
                                    Text("Pending", color = Color.Gray)
                                }
                            }
                            Text("Due: ${item.deadline}", fontSize = 12.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))

                            if (!item.isSubmitted) {
                                Button(onClick = {
                                    scope.launch {
                                        try {
                                            // Simulate Submission
                                            db.collection("submissions").add(hashMapOf(
                                                "assignmentId" to item.id,
                                                "studentUid" to uid,
                                                "timestamp" to System.currentTimeMillis()
                                            )).await()
                                            // Refresh local state
                                            assignments = assignments.map { if(it.id == item.id) it.copy(isSubmitted = true) else it }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                }, modifier = Modifier.fillMaxWidth()) {
                                    Text("Submit Assignment")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun isDatePassed(deadlineStr: String): Boolean {
    return try {
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val deadline = fmt.parse(deadlineStr)
        val now = Date()
        deadline != null && now.after(deadline)
    } catch (e: Exception) {
        false
    }
}