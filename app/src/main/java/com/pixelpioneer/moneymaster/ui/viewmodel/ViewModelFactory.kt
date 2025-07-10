package com.pixelpioneer.moneymaster.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pixelpioneer.moneymaster.data.repository.BudgetRepository
import com.pixelpioneer.moneymaster.data.repository.CategoryRepository
import com.pixelpioneer.moneymaster.data.repository.CoinCapRepository
import com.pixelpioneer.moneymaster.data.repository.TransactionRepository

class ViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository,
    private val coinCapRepository: CoinCapRepository,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(TransactionViewModel::class.java) -> {
                TransactionViewModel(transactionRepository, categoryRepository) as T
            }

            modelClass.isAssignableFrom(CategoryViewModel::class.java) -> {
                CategoryViewModel(categoryRepository) as T
            }

            modelClass.isAssignableFrom(BudgetViewModel::class.java) -> {
                BudgetViewModel(budgetRepository, categoryRepository) as T
            }

            modelClass.isAssignableFrom(StatisticsViewModel::class.java) -> {
                StatisticsViewModel(transactionRepository, categoryRepository) as T
            }

            modelClass.isAssignableFrom(CryptoViewModel::class.java) -> {
                CryptoViewModel(coinCapRepository) as T
            }

            modelClass.isAssignableFrom(ReceiptViewModel::class.java) -> {
                ReceiptViewModel() as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}