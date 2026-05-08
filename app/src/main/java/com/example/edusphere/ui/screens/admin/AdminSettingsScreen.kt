package com.example.edusphere.ui.screens.admin

import androidx.compose.runtime.Composable
import com.example.edusphere.ui.screens.student.ProfileScreen

@Composable
fun AdminSettingsScreen(onLogout: () -> Unit) {
    // Reuses the student ProfileScreen for consistency
    ProfileScreen(onLogout = onLogout)
}