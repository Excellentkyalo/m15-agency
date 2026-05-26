package com.example.edusphere.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusphere.ui.components.GlassCard
import com.example.edusphere.ui.screens.common.BottomNav
import java.util.Calendar

data class AdminAction(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val desc: String, val route: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    currentRoute: String? = null,
    onNavigate: (String) -> Unit
) {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Good Morning"
        hour < 18 -> "Good Afternoon"
        else -> "Good Evening"
    }

    Scaffold(
        containerColor = Color(0xFFF1F5F9),
        bottomBar = {
            BottomNav(
                isAdmin = true,
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            Box(modifier = Modifier.background(Color(0xFF1E1E2E)).padding(24.dp)) {
                Column {
                    Text("$greeting, Admin 👋", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("System Overview", color = Color(0xFF9CA3AF), fontSize = 14.sp)
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(title = "Total Students", value = "120", icon = Icons.Default.People, color = Color(0xFF6366F1))
                StatCard(title = "Active Items", value = "15", icon = Icons.Default.Search, color = Color(0xFF10B981))
            }

            Text("Management Tools", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), fontWeight = FontWeight.Bold, fontSize = 18.sp)

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                val actions = listOf(
                    AdminAction("Announcements", Icons.Default.Campaign, "Manage Posts", "manage_announcements"),
                    AdminAction("Upload Notes", Icons.Default.CloudUpload, "Manage Notes", "manage_notes"),
                    AdminAction("Assignments", Icons.AutoMirrored.Filled.Assignment, "Manage Tasks", "manage_assignments"),
                    AdminAction("Enter Grades", Icons.AutoMirrored.Filled.Assignment, "Student Results", "enter_grades"),
                    AdminAction("Timetable", Icons.Default.CalendarToday, "Manage Schedule", "manage_timetable"),
                    AdminAction("Lost & Found", Icons.Default.Search, "Report Items", "add_lost_item"),
                    AdminAction("Manage Users", Icons.Default.People, "View Progress", "manage_users"),
                    AdminAction("Analytics", Icons.Default.BarChart, "User Stats", "admin_analytics")
                )

                items(actions) { action ->
                    GlassCard(
                        modifier = Modifier.height(140.dp).clickable { onNavigate(action.route) }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(action.icon, contentDescription = null, tint = Color(0xFF6366F1), modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(action.title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(action.desc, fontSize = 10.sp, color = Color.Gray, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .height(100.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(title, fontSize = 10.sp, color = Color.Gray)
            }
            Icon(icon, contentDescription = null, tint = color)
        }
    }
}