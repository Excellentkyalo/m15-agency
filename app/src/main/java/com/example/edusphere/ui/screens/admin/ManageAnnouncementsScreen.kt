package com.example.edusphere.ui.screens.admin

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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AnnouncementData(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val priority: String = "Low",
    val timestamp: Long = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageAnnouncementsScreen(onNavigateBack: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val scope = rememberCoroutineScope()
    var announcements by remember { mutableStateOf<List<AnnouncementData>>(emptyList()) }

    var showEditDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<AnnouncementData?>(null) }
    var editTitle by remember { mutableStateOf("") }
    var editMessage by remember { mutableStateOf("") }
    var editPriority by remember { mutableStateOf("Low") }

    suspend fun refreshList() {
        val snapshot = db.collection("announcements").orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING).get().await()
        val list = snapshot.documents.mapNotNull { doc ->
            AnnouncementData(
                id = doc.id,
                title = doc.getString("title") ?: "",
                message = doc.getString("message") ?: "",
                priority = doc.getString("priority") ?: "Low",
                timestamp = doc.getLong("timestamp") ?: 0
            )
        }
        announcements = list
    }

    LaunchedEffect(Unit) {
        refreshList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Announcements") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null) // ✅ Fixed Icon
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingItem = null
                editTitle = ""
                editMessage = ""
                editPriority = "Low"
                showEditDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            items(announcements) { item ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.title, style = MaterialTheme.typography.titleMedium)
                            Text(item.message, style = MaterialTheme.typography.bodySmall)
                            Text("Priority: ${item.priority}", fontSize = 12.sp, color = if (item.priority == "High") Color.Red else Color.Gray)
                        }
                        Row {
                            IconButton(onClick = {
                                editingItem = item
                                editTitle = item.title
                                editMessage = item.message
                                editPriority = item.priority
                                showEditDialog = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                            IconButton(onClick = {
                                scope.launch {
                                    db.collection("announcements").document(item.id).delete().await()
                                    refreshList()
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

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(if (editingItem == null) "New Announcement" else "Edit Announcement") },
            text = {
                Column {
                    OutlinedTextField(value = editTitle, onValueChange = { editTitle = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editMessage, onValueChange = { editMessage = it }, label = { Text("Message") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Low", "Medium", "High").forEach { p ->
                            FilterChip(selected = editPriority == p, onClick = { editPriority = p }, label = { Text(p) })
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        val data: Map<String, Any> = mapOf(
                            "title" to editTitle,
                            "message" to editMessage,
                            "priority" to editPriority,
                            "timestamp" to System.currentTimeMillis()
                        )
                        if (editingItem == null) {
                            db.collection("announcements").add(data).await()
                        } else {
                            db.collection("announcements").document(editingItem!!.id).update(data).await()
                        }
                        refreshList()
                        showEditDialog = false
                    }
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showEditDialog = false }) { Text("Cancel") } }
        )
    }
}