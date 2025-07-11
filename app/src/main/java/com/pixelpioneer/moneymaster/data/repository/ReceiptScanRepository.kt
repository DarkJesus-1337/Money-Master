package com.pixelpioneer.moneymaster.data.repository

import com.pixelpioneer.moneymaster.data.services.RemoteConfigManager
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

class ReceiptScanRepository(
    private val remoteConfigManager: RemoteConfigManager
) {

    private val client = OkHttpClient()

    suspend fun scanReceipt(imageFile: File): String {
        val apiKey = remoteConfigManager.getOcrSpaceApiKey()

        if (apiKey.isEmpty()) {
            throw IllegalStateException("OCR Space API Key not available")
        }

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("apikey", apiKey)
            .addFormDataPart("language", "ger")
            .addFormDataPart("isOverlayRequired", "false")
            .addFormDataPart("detectOrientation", "true")
            .addFormDataPart("scale", "true")
            .addFormDataPart("isTable", "true")
            .addFormDataPart(
                "file",
                imageFile.name,
                imageFile.asRequestBody("image/*".toMediaType())
            )
            .build()

        val request = Request.Builder()
            .url("https://api.ocr.space/parse/image")
            .post(requestBody)
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.string() ?: throw IOException("Empty response body")
            } else {
                throw IOException("HTTP error: ${response.code}")
            }
        } catch (e: Exception) {
            throw IOException("Network error: ${e.message}", e)
        }
    }

    fun isApiKeyAvailable(): Boolean {
        return remoteConfigManager.getOcrSpaceApiKey().isNotEmpty()
    }
}