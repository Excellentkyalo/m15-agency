package com.example.edusphere.ui.screens.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // ✅ Updated Import
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

data class AssignmentData(
    val id: String = "",
    val title: String = "",
    val deadline: String = "",
    val fileUrl: String = "",
    val fileName: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageAssignmentsScreen(onNavigateBack: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    val scope = rememberCoroutineScope()
    var assignments by remember { mutableStateOf<List<AssignmentData>>(emptyList()) }

    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("") }
    var fileUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        fileUri = uri
    }

    LaunchedEffect(Unit) {
        val snapshot = db.collection("assignments").orderBy("deadline").get().await()
        assignments = snapshot.documents.mapNotNull { doc ->
            AssignmentData(
                id = doc.id,
                title = doc.getString("title") ?: "",
                deadline = doc.getString("deadline") ?: "",
                fileUrl = doc.getString("fileUrl") ?: "",
                fileName = doc.getString("fileName") ?: ""
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Assignments") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null) // ✅ Fixed Icon
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                showDialog = true
                title = ""
                deadline = ""
                fileUri = null
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            items(assignments) { item ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.title, style = MaterialTheme.typography.titleMedium)
                            Text("Due: ${item.deadline}", fontSize = 12.sp, color = Color.Gray)
                        }
                        IconButton(onClick = {
                            scope.launch {
                                db.collection("assignments").document(item.id).delete().await()
                                assignments = assignments.filter { it.id != item.id }
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("New Assignment") },
            text = {
                Column {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = deadline, onValueChange = { deadline = it }, label = { Text("Deadline (e.g., 2024-12-31)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { launcher.launch("application/pdf") }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (fileUri != null) "PDF Selected" else "Select PDF File")
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (title.isBlank() || fileUri == null) return@Button
                    isUploading = true
                    scope.launch {
                        try {
                            val ref = storage.reference.child("assignments/${UUID.randomUUID()}.pdf")
                            ref.putFile(fileUri!!).await()
                            val url = ref.downloadUrl.await()

                            val data = hashMapOf(
                                "title" to title,
                                "deadline" to deadline,
                                "fileUrl" to url.toString(),
                                "fileName" to "assignment.pdf",
                                "timestamp" to System.currentTimeMillis()
                            )
                            db.collection("assignments").add(data).await()

                            val snapshot = db.collection("assignments").get().await()
                            assignments = snapshot.documents.mapNotNull { doc ->
                                AssignmentData(
                                    doc.id,
                                    doc.getString("title") ?: "",
                                    doc.getString("deadline") ?: "",
                                    doc.getString("fileUrl") ?: "",
                                    doc.getString("fileName") ?: ""
                                )
                            }
                            showDialog = false
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            isUploading = false
                        }
                    }
                }, enabled = !isUploading) {
                    if(isUploading) CircularProgressIndicator(color=Color.White) else Text("Upload")
                }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } }
        )
    }
}