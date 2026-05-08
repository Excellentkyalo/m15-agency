package com.example.edusphere.ui.screens.student

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edusphere.ui.components.GlassCard
import com.example.edusphere.ui.components.PrimaryGradient
import com.example.edusphere.ui.screens.common.BottomNav
import com.example.edusphere.ui.theme.EduSphereTheme
import com.example.edusphere.viewmodel.ProfileViewModel
import java.util.Calendar

data class DashboardAction(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val route: String? = null)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardScreen(
    currentRoute: String? = null,
    onNavigate: (String) -> Unit = {},
    onNavigateToNotes: () -> Unit = {},
    onNavigateToAssignments: () -> Unit = {},
    onNavigateToTimetable: () -> Unit = {},
    onNavigateToPerformance: () -> Unit = {},
    onNavigateToAnnouncements: () -> Unit = {},
    // ✅ Removed unused onNavigateToSettings parameter
    onNavigateToLostFound: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel()
) {
    val profile by viewModel.profile.collectAsState()
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Good Morning"
        hour < 18 -> "Good Afternoon"
        else -> "Good Evening"
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        bottomBar = {
            BottomNav(
                isAdmin = false,
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToNotes, containerColor = Color(0xFF6366F1)) {
                Icon(Icons.Default.Add, contentDescription = "Quick Add Note", tint = Color.White)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            Box(modifier = Modifier.background(PrimaryGradient).padding(24.dp)) {
                Column {
                    Text("$greeting, ${profile.name.split(" ").first()} 👋", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                            Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("5-day study streak! 🔥", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White, modifier = Modifier.align(Alignment.TopEnd).size(24.dp))
            }

            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(title = "Pending", value = "3", icon = Icons.AutoMirrored.Filled.Assignment, color = Color(0xFF6366F1))
                StatCard(title = "Streak", value = "5 Days", icon = Icons.Default.LocalFireDepartment, color = Color(0xFFFFC107))
            }

            Text("Quick Actions", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), fontWeight = FontWeight.Bold, fontSize = 18.sp)

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val actions = listOf(
                    DashboardAction("Notes", Icons.Default.Description, "notes"),
                    DashboardAction("Assignments", Icons.AutoMirrored.Filled.Assignment, "assignments"),
                    DashboardAction("Timetable", Icons.Default.CalendarToday, "timetable"),
                    DashboardAction("Performance", Icons.Default.BarChart, "performance"),
                    DashboardAction("Announcements", Icons.Default.Campaign, "announcements"),
                    DashboardAction("Lost & Found", Icons.Default.Search, "lost_found")
                )

                items(actions) { action ->
                    GlassCard(
                        modifier = Modifier.height(120.dp).clickable {
                            when (action.route) {
                                "notes" -> onNavigateToNotes()
                                "assignments" -> onNavigateToAssignments()
                                "timetable" -> onNavigateToTimetable()
                                "performance" -> onNavigateToPerformance()
                                "announcements" -> onNavigateToAnnouncements()
                                "lost_found" -> onNavigateToLostFound()
                            }
                        }
                    ) {
                        Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Icon(action.icon, contentDescription = action.title, tint = Color(0xFF6366F1), modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(action.title, fontSize = 14.sp, fontWeight = FontWeight.Medium)
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
        Column(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Icon(icon, contentDescription = null, tint = color)
            Column {
                Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(title, fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StudentDashboardPreview() {
    EduSphereTheme {
        StudentDashboardScreen()
    }
}