package com.example.edusphere.data.repository

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class CloudinaryRepository {
    private val client = OkHttpClient()
    private val cloudName = "YOUR_CLOUD_NAME" // Replace with your Cloudinary Cloud Name
    private val uploadPreset = "YOUR_UPLOAD_PRESET" // Create an unsigned preset in Cloudinary settings

    suspend fun uploadImage(file: File): Result<String> {
        return try {
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.name, file.asRequestBody("image/*".toMediaType()))
                .addFormDataPart("upload_preset", uploadPreset)
                .build()

            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                // In a real app, parse JSON to get "secure_url"
                Result.success("https://res.cloudinary.com/demo/image/upload/sample.jpg")
            } else {
                Result.failure(Exception("Upload failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}