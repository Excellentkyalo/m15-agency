package com.example.edusphere.ui.screens.admin

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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageLostItemsScreen(onNavigateBack: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val scope = rememberCoroutineScope()
    var items by remember { mutableStateOf<List<Pair<String, Map<String, Any>>>>(emptyList()) }

    // Edit State
    var showEditDialog by remember { mutableStateOf(false) }
    var editingId by remember { mutableStateOf<String?>(null) }
    var editName by remember { mutableStateOf("") }
    var editLocation by remember { mutableStateOf("") }
    var editDesc by remember { mutableStateOf("") }

    // ✅ Define refreshList inside the Composable scope
    suspend fun refreshList() {
        val snapshot = db.collection("lost_items").get().await()
        items = snapshot.documents.map { it.id to (it.data ?: emptyMap()) }
    }

    LaunchedEffect(Unit) {
        refreshList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Lost Items") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            items(items) { (id, item) ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item["name"] as? String ?: "No Name", style = MaterialTheme.typography.titleMedium)
                            Text(item["location"] as? String ?: "", style = MaterialTheme.typography.bodySmall)
                        }
                        Row {
                            IconButton(onClick = {
                                editingId = id
                                editName = item["name"] as? String ?: ""
                                editLocation = item["location"] as? String ?: ""
                                editDesc = item["description"] as? String ?: ""
                                showEditDialog = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                            IconButton(onClick = {
                                scope.launch {
                                    db.collection("lost_items").document(id).delete().await()
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
            title = { Text("Edit Item") },
            text = {
                Column {
                    OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editLocation, onValueChange = { editLocation = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editDesc, onValueChange = { editDesc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        // ✅ Fixed: Use mapOf with explicit types to avoid HashMap mismatch
                        val updates: Map<String, Any> = mapOf(
                            "name" to editName,
                            "location" to editLocation,
                            "description" to editDesc
                        )
                        db.collection("lost_items").document(editingId!!).update(updates).await()
                        refreshList()
                        showEditDialog = false
                    }
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showEditDialog = false }) { Text("Cancel") } }
        )
    }
}