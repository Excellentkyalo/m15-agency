package com.example.edusphere

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.edusphere.navigation.AppNavigation
import com.example.edusphere.ui.theme.EduSphereTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Initialize Firebase
        FirebaseApp.initializeApp(this)

        setContent {
            EduSphereTheme {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }
}