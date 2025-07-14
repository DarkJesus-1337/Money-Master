package com.pixelpioneer.moneymaster.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    data object Dashboard : Screen("dashboard", "Home", Icons.Filled.Home)
    data object Transactions : Screen("transactions", "History", Icons.AutoMirrored.Filled.List)
    data object Budgets : Screen("budgets", "Budget", Icons.Filled.DateRange)
    data object Statistics : Screen("statistics", "Stats", Icons.Filled.PieChart)
    data object ReceiptScan : Screen("receipt_scan", "Scan", Icons.Filled.Camera)

    data object AddBudget : Screen("add_budget", "Add Budget")
    data object AddTransaction : Screen("add_transaction", "Add", Icons.Filled.Add)

    data object TransactionDetail : Screen("transaction_detail/{transactionId}", "Details") {
        fun createRoute(transactionId: Long) = "transaction_detail/$transactionId"
    }

    data object BudgetDetail : Screen("budget_detail/{budgetId}", "Budget Details") {
        fun createRoute(budgetId: Long) = "budget_detail/$budgetId"
    }


    data object EditBudget : Screen("edit_budget/{budgetId}", "Edit Budget") {
        fun createRoute(budgetId: Long) = "edit_budget/$budgetId"
    }

    data object EditTransaction : Screen("edit_transaction/{transactionId}", "Edit Transaction") {
        fun createRoute(transactionId: String) = "edit_transaction/$transactionId"
    }
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Transactions,
    Screen.Budgets,
    Screen.Statistics,
    Screen.ReceiptScan
)