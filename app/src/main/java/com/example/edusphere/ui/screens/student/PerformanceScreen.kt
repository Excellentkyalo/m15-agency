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
import kotlinx.coroutines.tasks.await
import java.util.Locale // ✅ Added Import

data class GradeRecord(val subject: String, val score: Int, val credits: Int, val gradePoint: Double)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceScreen() {
    val db = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    var grades by remember { mutableStateOf<List<GradeRecord>>(emptyList()) }
    // ✅ Optimized: Use mutableDoubleStateOf for primitive doubles
    var gpa by remember { mutableDoubleStateOf(0.0) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(uid) {
        if (uid == null) return@LaunchedEffect

        try {
            val snapshot = db.collection("grades").whereEqualTo("studentUid", uid).get().await()

            val tempGrades = snapshot.documents.mapNotNull { doc ->
                val score = (doc.getLong("score") ?: 0).toInt()
                val credits = (doc.getLong("credits") ?: 3).toInt()
                val subject = doc.getString("subject") ?: "Unknown"

                // Convert Score to Grade Point (4.0 Scale)
                val point = when {
                    score >= 90 -> 4.0
                    score >= 80 -> 3.0
                    score >= 70 -> 2.0
                    score >= 60 -> 1.0
                    else -> 0.0
                }

                GradeRecord(subject, score, credits, point)
            }

            grades = tempGrades

            // Calculate GPA
            if (tempGrades.isNotEmpty()) {
                val totalPoints = tempGrades.sumOf { it.gradePoint * it.credits }
                val totalCredits = tempGrades.sumOf { it.credits }
                gpa = if (totalCredits > 0) totalPoints / totalCredits else 0.0
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("My Performance 📊") }) }) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            Column(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF8FAFC)).padding(16.dp)) {

                GlassCard(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Current GPA", fontSize = 14.sp, color = Color.Gray)
                            // ✅ Fixed: Use Locale.getDefault()
                            Text(String.format(Locale.getDefault(), "%.2f", gpa), fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6366F1))
                            Text(if (gpa >= 3.5) "Excellent!" else if (gpa >= 2.5) "Good Job" else "Keep Trying", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Subject Breakdown", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(grades) { grade ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text(grade.subject, fontWeight = FontWeight.Bold)
                                    Text("${grade.score}% • ${grade.credits} Credits", fontSize = 12.sp, color = Color.Gray)
                                }
                                Surface(color = Color(0xFF6366F1).copy(alpha = 0.1f), shape = MaterialTheme.shapes.small) {
                                    Text("${grade.gradePoint}", color = Color(0xFF6366F1), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}