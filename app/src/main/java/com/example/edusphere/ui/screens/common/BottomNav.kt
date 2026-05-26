package com.example.edusphere.ui.screens.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.edusphere.ui.theme.EduSphereTheme

data class NavItem(val title: String, val icon: ImageVector, val route: String)

@Composable
fun BottomNav(
    isAdmin: Boolean,
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = if (isAdmin) {
        listOf(
            NavItem("Home", Icons.Default.Home, "admin_dashboard"),
            NavItem("Users", Icons.Default.People, "manage_users"),
            NavItem("Settings", Icons.Default.Settings, "admin_settings")
        )
    } else {
        listOf(
            NavItem("Home", Icons.Default.Home, "student_dashboard"),
            NavItem("Notes", Icons.Default.Description, "notes"),
            NavItem("Lost", Icons.Default.Search, "lost_found"),
            NavItem("Profile", Icons.Default.Person, "profile")
        )
    }

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        onNavigate(item.route)
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavPreview() {
    EduSphereTheme {
        BottomNav(isAdmin = true, currentRoute = "admin_dashboard", onNavigate = {})
    }
}