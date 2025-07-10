package com.pixelpioneer.moneymaster.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelpioneer.moneymaster.data.model.Receipt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ReceiptViewModel : ViewModel() {
    private val _currentReceipt = MutableStateFlow<Receipt?>(null)
    val currentReceipt: StateFlow<Receipt?> = _currentReceipt.asStateFlow()
    
    fun setReceipt(receipt: Receipt) {
        _currentReceipt.value = receipt
    }

    fun clearReceipt() {
        _currentReceipt.value = null
    }
}