package com.example.edusphere.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

// ✅ Data Class must be outside the class
data class LostItem(
    val itemId: String = "",
    val name: String = "",
    val description: String = "",
    val location: String = "",
    val date: String = "",
    val imageUrl: String = "",
    val uploadedBy: String = "admin"
)

class LostAndFoundRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("lost_items")

    suspend fun addLostItem(item: LostItem): Result<Unit> {
        return try {
            val id = UUID.randomUUID().toString()
            val newItem = item.copy(itemId = id)
            collection.document(id).set(newItem).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLostItems(): Result<List<LostItem>> {
        return try {
            val snapshot = collection.get().await()
            val items = snapshot.toObjects(LostItem::class.java)
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ✅ Suppressed warning since it's available for future use
    @Suppress("unused")
    suspend fun deleteLostItem(itemId: String): Result<Unit> {
        return try {
            collection.document(itemId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}