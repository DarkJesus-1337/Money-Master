// app/src/main/java/com/pixelpioneer/moneymaster/util/TextRecognitionHelper.kt
package com.pixelpioneer.moneymaster.util

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.pixelpioneer.moneymaster.data.model.Receipt
import com.pixelpioneer.moneymaster.data.model.ReceiptItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class TextRecognitionHelper {

    suspend fun processReceiptImage(bitmap: Bitmap): Receipt {
        return withContext(Dispatchers.IO) {
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            try {
                val image = InputImage.fromBitmap(bitmap, 0)
                val result = recognizer.process(image).await()

                val fullText = result.text
                parseReceiptText(fullText)

            } catch (e: Exception) {
                Receipt(null, null, emptyList())
            }
        }
    }

    fun parseReceiptText(text: String): Receipt {
        Log.d("OCR_DEBUG", "=== OCR Full Text ===")
        Log.d("OCR_DEBUG", text)
        Log.d("OCR_DEBUG", "=== Lines ===")

        val lines = text.split("\n").filter { it.trim().isNotEmpty() }
        lines.forEachIndexed { index, line ->
            Log.d("OCR_DEBUG", "$index: '$line'")
        }

        val items = extractItems(lines)
        Log.d("OCR_DEBUG", "=== Extracted Items (${items.size}) ===")
        items.forEach { item ->
            Log.d("OCR_DEBUG", "Item: '${item.name}' -> ${item.price}€")
        }

        val receipt = Receipt(
            storeName = extractStoreName(lines),
            date = null,
            items = items
        )

        Log.d("OCR_DEBUG", "=== Final Receipt ===")
        Log.d("OCR_DEBUG", "Store: ${receipt.storeName}")
        Log.d("OCR_DEBUG", "Items count: ${receipt.items.size}")

        return receipt
    }

    private fun extractStoreName(lines: List<String>): String? {
        // Suche nach bekannten Store-Namen
        val storePatterns = listOf("LIDL", "ALDI", "REWE", "EDEKA", "PENNY")

        return lines.take(5).firstOrNull { line ->
            storePatterns.any { pattern ->
                line.uppercase().contains(pattern)
            }
        }?.trim()
    }

    private fun extractItems(lines: List<String>): List<ReceiptItem> {
        val items = mutableListOf<ReceiptItem>()
        var i = 0

        Log.d("OCR_DEBUG", "=== Starting Item Extraction (sequenziell, robust) ===")

        while (i < lines.size) {
            val line = lines[i].trim()
            // Preiszeile erkennen (z.B. "1,99 A" oder "-0,20")
            val priceMatch = Regex("""^(-?\d+[,.]\d{1,2})\s*[A-Za-z>]*$""").find(line)
            if (priceMatch != null) {
                val price = priceMatch.groupValues[1].replace(",", ".").toDoubleOrNull()
                if (price != null) {
                    // Suche den/die nächsten Artikelnamen (maximal 2 Zeilen, keine Preiszeile, kein Skip)
                    var nameBuilder = StringBuilder()
                    var j = i + 1
                    var nameLines = 0
                    while (j < lines.size && nameLines < 2) {
                        val next = lines[j].trim()
                        // Stoppe, wenn Preiszeile oder Skip
                        if (next.isEmpty() || shouldSkipLine(next) || Regex("""^-?\d+([,.]\d{1,2})?\s*[A-Za-z>]*$""").matches(next)) {
                            break
                        }
                        if (nameBuilder.isNotEmpty()) nameBuilder.append(" ")
                        nameBuilder.append(next)
                        nameLines++
                        j++
                    }
                    val name = nameBuilder.toString()
                    if (name.isNotBlank()) {
                        items.add(ReceiptItem(name, price))
                        Log.d("OCR_DEBUG", "Item zugeordnet: '$name' -> $price")
                        i = j - 1 // springe zum letzten Namen weiter
                    }
                }
            }
            i++
        }

        Log.d("OCR_DEBUG", "=== Extraction Complete: ${items.size} items found ===")
        return items
    }

    private fun shouldSkipLine(line: String): Boolean {
        val skipPatterns = listOf(
            "zu zahlen",
            "kreditkarte",
            "bar",
            "rückgeld",
            "mwst",
            "summe",
            "gesamt",
            "total",
            "subtotal",
            "datum",
            "uhrzeit",
            "kassierer",
            "bon-nr",
            "vielen dank",
            "preisvorteil",
            "rabatt",
            "pfand rückgabe",
            "eur$",
            "^-+$",
            "^\\d+$",
            "^\\d{1,2}:\\d{2}$", // Uhrzeit
            "^\\d{1,2}\\.\\d{1,2}\\.\\d{4}$", // Datum
            "steuer",
            "mehrwertsteuer",
            "ustid",
            "tel:",
            "www\\.",
            "fax:",
            "email:",
            "@"
        )

        val lowerLine = line.lowercase()
        val shouldSkip = skipPatterns.any { pattern ->
            if (pattern.startsWith("^") || pattern.endsWith("$")) {
                Regex(pattern).matches(lowerLine)
            } else {
                lowerLine.contains(pattern)
            }
        }

        if (shouldSkip) {
            Log.d("OCR_DEBUG", "Skipping line (pattern match): '$line'")
        }

        return shouldSkip
    }
}