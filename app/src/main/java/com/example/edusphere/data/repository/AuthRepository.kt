package com.example.edusphere.data.repository

import android.util.Log
import com.example.edusphere.navigation.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "AuthRepository"

    suspend fun login(email: String, password: String): Result<UserRole> {
        return try {
            Log.d(TAG, "Attempting login for: $email")

            // 1. Authenticate
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("User ID missing after login")
            Log.d(TAG, "Auth Successful. UID: $uid")

            // 2. Fetch Role from Firestore
            val doc = db.collection("users").document(uid).get().await()

            if (!doc.exists()) {
                Log.e(TAG, "Firestore document missing for UID: $uid")
                throw Exception("User profile not found in database.")
            }

            val roleStr = doc.getString("role") ?: "student"
            Log.d(TAG, "Role fetched from DB: $roleStr")

            // 3. Convert to Enum
            val role = try {
                UserRole.valueOf(roleStr.uppercase())
            } catch (e: Exception) {
                Log.e(TAG, "Invalid Role String: $roleStr", e)
                UserRole.STUDENT // Default to student if invalid
            }

            Result.success(role)
        } catch (e: Exception) {
            Log.e(TAG, "Login Failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String, name: String, role: UserRole): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("User ID missing")

            val userMap = hashMapOf(
                "uid" to uid,
                "name" to name,
                "email" to email,
                "role" to role.name.lowercase(),
                "createdAt" to System.currentTimeMillis()
            )
            db.collection("users").document(uid).set(userMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }
}