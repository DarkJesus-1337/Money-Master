package com.pixelpioneer.moneymaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.pixelpioneer.moneymaster.ui.navigation.MoneyMasterNavHost
import com.pixelpioneer.moneymaster.ui.theme.MoneyMasterTheme
import com.pixelpioneer.moneymaster.ui.viewmodel.BudgetViewModel
import com.pixelpioneer.moneymaster.ui.viewmodel.CategoryViewModel
import com.pixelpioneer.moneymaster.ui.viewmodel.CryptoViewModel
import com.pixelpioneer.moneymaster.ui.viewmodel.ReceiptViewModel
import com.pixelpioneer.moneymaster.ui.viewmodel.StatisticsViewModel
import com.pixelpioneer.moneymaster.ui.viewmodel.TransactionViewModel

class MainActivity : ComponentActivity() {

    private val transactionViewModel: TransactionViewModel by viewModels {
        (application as MoneyMasterApplication).viewModelFactory
    }

    private val categoryViewModel: CategoryViewModel by viewModels {
        (application as MoneyMasterApplication).viewModelFactory
    }

    private val budgetViewModel: BudgetViewModel by viewModels {
        (application as MoneyMasterApplication).viewModelFactory
    }

    private val statisticsViewModel: StatisticsViewModel by viewModels {
        (application as MoneyMasterApplication).viewModelFactory
    }

    private val cryptoViewModel: CryptoViewModel by viewModels {
        (application as MoneyMasterApplication).viewModelFactory
    }

    private val receiptViewModel: ReceiptViewModel by viewModels {
        (application as MoneyMasterApplication).viewModelFactory
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        categoryViewModel.createDefaultCategoriesIfNeeded()

        setContent {
            MoneyMasterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    MoneyMasterNavHost(
                        navController = navController,
                        transactionViewModel = transactionViewModel,
                        categoryViewModel = categoryViewModel,
                        statisticsViewModel = statisticsViewModel,
                        budgetViewModel = budgetViewModel,
                        cryptoViewModel = cryptoViewModel,
                        receiptViewModel = receiptViewModel
                    )
                }
            }
        }
    }
}