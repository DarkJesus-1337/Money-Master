package com.pixelpioneer.moneymaster.data.repository

import com.pixelpioneer.moneymaster.data.db.TransactionDao
import com.pixelpioneer.moneymaster.data.mapper.TransactionMapper
import com.pixelpioneer.moneymaster.data.model.Asset
import com.pixelpioneer.moneymaster.data.model.Transaction
import com.pixelpioneer.moneymaster.data.relation.TransactionWithCategory
import com.pixelpioneer.moneymaster.data.services.CoinCapApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val coinCapApiService: CoinCapApiService? = null
) {
    val allTransactionsWithCategory = transactionDao.getTransactionsWithCategory()
        .map { list -> list.map { TransactionMapper.fromEntity(it) } }

    fun getTransactionById(id: Long): Flow<Transaction> {
        return transactionDao.getTransactionWithCategoryById(id)
            .map { TransactionMapper.fromEntity(it) }
    }

    suspend fun insertTransaction(transaction: Transaction): Long {
        val entity = TransactionMapper.toEntity(transaction)
        return transactionDao.insertTransaction(entity)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        val entity = TransactionMapper.toEntity(transaction)
        transactionDao.updateTransaction(entity)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        val entity = TransactionMapper.toEntity(transaction)
        transactionDao.deleteTransaction(entity)
    }

    private fun getTransactionsByDateRange(
        startDate: Long,
        endDate: Long
    ): Flow<List<Transaction>> {
        return transactionDao.getTransactionsWithCategory()
            .map { list ->
                list.filter { it.transaction.date in startDate..endDate }
                    .map { TransactionMapper.fromEntity(it) }
            }
    }

    fun getCurrentMonthTransactions(): Flow<List<Transaction>> {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        val endDate = calendar.timeInMillis

        return getTransactionsByDateRange(startDate, endDate)
    }

    fun getTotalExpensesByMonth(): Flow<Double> {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        val endDate = calendar.timeInMillis

        return transactionDao.getTotalExpensesByDateRange(startDate, endDate)
            .map { it ?: 0.0 }
    }

    fun getTotalIncomeByMonth(): Flow<Double> {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        val endDate = calendar.timeInMillis

        return transactionDao.getTotalIncomeByDateRange(startDate, endDate)
            .map { it ?: 0.0 }
    }

    fun getTotalExpensesByCategoryForCurrentMonth(categoryId: Long): Flow<Double> {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        val endDate = calendar.timeInMillis

        return transactionDao.getTotalExpensesByCategoryAndDateRange(categoryId, startDate, endDate)
            .map { it ?: 0.0 }
    }

    suspend fun getTotalIncomeSync(): Double {
        return transactionDao.getTotalIncomeSync()
    }

    suspend fun getTotalExpensesSync(): Double {
        return transactionDao.getTotalExpensesSync()
    }

    suspend fun getTransactionCountSync(): Int {
        return transactionDao.getTransactionCountSync()
    }

    fun getTransactionsWithCategoryByDateRange(
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionWithCategory>> {
        return transactionDao.getTransactionsWithCategoryByDateRange(startDate, endDate)
    }

    suspend fun fetchCryptoAssets(limit: Int = 10): List<Asset> {
        val apiService = coinCapApiService ?: throw IllegalStateException("CoinCap API Service not initialized")

        val response = apiService.getAssets(limit)
        if (response.isSuccessful) {
            return response.body()?.data ?: emptyList()
        } else {
            throw Exception("Fehler beim Abrufen der Krypto-Assets: ${response.message()}")
        }
    }
}