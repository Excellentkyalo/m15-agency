package com.example.edusphere.ui.screens.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

@Composable
fun UploadNotesScreen(onNavigateBack: () -> Unit) {
    // ✅ Removed unused context
    val storageRef = FirebaseStorage.getInstance().reference
    val db = FirebaseFirestore.getInstance() // ✅ Added Firestore instance
    val scope = rememberCoroutineScope()

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedUri = uri
        fileName = uri?.lastPathSegment ?: "file.pdf"
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Upload Study Note", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { launcher.launch("*/*") }, modifier = Modifier.fillMaxWidth()) {
            Text(if (selectedUri != null) "File Selected: $fileName" else "Select PDF/File")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (selectedUri == null || subject.isBlank()) return@Button
            isUploading = true
            scope.launch {
                try {
                    // 1. Upload to Firebase Storage
                    val ref = storageRef.child("notes/${UUID.randomUUID()}.pdf")
                    ref.putFile(selectedUri!!).await()
                    val downloadUrl = ref.downloadUrl.await()

                    // 2. Save Metadata to Firestore 'notes' collection
                    val noteData = hashMapOf(
                        "subject" to subject,
                        "fileUrl" to downloadUrl.toString(),
                        "fileName" to fileName,
                        "timestamp" to System.currentTimeMillis()
                    )
                    db.collection("notes").add(noteData).await()

                    isUploading = false
                    onNavigateBack()
                } catch (e: Exception) {
                    isUploading = false
                    e.printStackTrace() // ✅ Fixed: Used parameter 'e'
                }
            }
        }, modifier = Modifier.fillMaxWidth(), enabled = !isUploading) {
            if (isUploading) CircularProgressIndicator(color = Color.White) else Text("Upload to Cloud")
        }
    }
}