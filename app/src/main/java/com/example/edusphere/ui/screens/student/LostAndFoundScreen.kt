package com.example.edusphere.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.edusphere.data.repository.LostAndFoundRepository
import com.example.edusphere.data.repository.LostItem
import com.example.edusphere.ui.components.GlassCard
import com.example.edusphere.ui.components.SmartSearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostAndFoundScreen() {
    val repo = remember { LostAndFoundRepository() }

    var items by remember { mutableStateOf<List<LostItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        isLoading = true
        repo.getLostItems().onSuccess { list ->
            items = list
            isLoading = false
        }.onFailure {
            isLoading = false
        }
    }

    val filteredItems = items.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.location.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lost & Found 🔍", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF8FAFC))) {

            SmartSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search items, locations..."
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF6366F1))
                }
            } else if (filteredItems.isEmpty()) {
                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text("📭", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No items found", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filteredItems) { item ->
                        LostItemCard(item)
                    }
                }
            }
        }
    }
}

@Composable
fun LostItemCard(item: LostItem) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            // ✅ Fixed Image Loading
            // Inside LostItemCard in LostAndFoundScreen.kt
            if (item.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    error = null // ✅ Fixed: Removed composable from error param
                )
            } else {
                Box(modifier = Modifier.size(80.dp).background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                    Text("📷", fontSize = 24.sp)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(item.location, color = Color(0xFF6366F1), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(item.description, color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Found on: ${item.date}", fontSize = 10.sp, color = Color.LightGray)
            }
        }
    }
}