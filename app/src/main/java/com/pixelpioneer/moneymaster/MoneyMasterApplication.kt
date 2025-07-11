package com.pixelpioneer.moneymaster

import android.app.Application
import android.util.Log
import com.pixelpioneer.moneymaster.data.db.MoneyMasterDatabase
import com.pixelpioneer.moneymaster.data.repository.BudgetRepository
import com.pixelpioneer.moneymaster.data.repository.CategoryRepository
import com.pixelpioneer.moneymaster.data.repository.CoinCapRepository
import com.pixelpioneer.moneymaster.data.repository.ReceiptScanRepository
import com.pixelpioneer.moneymaster.data.repository.TransactionRepository
import com.pixelpioneer.moneymaster.data.services.CoinCapApiClient
import com.pixelpioneer.moneymaster.data.services.RemoteConfigManager
import com.pixelpioneer.moneymaster.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MoneyMasterApplication : Application() {

    private val database by lazy {
        MoneyMasterDatabase.getDatabase(this)
    }

    private val transactionRepository by lazy {
        TransactionRepository(database.transactionDao(), coinCapApiClient.api)
    }

    private val categoryRepository by lazy {
        CategoryRepository(database.categoryDao())
    }

    private val budgetRepository by lazy {
        BudgetRepository(database.budgetDao(), database.transactionDao())
    }

    val remoteConfigManager by lazy {
        RemoteConfigManager(this)
    }

    private val coinCapApiClient by lazy {
        CoinCapApiClient(remoteConfigManager)
    }

    private val coinCapRepository by lazy {
        CoinCapRepository(coinCapApiClient.api)
    }

    private val receiptScanRepository by lazy {
        ReceiptScanRepository(remoteConfigManager)
    }

    val viewModelFactory by lazy {
        ViewModelFactory(
            transactionRepository = transactionRepository,
            categoryRepository = categoryRepository,
            budgetRepository = budgetRepository,
            coinCapRepository = coinCapRepository,
            receiptScanRepository = receiptScanRepository,
            remoteConfigManager = remoteConfigManager
        )
    }

    override fun onCreate() {
        super.onCreate()

        // Remote Config im Hintergrund laden
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val success = remoteConfigManager.fetchAndActivate()
                Log.d("MoneyMasterApp", "Remote config loaded: $success")

                if (BuildConfig.DEBUG) {
                    Log.d("MoneyMasterApp", "Remote config debug info: ${remoteConfigManager.getDebugInfo()}")
                }
            } catch (e: Exception) {
                Log.e("MoneyMasterApp", "Error loading remote config", e)
            }
        }
    }
}