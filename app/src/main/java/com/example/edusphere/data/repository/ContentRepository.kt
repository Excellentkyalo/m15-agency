package com.example.edusphere.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ContentRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun addAnnouncement(title: String, message: String, priority: String): Result<Unit> {
        return try {
            val announcement = hashMapOf(
                "title" to title,
                "message" to message,
                "priority" to priority,
                "timestamp" to System.currentTimeMillis(),
                "authorId" to "admin_id" // Replace with actual admin UID
            )
            db.collection("announcements").add(announcement).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addNote(title: String, subject: String, content: String): Result<Unit> {
        return try {
            val note = hashMapOf(
                "title" to title,
                "subject" to subject,
                "content" to content,
                "timestamp" to System.currentTimeMillis()
            )
            db.collection("notes").add(note).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addAssignment(title: String, subject: String, deadline: String): Result<Unit> {
        return try {
            val assignment = hashMapOf(
                "title" to title,
                "subject" to subject,
                "deadline" to deadline,
                "status" to "Pending",
                "timestamp" to System.currentTimeMillis()
            )
            db.collection("assignments").add(assignment).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}