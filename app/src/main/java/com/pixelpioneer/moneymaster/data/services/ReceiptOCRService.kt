// app/src/main/java/com/pixelpioneer/moneymaster/data/services/ReceiptOCRService.kt
package com.pixelpioneer.moneymaster.data.services

import android.graphics.Bitmap
import android.util.Log
import com.pixelpioneer.moneymaster.data.model.Receipt
import com.pixelpioneer.moneymaster.data.model.ReceiptItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.util.regex.Pattern

class ReceiptOCRService {
    companion object {
        private const val API_KEY = "K88724362288957" // Hier deinen API Key einfügen
        private const val TAG = "OCR_SERVICE"
    }

    fun recognizeReceipt(
        bitmap: Bitmap,
        onSuccess: (Receipt) -> Unit,
        onError: (Exception) -> Unit
    ) {
        Log.d(TAG, "Starting receipt recognition with OCR.space...")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val receipt = processReceiptWithOcrSpace(bitmap)
                withContext(Dispatchers.Main) {
                    Log.d(TAG, "Receipt processed successfully:")
                    Log.d(TAG, "Store: ${receipt.storeName}")
                    Log.d(TAG, "Items count: ${receipt.items.size}")
                    receipt.items.forEach { item ->
                        Log.d(TAG, "Item: '${item.name}' -> ${item.price}€")
                    }
                    onSuccess(receipt)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e(TAG, "Error processing receipt", e)
                    onError(e)
                }
            }
        }
    }

    private suspend fun processReceiptWithOcrSpace(bitmap: Bitmap): Receipt {
        // Bitmap zu ByteArray konvertieren
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        val imageBytes = stream.toByteArray()

        // Multipart Body erstellen
        val requestBody = imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("file", "receipt.jpg", requestBody)
        val apiKeyBody = API_KEY.toRequestBody(MultipartBody.FORM)

        // API Call
        val response = OcrSpaceApiClient.apiService.parseImage(
            apiKey = apiKeyBody,
            image = imagePart
        )

        if (!response.isSuccessful) {
            throw Exception("OCR API call failed: ${response.code()}")
        }

        val ocrResponse = response.body() ?: throw Exception("Empty response body")

        if (ocrResponse.IsErroredOnProcessing) {
            throw Exception("OCR processing error: ${ocrResponse.ErrorMessage}")
        }

        val parsedText = ocrResponse.ParsedResults?.firstOrNull()?.ParsedText
            ?: throw Exception("No text recognized")

        return parseReceiptText(parsedText)
    }

    private fun parseReceiptText(text: String): Receipt {
        val lines = text.split("\n").map { it.trim() }.filter { it.isNotEmpty() }

        var storeName = "Unbekannter Laden"
        val items = mutableListOf<ReceiptItem>()
        var total = 0.0

        // Store name detection (erste paar Zeilen)
        val storePattern = Pattern.compile("^[A-Z][A-Za-z\\s&-]+$")
        for (i in 0..minOf(3, lines.size - 1)) {
            if (storePattern.matcher(lines[i]).matches() && lines[i].length > 3) {
                storeName = lines[i]
                break
            }
        }

        // Artikel und Preise erkennen
        val pricePattern = Pattern.compile("(\\d+[,.]\\d{2})\\s*€?")
        val itemPattern = Pattern.compile("^([A-Za-zäöüÄÖÜß\\s\\-\\.]+).*?(\\d+[,.]\\d{2})\\s*€?")

        for (line in lines) {
            val itemMatcher = itemPattern.matcher(line)
            if (itemMatcher.find()) {
                val itemName = itemMatcher.group(1)?.trim() ?: continue
                val priceStr = itemMatcher.group(2)?.replace(",", ".") ?: continue

                try {
                    val price = priceStr.toDouble()
                    if (itemName.isNotEmpty() && price > 0) {
                        items.add(ReceiptItem(name = itemName, price = price))
                    }
                } catch (e: NumberFormatException) {
                    Log.w(TAG, "Could not parse price: $priceStr")
                }
            }
        }

        // Gesamtsumme finden
        val totalPattern = Pattern.compile("(?i)(summe|total|gesamt).*?(\\d+[,.]\\d{2})")
        for (line in lines) {
            val totalMatcher = totalPattern.matcher(line)
            if (totalMatcher.find()) {
                try {
                    total = totalMatcher.group(2)?.replace(",", ".")?.toDouble() ?: 0.0
                    break
                } catch (e: NumberFormatException) {
                    Log.w(TAG, "Could not parse total")
                }
            }
        }

        // Falls kein Total gefunden, aus Items berechnen
        if (total == 0.0) {
            total = items.sumOf { it.price }
        }

        // Datum als String formatieren
        val currentDate = java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault())
            .format(java.util.Date())

        return Receipt(
            storeName = storeName,
            items = items,
            date = currentDate
        )
    }
}