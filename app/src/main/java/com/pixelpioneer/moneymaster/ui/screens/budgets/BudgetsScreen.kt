package com.pixelpioneer.moneymaster.ui.screens.budgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pixelpioneer.moneymaster.ui.components.ErrorMessage
import com.pixelpioneer.moneymaster.ui.components.budget.BudgetItem
import com.pixelpioneer.moneymaster.ui.components.budget.DeleteBudgetDialog
import com.pixelpioneer.moneymaster.ui.components.emptyview.EmptyBudgetsView
import com.pixelpioneer.moneymaster.ui.navigation.MoneyMasterBottomNavigation
import com.pixelpioneer.moneymaster.ui.navigation.Screen
import com.pixelpioneer.moneymaster.ui.viewmodel.BudgetViewModel
import com.pixelpioneer.moneymaster.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetsScreen(
    navController: NavController,
    budgetViewModel: BudgetViewModel
) {
    val budgetsState = budgetViewModel.budgetsState.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budgets") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.AddBudget.route) }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Budget")
                    }
                }
            )
        },
        bottomBar = { MoneyMasterBottomNavigation(navController) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            when (budgetsState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(budgetsState.data) { budget ->
                            var showDeleteDialog by remember { mutableStateOf(false) }

                            BudgetItem(
                                budget = budget,
                                onClick = {
                                    navController.navigate(Screen.BudgetDetail.createRoute(budget.id))
                                },
                                onEdit = {
                                    navController.navigate(Screen.EditBudget.createRoute(budget.id))
                                },
                                onDelete = {
                                    showDeleteDialog = true
                                }
                            )

                            if (showDeleteDialog) {
                                DeleteBudgetDialog(
                                    budget = budget,
                                    onConfirm = {
                                        budgetViewModel.deleteBudget(budget)
                                        showDeleteDialog = false
                                    },
                                    onDismiss = { showDeleteDialog = false }
                                )
                            }
                        }
                    }
                }

                is UiState.Error -> {
                    ErrorMessage(
                        message = budgetsState.message,
                        onRetry = { budgetViewModel.refreshBudgets() }
                    )
                }

                is UiState.Empty -> {
                    EmptyBudgetsView(
                        onAddButtonClick = { navController.navigate(Screen.AddBudget.route) }
                    )
                }
            }
        }
    }
}
