package com.pixelpioneer.moneymaster.data.sample

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.pixelpioneer.moneymaster.ui.components.ViewAllTransactionsButton
import com.pixelpioneer.moneymaster.ui.components.dashboard.BudgetOverview
import com.pixelpioneer.moneymaster.ui.components.dashboard.FinancialSummaryCards
import com.pixelpioneer.moneymaster.ui.components.dashboard.RecentTransactionItem
import com.pixelpioneer.moneymaster.ui.components.emptyview.EmptyBudgetsList
import com.pixelpioneer.moneymaster.ui.components.emptyview.EmptyTransactionsList
import com.pixelpioneer.moneymaster.ui.navigation.MoneyMasterBottomNavigation
import com.pixelpioneer.moneymaster.ui.theme.MoneyMasterTheme
import com.pixelpioneer.moneymaster.ui.viewmodel.FinancialSummary

@Composable
@Preview(
    showBackground = true,

)
fun DashboardScreenPreview() {
    MoneyMasterTheme(
        darkTheme = true, // Set to true for dark theme preview
        dynamicColor = false // Disable dynamic color for consistent preview
    ) {

        val navController = rememberNavController()

        DashboardScreenMock(
            navController = navController
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardScreenMock(
    navController: NavController
) {
    // Statische Mock-Daten für Preview
    val mockTransactions = SampleData.sampleTransactions
    val mockBudgets = SampleData.sampleBudgets

    // Calculate financial summary from mock data
    val totalIncome = mockTransactions.filter { !it.isExpense }.sumOf { it.amount }
    val totalExpenses = mockTransactions.filter { it.isExpense }.sumOf { it.amount }
    val balance = totalIncome - totalExpenses

    val financialSummary = FinancialSummary(
        totalIncome = totalIncome,
        totalExpenses = totalExpenses,
        balance = balance
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    IconButton(onClick = { /* Mock action */ }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Transaction")
                    }
                }
            )
        },
        bottomBar = { MoneyMasterBottomNavigation(navController) },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Financial Summary Cards
            item {
                FinancialSummaryCards(financialSummary)
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Recent Transactions
            if (mockTransactions.isNotEmpty()) {
                val recentTransactions = mockTransactions.take(5)

                items(recentTransactions) { transaction ->
                    RecentTransactionItem(
                        transaction = transaction,
                        onClick = { /* Mock click */ }
                    )
                }

                if (mockTransactions.size > 5) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        ViewAllTransactionsButton(
                            onClick = { /* Mock click */ }
                        )
                    }
                }
            } else {
                item {
                    EmptyTransactionsList(
                        onAddTransaction = { /* Mock action */ }
                    )
                }
            }

            // Budget Overview Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Budget Overview",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (mockBudgets.isNotEmpty()) {
                    BudgetOverview(
                        budgets = mockBudgets,
                        onBudgetsClick = { /* Mock click */ }
                    )
                } else {
                    EmptyBudgetsList(
                        onAddBudget = { /* Mock action */ }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
