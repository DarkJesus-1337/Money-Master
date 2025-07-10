package com.pixelpioneer.moneymaster.data.repository

import android.util.Log
import com.pixelpioneer.moneymaster.data.services.OcrSpaceApiClient
import com.pixelpioneer.moneymaster.data.services.OcrSpaceResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ReceiptScanRepository(
    private val apiKey: String
) {
    suspend fun scanReceipt(imageFile: File): OcrSpaceResponse? {
        val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)
        val apiKeyBody = apiKey.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val languageBody = "ger".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val overlayBody = "false".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val orientationBody = "false".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val isTableBody = "true".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val scaleBody = "false".toRequestBody("multipart/form-data".toMediaTypeOrNull())

        return try {
            val response = OcrSpaceApiClient.apiService.parseImage(
                apiKey = apiKeyBody,
                language = languageBody,
                overlay = overlayBody,
                orientation = orientationBody,
                isTable = isTableBody,
                scale = scaleBody,
                image = body
            )
            Log.d(
                "ReceiptScanRepository",
                "HTTP-Status: ${response.code()}, body: ${response.body()}, errorBody: ${
                    response.errorBody()?.string()
                }"
            )
            if (!response.isSuccessful) {
                null
            } else {
                response.body()
            }
        } catch (e: Exception) {
            Log.e("ReceiptScanRepository", "Fehler beim OCR-Request: ${e.message}", e)
            null
        }
    }
}
