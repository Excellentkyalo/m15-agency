package com.example.edusphere.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusphere.ui.theme.EduSphereTheme
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// ✅ UNIQUE Data Class Name
data class GradeStudentData(val uid: String, val name: String, val email: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterGradesScreen() {
    val db = FirebaseFirestore.getInstance()
    val scope = rememberCoroutineScope()

    var students by remember { mutableStateOf<List<GradeStudentData>>(emptyList()) }
    var selectedStudent by remember { mutableStateOf<GradeStudentData?>(null) }

    var subject by remember { mutableStateOf("") }
    var score by remember { mutableStateOf("") }
    var credits by remember { mutableStateOf("3") }
    var message by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val snapshot = db.collection("users")
                .whereEqualTo("role", "student")
                .get()
                .await()

            val tempList = mutableListOf<GradeStudentData>()
            for (doc in snapshot.documents) {
                val name = doc.getString("name") ?: "Unknown"
                val email = doc.getString("email") ?: ""
                tempList.add(GradeStudentData(doc.id, name, email))
            }
            students = tempList
        } catch (e: Exception) {
            message = "Error: ${e.message}"
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Enter Grades 📝") }) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(16.dp)
        ) {

            Text("Select Student", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.White, shape = MaterialTheme.shapes.medium)
                    .verticalScroll(rememberScrollState())
            ) {
                Column {
                    if (students.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No students found", color = Color.Gray)
                        }
                    } else {
                        students.forEach { student ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedStudent = student
                                        message = ""
                                    }
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(student.name, fontWeight = FontWeight.Medium)
                                    Text(student.email, fontSize = 12.sp, color = Color.Gray)
                                }
                                if (selectedStudent?.uid == student.uid) {
                                    Text("✅", color = Color.Green)
                                }
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }

            if (selectedStudent != null) {
                Spacer(modifier = Modifier.height(16.dp))

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Grade for: ${selectedStudent!!.name}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF6366F1))
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = score, onValueChange = { if (it.all { c -> c.isDigit() }) score = it }, label = { Text("Score (%)") }, modifier = Modifier.weight(1f), singleLine = true)
                            OutlinedTextField(value = credits, onValueChange = { if (it.all { c -> c.isDigit() }) credits = it }, label = { Text("Credits") }, modifier = Modifier.weight(1f), singleLine = true)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ✅ RECTIFIED BUTTON WITH SAFE TYPES
                        Button(
                            onClick = {
                                if (subject.isBlank() || score.isBlank()) {
                                    message = "Please fill in all fields"
                                    return@Button
                                }

                                // ✅ Safe conversion for credits
                                val creditValue = credits.toIntOrNull() ?: 3

                                isLoading = true
                                scope.launch {
                                    try {
                                        // ✅ Explicitly typed HashMap to avoid Serializable errors
                                        val gradeMap: HashMap<String, Any> = hashMapOf(
                                            "studentUid" to selectedStudent!!.uid,
                                            "studentName" to selectedStudent!!.name,
                                            "subject" to subject,
                                            "score" to score.toInt(),
                                            "credits" to creditValue,
                                            "term" to "Current Term",
                                            "timestamp" to System.currentTimeMillis()
                                        )

                                        db.collection("grades").add(gradeMap).await()

                                        message = "Grade Saved Successfully! ✅"
                                        subject = ""
                                        score = ""
                                        credits = "3"
                                    } catch (e: Exception) {
                                        message = "Error: ${e.message}"
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading
                        ) {
                            if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp)) else Text("Save Grade")
                        }

                        if (message.isNotEmpty()) {
                            Text(message, color = if (message.contains("✅")) Color.Green else Color.Red, modifier = Modifier.padding(top = 8.dp), fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

// ✅ PREVIEW ADDED HERE
@Preview(showBackground = true)
@Composable
fun EnterGradesScreenPreview() {
    EduSphereTheme {
        EnterGradesScreen()
    }
}