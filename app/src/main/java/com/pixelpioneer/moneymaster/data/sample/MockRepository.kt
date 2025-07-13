package com.pixelpioneer.moneymaster.data.sample

import com.pixelpioneer.moneymaster.data.enums.BudgetPeriod
import com.pixelpioneer.moneymaster.data.model.*
import com.pixelpioneer.moneymaster.data.sample.SampleData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class MockRepository {
    
    // Mock Transaction Repository
    private val _transactions = SampleData.sampleTransactions.toMutableList()
    
    fun getAllTransactions(): Flow<List<Transaction>> = flow {
        delay(500) // Simulate network delay
        emit(_transactions.toList())
    }
    
    suspend fun insertTransaction(transaction: Transaction): Long {
        delay(200)
        val newId = (_transactions.maxOfOrNull { it.id } ?: 0) + 1
        val newTransaction = transaction.copy(id = newId)
        _transactions.add(newTransaction)
        return newId
    }
    
    suspend fun updateTransaction(transaction: Transaction) {
        delay(200)
        val index = _transactions.indexOfFirst { it.id == transaction.id }
        if (index != -1) {
            _transactions[index] = transaction
        }
    }
    
    suspend fun deleteTransaction(transaction: Transaction) {
        delay(200)
        _transactions.removeIf { it.id == transaction.id }
    }
    
    fun getTransactionsByCategory(categoryId: Long): Flow<List<Transaction>> = flow {
        delay(300)
        emit(_transactions.filter { it.category.id == categoryId })
    }
    
    // Mock Category Repository
    private val _categories = SampleData.sampleCategories.toMutableList()
    
    fun getAllCategories(): Flow<List<TransactionCategory>> = flow {
        delay(300)
        emit(_categories.toList())
    }
    
    suspend fun insertCategory(category: TransactionCategory): Long {
        delay(200)
        val newId = (_categories.maxOfOrNull { it.id } ?: 0) + 1
        val newCategory = category.copy(id = newId)
        _categories.add(newCategory)
        return newId
    }
    
    suspend fun updateCategory(category: TransactionCategory) {
        delay(200)
        val index = _categories.indexOfFirst { it.id == category.id }
        if (index != -1) {
            _categories[index] = category
        }
    }
    
    suspend fun deleteCategory(category: TransactionCategory) {
        delay(200)
        _categories.removeIf { it.id == category.id }
    }
    
    // Mock Budget Repository
    private val _budgets = SampleData.sampleBudgets.toMutableList()
    
    fun getAllBudgets(): Flow<List<Budget>> = flow {
        delay(400)
        emit(_budgets.toList())
    }
    
    suspend fun insertBudget(budget: Budget): Long {
        delay(200)
        val newId = (_budgets.maxOfOrNull { it.id } ?: 0) + 1
        val newBudget = budget.copy(id = newId)
        _budgets.add(newBudget)
        return newId
    }
    
    suspend fun updateBudget(budget: Budget) {
        delay(200)
        val index = _budgets.indexOfFirst { it.id == budget.id }
        if (index != -1) {
            _budgets[index] = budget
        }
    }
    
    suspend fun deleteBudget(budget: Budget) {
        delay(200)
        _budgets.removeIf { it.id == budget.id }
    }
    
    fun getBudgetsByPeriod(period: BudgetPeriod): Flow<List<Budget>> = flow {
        delay(300)
        emit(_budgets.filter { it.period == period })
    }
    
    // Mock CoinCap Repository
    suspend fun getAssets(): Result<AssetsResponse> {
        delay(1000) // Simulate API call
        return if (Random.nextBoolean()) {
            Result.success(SampleData.sampleAssetsResponse)
        } else {
            Result.failure(Exception("Netzwerkfehler"))
        }
    }
    
    suspend fun getAssetHistory(assetId: String): Result<HistoryResponse> {
        delay(800)
        return if (Random.nextBoolean()) {
            Result.success(SampleData.sampleHistoryResponse)
        } else {
            Result.failure(Exception("Historische Daten nicht verfügbar"))
        }
    }
    
    // Mock Receipt Scan Repository
    suspend fun scanReceipt(imageData: ByteArray): Result<Receipt> {
        delay(2000) // Simulate OCR processing
        return if (Random.nextBoolean()) {
            Result.success(SampleData.sampleReceipt)
        } else {
            Result.failure(Exception("Beleg konnte nicht gescannt werden"))
        }
    }
    
    // Statistics helpers
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>> = flow {
        delay(400)
        emit(_transactions.filter { it.date in startDate..endDate })
    }
    
    fun getExpensesByCategory(): Flow<Map<TransactionCategory, Double>> = flow {
        delay(500)
        val expenses = _transactions.filter { it.isExpense }
            .groupBy { it.category }
            .mapValues { (_, transactions) -> transactions.sumOf { it.amount } }
        emit(expenses)
    }
    
    fun getMonthlyTotals(): Flow<Map<String, Double>> = flow {
        delay(600)
        // Simplified monthly totals simulation
        val monthlyData = mapOf(
            "Januar" to 1250.50,
            "Februar" to 1180.25,
            "März" to 1320.75,
            "April" to 1095.30
        )
        emit(monthlyData)
    }
}