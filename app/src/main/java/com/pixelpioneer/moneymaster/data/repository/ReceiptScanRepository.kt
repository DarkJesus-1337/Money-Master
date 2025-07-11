package com.pixelpioneer.moneymaster.data.repository

import android.util.Log
import com.pixelpioneer.moneymaster.data.services.OcrSpaceApiClient
import com.pixelpioneer.moneymaster.data.services.OcrSpaceResponse
import com.pixelpioneer.moneymaster.data.services.RemoteConfigManager
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException

class ReceiptScanRepository(
    private val remoteConfigManager: RemoteConfigManager
) {
    companion object {
        private const val TAG = "ReceiptScanRepository"
    }

    private val apiService = OcrSpaceApiClient.apiService

    suspend fun scanReceipt(imageFile: File): OcrSpaceResponse {
        val apiKey = remoteConfigManager.getOcrSpaceApiKey()

        Log.d(TAG, "Starting OCR scan with API key: ${apiKey.take(10)}...")

        if (apiKey.isEmpty()) {
            throw IllegalStateException("OCR Space API Key not available")
        }

        val apiKeyBody = apiKey.toRequestBody("text/plain".toMediaType())
        val languageBody = "ger".toRequestBody("text/plain".toMediaType())
        val overlayBody = "false".toRequestBody("text/plain".toMediaType())
        val orientationBody = "true".toRequestBody("text/plain".toMediaType())
        val isTableBody = "true".toRequestBody("text/plain".toMediaType())
        val scaleBody = "true".toRequestBody("text/plain".toMediaType())

        val imagePart = MultipartBody.Part.createFormData(
            "file",
            imageFile.name,
            imageFile.asRequestBody("image/*".toMediaType())
        )

        Log.d(TAG, "Making OCR API request")
        Log.d(TAG, "File size: ${imageFile.length()} bytes")

        return try {
            val response = apiService.parseImage(
                apiKey = apiKeyBody,
                language = languageBody,
                overlay = overlayBody,
                orientation = orientationBody,
                isTable = isTableBody,
                scale = scaleBody,
                image = imagePart
            )

            Log.d(TAG, "Response code: ${response.code()}")

            if (response.isSuccessful) {
                val ocrResponse = response.body()
                if (ocrResponse != null) {
                    // Prüfe auf OCR-spezifische Fehler
                    if (ocrResponse.isErroredOnProcessing) {
                        Log.e(TAG, "OCR processing error: ${ocrResponse.errorMessage}")
                        throw IOException("OCR processing failed: ${ocrResponse.errorMessage}")
                    }

                    if (ocrResponse.ocrExitCode != 1) {
                        Log.e(TAG, "OCR exit code: ${ocrResponse.ocrExitCode}")
                        throw IOException("OCR failed with exit code: ${ocrResponse.ocrExitCode}")
                    }

                    Log.d(TAG, "OCR scan successful")
                    ocrResponse
                } else {
                    throw IOException("Empty response body")
                }
            } else {
                Log.e(TAG, "HTTP error: ${response.code()}")
                throw IOException("HTTP error: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network error", e)
            throw IOException("Network error: ${e.message}", e)
        }
    }

    fun isApiKeyAvailable(): Boolean {
        val available = remoteConfigManager.getOcrSpaceApiKey().isNotEmpty()
        Log.d(TAG, "API key available: $available")
        return available
    }

    fun getDebugInfo(): Map<String, String> {
        val apiKey = remoteConfigManager.getOcrSpaceApiKey()
        return mapOf(
            "apiKeyLength" to apiKey.length.toString(),
            "apiKeyPrefix" to apiKey.take(10),
            "hasRemoteConfig" to remoteConfigManager.hasKey(RemoteConfigManager.OCR_SPACE_API_KEY).toString()
        )
    }
}