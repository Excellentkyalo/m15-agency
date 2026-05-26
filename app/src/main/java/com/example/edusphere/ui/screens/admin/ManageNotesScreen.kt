package com.example.edusphere.ui.screens.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // ✅ Added Import for sp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

data class NoteData(
    val id: String = "",
    val subject: String = "",
    val fileUrl: String = "",
    val fileName: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageNotesScreen(onNavigateBack: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    val scope = rememberCoroutineScope()
    var notes by remember { mutableStateOf<List<NoteData>>(emptyList()) }

    var showDialog by remember { mutableStateOf(false) }
    var subject by remember { mutableStateOf("") }
    var fileUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        fileUri = uri
    }

    LaunchedEffect(Unit) {
        val snapshot = db.collection("notes").orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING).get().await()
        notes = snapshot.documents.mapNotNull { doc ->
            NoteData(
                id = doc.id,
                subject = doc.getString("subject") ?: "",
                fileUrl = doc.getString("fileUrl") ?: "",
                fileName = doc.getString("fileName") ?: ""
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Notes") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                showDialog = true
                subject = ""
                fileUri = null
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            items(notes) { item ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.subject, style = MaterialTheme.typography.titleMedium)
                            Text(item.fileName, fontSize = 12.sp, color = Color.Gray) // ✅ Now works with import
                        }
                        IconButton(onClick = {
                            scope.launch {
                                db.collection("notes").document(item.id).delete().await()
                                notes = notes.filter { it.id != item.id }
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
            title = { Text("Upload Note") },
            text = {
                Column {
                    OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { launcher.launch("application/pdf") }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (fileUri != null) "PDF Selected" else "Select PDF File")
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (subject.isBlank() || fileUri == null) return@Button
                    isUploading = true
                    scope.launch {
                        try {
                            val ref = storage.reference.child("notes/${UUID.randomUUID()}.pdf")
                            ref.putFile(fileUri!!).await()
                            val url = ref.downloadUrl.await()

                            val data = hashMapOf(
                                "subject" to subject,
                                "fileUrl" to url.toString(),
                                "fileName" to "note.pdf",
                                "timestamp" to System.currentTimeMillis()
                            )
                            db.collection("notes").add(data).await()

                            val snapshot = db.collection("notes").get().await()
                            notes = snapshot.documents.mapNotNull { doc ->
                                NoteData(doc.id, doc.getString("subject")?: "", doc.getString("fileUrl")?: "", doc.getString("fileName")?: "")
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