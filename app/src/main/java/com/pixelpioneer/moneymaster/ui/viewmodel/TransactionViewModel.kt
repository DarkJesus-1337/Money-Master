package com.pixelpioneer.moneymaster.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelpioneer.moneymaster.data.model.Receipt
import com.pixelpioneer.moneymaster.data.model.Transaction
import com.pixelpioneer.moneymaster.data.model.TransactionCategory
import com.pixelpioneer.moneymaster.data.repository.CategoryRepository
import com.pixelpioneer.moneymaster.data.repository.TransactionRepository
import com.pixelpioneer.moneymaster.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class TransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _transactionsState = MutableStateFlow<UiState<List<Transaction>>>(UiState.Loading)
    val transactionsState: StateFlow<UiState<List<Transaction>>> = _transactionsState

    private val _categoriesState =
        MutableStateFlow<UiState<List<TransactionCategory>>>(UiState.Loading)
    val categoriesState: StateFlow<UiState<List<TransactionCategory>>> = _categoriesState

    private val _selectedTransaction = MutableStateFlow<UiState<Transaction>>(UiState.Loading)
    val selectedTransaction: StateFlow<UiState<Transaction>> = _selectedTransaction

    private val _transactionFormState = MutableStateFlow(TransactionFormState())
    val transactionFormState: StateFlow<TransactionFormState> = _transactionFormState

    private val _financialSummary = MutableStateFlow<UiState<FinancialSummary>>(UiState.Loading)
    val financialSummary: StateFlow<UiState<FinancialSummary>> = _financialSummary

    init {
        loadTransactions()
        loadCategories()
        loadFinancialSummary()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            try {
                _transactionsState.value = UiState.Loading
                transactionRepository.allTransactionsWithCategory
                    .catch { e ->
                        _transactionsState.value =
                            UiState.Error(e.message ?: "Unknown error occurred")
                    }
                    .collect { transactions ->
                        if (transactions.isEmpty()) {
                            _transactionsState.value = UiState.Empty
                        } else {
                            _transactionsState.value = UiState.Success(transactions)
                        }
                    }
            } catch (e: Exception) {
                _transactionsState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun loadTransactionById(id: Long) {
        viewModelScope.launch {
            try {
                _selectedTransaction.value = UiState.Loading
                transactionRepository.getTransactionById(id)
                    .catch { e ->
                        _selectedTransaction.value =
                            UiState.Error(e.message ?: "Unknown error occurred")
                    }
                    .collect { transaction ->
                        _selectedTransaction.value = UiState.Success(transaction)
                    }
            } catch (e: Exception) {
                _selectedTransaction.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                _categoriesState.value = UiState.Loading
                categoryRepository.allCategories
                    .catch { e ->
                        _categoriesState.value =
                            UiState.Error(e.message ?: "Unknown error occurred")
                    }
                    .collect { categories ->
                        if (categories.isEmpty()) {
                            _categoriesState.value = UiState.Empty
                        } else {
                            _categoriesState.value = UiState.Success(categories)
                        }
                    }
            } catch (e: Exception) {
                _categoriesState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    private fun loadFinancialSummary() {
        viewModelScope.launch {
            try {
                _financialSummary.value = UiState.Loading

                transactionRepository.getTotalExpensesByMonth().collect { expenses ->
                    transactionRepository.getTotalIncomeByMonth().collect { income ->
                        val balance = income - expenses

                        _financialSummary.value = UiState.Success(
                            FinancialSummary(
                                totalIncome = income,
                                totalExpenses = expenses,
                                balance = balance
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                _financialSummary.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun createTransaction() {
        viewModelScope.launch {
            val formState = _transactionFormState.value

            if (!validateTransactionForm()) {
                return@launch
            }

            try {
                val category = _transactionFormState.value.selectedCategory
                    ?: throw IllegalStateException("Category cannot be null")

                val transaction = Transaction(
                    amount = formState.amount,
                    title = formState.title,
                    description = formState.description,
                    category = category,
                    date = formState.date,
                    isExpense = formState.isExpense
                )

                transactionRepository.insertTransaction(transaction)

                resetFormState()

                loadTransactions()
                loadFinancialSummary()
            } catch (e: Exception) {
            }
        }
    }

    fun updateTransaction(id: Long) {
        viewModelScope.launch {
            val formState = _transactionFormState.value

            if (!validateTransactionForm()) {
                return@launch
            }

            try {
                val category = _transactionFormState.value.selectedCategory
                    ?: throw IllegalStateException("Category cannot be null")

                val transaction = Transaction(
                    id = id,
                    amount = formState.amount,
                    title = formState.title,
                    description = formState.description,
                    category = category,
                    date = formState.date,
                    isExpense = formState.isExpense
                )

                transactionRepository.updateTransaction(transaction)

                resetFormState()

                loadTransactions()
                loadFinancialSummary()

                loadTransactionById(id)
            } catch (e: Exception) {
            }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                transactionRepository.deleteTransaction(transaction)
                loadTransactions()
                loadFinancialSummary()
            } catch (e: Exception) {
            }
        }
    }

    fun updateAmount(amount: Double) {
        _transactionFormState.value = _transactionFormState.value.copy(
            amount = amount,
            amountError = if (amount <= 0) "Amount must be greater than zero" else null
        )
    }

    fun updateTitle(title: String) {
        _transactionFormState.value = _transactionFormState.value.copy(
            title = title,
            titleError = if (title.isBlank()) "Title cannot be empty" else null
        )
    }

    fun updateDescription(description: String) {
        _transactionFormState.value = _transactionFormState.value.copy(description = description)
    }

    fun updateSelectedCategory(category: TransactionCategory) {
        _transactionFormState.value = _transactionFormState.value.copy(
            selectedCategory = category,
            categoryError = null
        )
    }

    fun updateDate(date: Long) {
        _transactionFormState.value = _transactionFormState.value.copy(date = date)
    }

    fun updateIsExpense(isExpense: Boolean) {
        _transactionFormState.value = _transactionFormState.value.copy(isExpense = isExpense)
    }

    private fun resetFormState() {
        _transactionFormState.value = TransactionFormState()
    }

    fun initFormWithTransaction(transaction: Transaction) {
        _transactionFormState.value = TransactionFormState(
            amount = transaction.amount,
            title = transaction.title,
            description = transaction.description,
            selectedCategory = transaction.category,
            date = transaction.date,
            isExpense = transaction.isExpense
        )
    }

    private fun validateTransactionForm(): Boolean {
        val formState = _transactionFormState.value
        var isValid = true

        var updatedFormState = formState

        if (formState.amount <= 0) {
            updatedFormState = updatedFormState.copy(
                amountError = "Amount must be greater than zero"
            )
            isValid = false
        }

        if (formState.title.isBlank()) {
            updatedFormState = updatedFormState.copy(
                titleError = "Title cannot be empty"
            )
            isValid = false
        }

        if (formState.selectedCategory == null) {
            updatedFormState = updatedFormState.copy(
                categoryError = "Please select a category"
            )
            isValid = false
        }

        _transactionFormState.value = updatedFormState

        return isValid
    }

    fun saveReceiptAsTransactions(receipt: Receipt) {
        viewModelScope.launch {
            try {
                val defaultCategory = getDefaultCategory()

                receipt.items.forEach { item ->
                    val transaction = Transaction(
                        id = 0,
                        amount = item.price,
                        title = item.name,
                        description = "Vom Kassenzettel: ${receipt.storeName ?: "Unbekannt"}",
                        category = defaultCategory,
                        date = System.currentTimeMillis(),
                        isExpense = true
                    )
                    transactionRepository.insertTransaction(transaction)
                }

                loadTransactions()
                loadFinancialSummary()

            } catch (e: Exception) {
            }
        }
    }

    private suspend fun getDefaultCategory(): TransactionCategory {
        return try {
            val categories = categoryRepository.allCategories.first()
            categories.firstOrNull()
                ?: TransactionCategory(
                    id = 1,
                    name = "Einkauf",
                    color = 0xFF4CAF50.toInt(),
                    icon = 0
                )
        } catch (e: Exception) {
            TransactionCategory(
                id = 1,
                name = "Einkauf",
                color = 0xFF4CAF50.toInt(),
                icon = 0
            )
        }
    }

    fun refreshFinancialSummary() {
        viewModelScope.launch {
            try {
            } catch (e: Exception) {
            }
        }
    }

    fun refreshTransactions() {
        loadTransactions()
    }

    // Neue Methode zum direkten Hinzufügen einer Transaktion (z.B. für OCR-Import)
    fun addTransactionDirect(transaction: Transaction) {
        viewModelScope.launch {
            try {
                transactionRepository.insertTransaction(transaction)
                loadTransactions()
                loadFinancialSummary()
            } catch (e: Exception) {
                // Fehlerbehandlung optional
            }
        }
    }
}

data class TransactionFormState(
    val amount: Double = 0.0,
    val title: String = "",
    val description: String = "",
    val selectedCategory: TransactionCategory? = null,
    val date: Long = Calendar.getInstance().timeInMillis,
    val isExpense: Boolean = true,
    val amountError: String? = null,
    val titleError: String? = null,
    val categoryError: String? = null
)

data class FinancialSummary(
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val balance: Double = 0.0
)