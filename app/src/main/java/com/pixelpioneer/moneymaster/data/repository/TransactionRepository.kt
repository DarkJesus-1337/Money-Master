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

/**
 * Repository for managing transactions and related financial data.
 *
 * Provides methods to retrieve, insert, update, and delete transactions,
 * as well as calculate financial summaries and interact with cryptocurrency assets.
 *
 * @property transactionDao Data access object for transaction entities.
 * @property coinCapApiService Optional service for CoinCap API requests.
 */
class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val coinCapApiService: CoinCapApiService? = null
) {
    /**
     * Flow of all transactions with their associated categories.
     */
    val allTransactionsWithCategory = transactionDao.getTransactionsWithCategory()
        .map { list -> list.map { TransactionMapper.fromEntity(it) } }

    /**
     * Retrieves a specific transaction by its ID.
     *
     * @param id The unique identifier of the transaction.
     * @return A [Flow] containing the [Transaction] with category information.
     */
    fun getTransactionById(id: Long): Flow<Transaction> {
        return transactionDao.getTransactionWithCategoryById(id)
            .map { TransactionMapper.fromEntity(it) }
    }

    /**
     * Inserts a new transaction into the database.
     *
     * @param transaction The transaction to insert.
     * @return The ID of the newly inserted transaction.
     */
    suspend fun insertTransaction(transaction: Transaction): Long {
        val entity = TransactionMapper.toEntity(transaction)
        return transactionDao.insertTransaction(entity)
    }

    /**
     * Updates an existing transaction in the database.
     *
     * @param transaction The transaction with updated information.
     */
    suspend fun updateTransaction(transaction: Transaction) {
        val entity = TransactionMapper.toEntity(transaction)
        transactionDao.updateTransaction(entity)
    }

    /**
     * Deletes a transaction from the database.
     *
     * @param transaction The transaction to delete.
     */
    suspend fun deleteTransaction(transaction: Transaction) {
        val entity = TransactionMapper.toEntity(transaction)
        transactionDao.deleteTransaction(entity)
    }

    /**
     * Retrieves transactions within a specific date range.
     *
     * @param startDate The start date in milliseconds.
     * @param endDate The end date in milliseconds.
     * @return A [Flow] containing a list of [Transaction] objects within the date range.
     */
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

    /**
     * Retrieves all transactions for the current month.
     *
     * @return A [Flow] containing a list of [Transaction] objects for the current month.
     */
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

    /**
     * Calculates the total expenses for the current month.
     *
     * @return A [Flow] containing the total expense amount as a [Double].
     */
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

    /**
     * Calculates the total income for the current month.
     *
     * @return A [Flow] containing the total income amount as a [Double].
     */
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

    /**
     * Calculates the total expenses for a specific category in the current month.
     *
     * @param categoryId The ID of the category to calculate expenses for.
     * @return A [Flow] containing the total expense amount for the category as a [Double].
     */
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

    /**
     * Retrieves the total income synchronously.
     *
     * This method performs a synchronous database operation and should
     * only be used when necessary (e.g., in background tasks).
     *
     * @return The total income amount as a [Double].
     */
    suspend fun getTotalIncomeSync(): Double {
        return transactionDao.getTotalIncomeSync()
    }

    /**
     * Retrieves the total expenses synchronously.
     *
     * This method performs a synchronous database operation and should
     * only be used when necessary (e.g., in background tasks).
     *
     * @return The total expense amount as a [Double].
     */
    suspend fun getTotalExpensesSync(): Double {
        return transactionDao.getTotalExpensesSync()
    }

    /**
     * Retrieves the total number of transactions synchronously.
     *
     * This method performs a synchronous database operation and should
     * only be used when necessary (e.g., in background tasks).
     *
     * @return The total number of transactions as an [Int].
     */
    suspend fun getTransactionCountSync(): Int {
        return transactionDao.getTransactionCountSync()
    }

    /**
     * Retrieves transactions with categories within a specific date range.
     *
     * @param startDate The start date in milliseconds.
     * @param endDate The end date in milliseconds.
     * @return A [Flow] containing a list of [TransactionWithCategory] objects.
     */
    fun getTransactionsWithCategoryByDateRange(
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionWithCategory>> {
        return transactionDao.getTransactionsWithCategoryByDateRange(startDate, endDate)
    }

    /**
     * Fetches cryptocurrency assets from the CoinCap API.
     *
     * @param limit The maximum number of assets to fetch. Default is 10.
     * @return A list of [Asset] objects.
     * @throws IllegalStateException If the CoinCap API service is not initialized.
     * @throws Exception If there's an error fetching the assets.
     */
    suspend fun fetchCryptoAssets(limit: Int = 10): List<Asset> {
        val apiService =
            coinCapApiService ?: throw IllegalStateException("CoinCap API Service not initialized")

        val response = apiService.getAssets(limit)
        if (response.isSuccessful) {
            return response.body()?.data ?: emptyList()
        } else {
            throw Exception("Fehler beim Abrufen der Krypto-Assets: ${response.message()}")
        }
    }
}