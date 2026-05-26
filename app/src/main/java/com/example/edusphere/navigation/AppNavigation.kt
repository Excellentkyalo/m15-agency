package com.example.edusphere.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.edusphere.ui.screens.admin.*
import com.example.edusphere.ui.screens.auth.LoginScreen
import com.example.edusphere.ui.screens.auth.RegisterScreen
import com.example.edusphere.ui.screens.student.*
import com.example.edusphere.viewmodel.AuthViewModel

@Composable
fun AppNavigation(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()

    // ✅ Determine Start Destination based on Auth State
    val startDestination = when {
        authState.isAuthenticated && authState.role == UserRole.ADMIN -> "admin_dashboard"
        authState.isAuthenticated && authState.role == UserRole.STUDENT -> "student_dashboard"
        else -> "login"
    }

    NavHost(navController = navController, startDestination = startDestination) {

        // --- Auth Screens ---
        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                viewModel = authViewModel
            )
        }

        composable("register") {
            RegisterScreen(
                onNavigateToLogin = { navController.navigate("login") },
                viewModel = authViewModel
            )
        }

        // --- Admin Screens ---
        composable("admin_dashboard") {
            AdminDashboardScreen(
                currentRoute = "admin_dashboard",
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable("manage_users") { ManageUsersScreen() }
        composable("admin_analytics") { AdminAnalyticsScreen() }
        composable("admin_settings") {
            AdminSettingsScreen(onLogout = {
                authViewModel.logout()
                navController.navigate("login") { popUpTo("login") { inclusive = true } }
            })
        }

        composable("enter_grades") { EnterGradesScreen() }
        composable("manage_timetable") { TimetableManagementScreen() }

        // ✅ Updated Routes for Management Screens
        composable("manage_lost_items") { ManageLostItemsScreen(onNavigateBack = { navController.popBackStack() }) }
        composable("add_lost_item") { AddLostItemScreen(onNavigateBack = { navController.popBackStack() }) }

        composable("manage_announcements") { ManageAnnouncementsScreen(onNavigateBack = { navController.popBackStack() }) }
        composable("add_announcement") { AddAnnouncementScreen(onNavigateBack = { navController.popBackStack() }) }

        // ✅ Updated Notes Route to use ManageNotesScreen (Upload/Delete)
        composable("manage_notes") { ManageNotesScreen(onNavigateBack = { navController.popBackStack() }) }
        composable("upload_notes") { UploadNotesScreen(onNavigateBack = { navController.popBackStack() }) }

        composable("manage_assignments") { ManageAssignmentsScreen(onNavigateBack = { navController.popBackStack() }) }
        composable("add_assignment") { Text("Add Assignment Screen Coming Soon") }

        // --- Student Screens ---
        composable("student_dashboard") {
            StudentDashboardScreen(
                currentRoute = "student_dashboard",
                onNavigate = { route -> navController.navigate(route) },
                onNavigateToNotes = { navController.navigate("notes") },
                onNavigateToAssignments = { navController.navigate("assignments") },
                onNavigateToTimetable = { navController.navigate("timetable") },
                onNavigateToPerformance = { navController.navigate("performance") },
                onNavigateToAnnouncements = { navController.navigate("announcements") },
                onNavigateToLostFound = { navController.navigate("lost_found") }
            )
        }

        composable("notes") { NotesScreen(onNavigateToAddNote = {}) }
        composable("assignments") { AssignmentsScreen() }
        composable("timetable") { TimetableScreen() }
        composable("performance") { PerformanceScreen() }
        composable("announcements") { AnnouncementsScreen() }
        composable("lost_found") { LostAndFoundScreen() }

        composable("profile") {
            ProfileScreen(onLogout = {
                authViewModel.logout()
                navController.navigate("login") { popUpTo("login") { inclusive = true } }
            })
        }
    }

    // ✅ Force Navigation when Auth State Changes
    LaunchedEffect(authState.isAuthenticated, authState.role) {
        if (authState.isAuthenticated) {
            val targetRoute = if (authState.role == UserRole.ADMIN) "admin_dashboard" else "student_dashboard"

            // Check if we are already on the correct screen to avoid loops
            if (navController.currentDestination?.route != targetRoute) {
                navController.navigate(targetRoute) {
                    popUpTo("login") { inclusive = true }
                }
            }
        }
    }
}