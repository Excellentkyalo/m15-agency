package com.example.edusphere.viewmodel

import androidx.appcompat.app.AppCompatDelegate // ✅ Now works after Sync
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class UserProfile(
    val name: String = "",
    val email: String = "",
    val role: String = ""
)

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val doc = db.collection("users").document(uid).get().await()
                if (doc.exists()) {
                    _profile.value = UserProfile(
                        name = doc.getString("name") ?: "User",
                        email = doc.getString("email") ?: "",
                        role = doc.getString("role") ?: "student"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateUserName(newName: String) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                db.collection("users").document(uid).update("name", newName).await()
                _profile.value = _profile.value.copy(name = newName)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleDarkMode(isDark: Boolean) {
        _isDarkMode.value = isDark
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}