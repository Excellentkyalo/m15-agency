package com.example.edusphere.data.repository

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File

// ✅ Moved to top-level to allow const
private const val CLOUD_NAME = "dn3po2zo0" // Replace with your Cloud Name
private const val UPLOAD_PRESET = "edusphere_preset" // Replace with your Unsigned Upload Preset

class CloudinaryUploader {
    private val client = OkHttpClient()

    suspend fun uploadImage(file: File): String? {
        return try {
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.name, file.asRequestBody("image/*".toMediaType()))
                .addFormDataPart("upload_preset", UPLOAD_PRESET)
                .build()

            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = JSONObject(response.body?.string() ?: "")
                json.getString("secure_url")
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}