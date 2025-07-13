package com.pixelpioneer.moneymaster.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelpioneer.moneymaster.data.enums.BudgetPeriod
import com.pixelpioneer.moneymaster.data.model.*
import com.pixelpioneer.moneymaster.data.sample.MockRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MockViewModel : ViewModel() {

    private val repository = MockRepository()

    // UI State
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage.asSharedFlow()

    // Transactions
    val transactions = repository.getAllTransactions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Categories
    val categories = repository.getAllCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Budgets
    val budgets = repository.getAllBudgets()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Assets
    private val _assets = MutableStateFlow<List<Asset>>(emptyList())
    val assets: StateFlow<List<Asset>> = _assets.asStateFlow()

    // Statistics
    val expensesByCategory = repository.getExpensesByCategory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    val monthlyTotals = repository.getMonthlyTotals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    // Transaction Operations
    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                repository.insertTransaction(transaction)
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _errorMessage.emit("Transaktion konnte nicht hinzugefügt werden: ${e.message}")
            }
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                repository.updateTransaction(transaction)
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _errorMessage.emit("Transaktion konnte nicht aktualisiert werden: ${e.message}")
            }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                repository.deleteTransaction(transaction)
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _errorMessage.emit("Transaktion konnte nicht gelöscht werden: ${e.message}")
            }
        }
    }

    // Category Operations
    fun insertCategory(category: TransactionCategory) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                repository.insertCategory(category)
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _errorMessage.emit("Kategorie konnte nicht hinzugefügt werden: ${e.message}")
            }
        }
    }

    fun updateCategory(category: TransactionCategory) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                repository.updateCategory(category)
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _errorMessage.emit("Kategorie konnte nicht aktualisiert werden: ${e.message}")
            }
        }
    }

    fun deleteCategory(category: TransactionCategory) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                repository.deleteCategory(category)
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _errorMessage.emit("Kategorie konnte nicht gelöscht werden: ${e.message}")
            }
        }
    }

    // Budget Operations
    fun insertBudget(budget: Budget) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                repository.insertBudget(budget)
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _errorMessage.emit("Budget konnte nicht hinzugefügt werden: ${e.message}")
            }
        }
    }

    fun updateBudget(budget: Budget) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                repository.updateBudget(budget)
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _errorMessage.emit("Budget konnte nicht aktualisiert werden: ${e.message}")
            }
        }
    }

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                repository.deleteBudget(budget)
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _errorMessage.emit("Budget konnte nicht gelöscht werden: ${e.message}")
            }
        }
    }

    // Crypto Operations
    fun loadAssets() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val result = repository.getAssets()
                result.fold(
                    onSuccess = { response ->
                        _assets.value = response.data
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        _errorMessage.emit("Assets konnten nicht geladen werden: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _errorMessage.emit("Unerwarteter Fehler: ${e.message}")
            }
        }
    }

    fun loadAssetHistory(assetId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val result = repository.getAssetHistory(assetId)
                result.fold(
                    onSuccess = { response ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            selectedAssetHistory = response.data
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        _errorMessage.emit("Historische Daten konnten nicht geladen werden: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _errorMessage.emit("Unerwarteter Fehler: ${e.message}")
            }
        }
    }

    // Receipt Scanning
    fun scanReceipt(imageData: ByteArray) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val result = repository.scanReceipt(imageData)
                result.fold(
                    onSuccess = { receipt ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            scannedReceipt = receipt
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        _errorMessage.emit("Beleg konnte nicht gescannt werden: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _errorMessage.emit("Scanner-Fehler: ${e.message}")
            }
        }
    }

    // Filter Operations
    fun getTransactionsByCategory(categoryId: Long): Flow<List<Transaction>> {
        return repository.getTransactionsByCategory(categoryId)
    }

    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>> {
        return repository.getTransactionsByDateRange(startDate, endDate)
    }

    fun getBudgetsByPeriod(period: BudgetPeriod): Flow<List<Budget>> {
        return repository.getBudgetsByPeriod(period)
    }

    // UI State Management
    fun clearScannedReceipt() {
        _uiState.value = _uiState.value.copy(scannedReceipt = null)
    }

    fun clearSelectedAssetHistory() {
        _uiState.value = _uiState.value.copy(selectedAssetHistory = emptyList())
    }

    data class UiState(
        val isLoading: Boolean = false,
        val scannedReceipt: Receipt? = null,
        val selectedAssetHistory: List<HistoryDataPoint> = emptyList()
    )
}