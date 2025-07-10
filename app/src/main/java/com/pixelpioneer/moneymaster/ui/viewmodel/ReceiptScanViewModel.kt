package com.pixelpioneer.moneymaster.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelpioneer.moneymaster.data.model.Transaction
import com.pixelpioneer.moneymaster.data.model.TransactionCategory
import com.pixelpioneer.moneymaster.data.repository.ReceiptScanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class ReceiptScanViewModel(
    private val receiptScanRepository: ReceiptScanRepository
) : ViewModel() {

    private val _scannedItems = MutableStateFlow<List<Transaction>>(emptyList())
    val scannedItems: StateFlow<List<Transaction>> = _scannedItems

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun scanReceipt(imageFile: File, defaultCategory: TransactionCategory) {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val response = receiptScanRepository.scanReceipt(imageFile)
                Log.d("ReceiptScanViewModel", "OCR Response: $response")
                if (response == null) {
                    _error.value = "Fehler beim OCR-Request. Prüfe API-Key, Internetverbindung und Dateiformat."
                    _scannedItems.value = emptyList()
                    return@launch
                }
                val parsedText = response.ParsedResults?.firstOrNull()?.ParsedText.orEmpty()
                val errorMessage = response.ErrorMessage?.let { anyToString(it) }
                val errorDetails = response.ErrorDetails?.let { anyToString(it) }
                val exitCode = response.OCRExitCode
                Log.d("ReceiptScanViewModel", "OCR ParsedText: $parsedText")
                if (parsedText.isBlank()) {
                    _error.value = buildString {
                        append("Kein Text erkannt.\n")
                        append("OCRExitCode: $exitCode\n")
                        if (!errorMessage.isNullOrBlank()) append("ErrorMessage: $errorMessage\n")
                        if (!errorDetails.isNullOrBlank()) append("ErrorDetails: $errorDetails\n")
                    }
                    _scannedItems.value = emptyList()
                    return@launch
                }
                val items = parseItemsFromText(parsedText, defaultCategory)
                if (items.isEmpty()) {
                    _error.value = "Kein Artikel erkannt. OCR-Text: $parsedText"
                }
                _scannedItems.value = items
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun anyToString(any: Any): String = when (any) {
        is String -> any
        is List<*> -> any.joinToString("; ") { it?.toString().orEmpty() }
        is Array<*> -> any.joinToString("; ") { it?.toString().orEmpty() }
        else -> any.toString()
    }

    private fun parseItemsFromText(text: String, defaultCategory: TransactionCategory): List<Transaction> {
        // Flexibleres Regex: Erlaubt auch Preise mit Leerzeichen, Komma oder Punkt, optional führende Null
        val regex = Regex("""(.+?)\s+(\d{1,3}[\.,]\d{2})""")
        val now = System.currentTimeMillis()
        return text.lines().mapNotNull { line ->
            val match = regex.find(line)
            match?.let {
                val (title, amountStr) = it.destructured
                val amount = amountStr.replace(",", ".").toDoubleOrNull() ?: return@let null
                Transaction(
                    amount = amount,
                    title = title.trim(),
                    category = defaultCategory,
                    date = now,
                    isExpense = true
                )
            }
        }
    }
}
