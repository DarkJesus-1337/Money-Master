package com.pixelpioneer.moneymaster.ui.screens.transactions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pixelpioneer.moneymaster.data.model.Transaction
import com.pixelpioneer.moneymaster.ui.components.ErrorMessage
import com.pixelpioneer.moneymaster.ui.components.transactions.EmptyTransactionsView
import com.pixelpioneer.moneymaster.ui.components.transactions.TransactionItem
import com.pixelpioneer.moneymaster.ui.navigation.MoneyMasterBottomNavigation
import com.pixelpioneer.moneymaster.ui.navigation.Screen
import com.pixelpioneer.moneymaster.ui.viewmodel.TransactionViewModel
import com.pixelpioneer.moneymaster.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel
) {
    val transactionsState = transactionViewModel.transactionsState.collectAsState().value
    var searchQuery by remember { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transactions", style = MaterialTheme.typography.headlineMedium) },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.AddTransaction.route) }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Transaction")
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
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                placeholder = {
                    Text(
                        "Search transactions",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        keyboardController?.hide()
                    }
                )
            )

            when (transactionsState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Success -> {
                    val transactions = transactionsState.data
                    val filteredTransactions = if (searchQuery.isBlank()) {
                        transactions
                    } else {
                        transactions.filter {
                            it.title.contains(searchQuery, ignoreCase = true) ||
                                    it.description.contains(searchQuery, ignoreCase = true) ||
                                    it.category.name.contains(searchQuery, ignoreCase = true)
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredTransactions) { transaction ->
                            TransactionItem(
                                transaction = transaction,
                                onClick = { onTransactionClick(transaction, navController) },
                                onDeleteClick = { transactionViewModel.deleteTransaction(transaction) }
                            )
                            HorizontalDivider()
                        }
                    }
                }

                is UiState.Error -> {
                    ErrorMessage(
                        message = transactionsState.message,
                        onRetry = { /* Reload data */ }
                    )
                }

                is UiState.Empty -> {
                    EmptyTransactionsView(
                        onAddButtonClick = { navController.navigate(Screen.AddTransaction.route) }
                    )
                }
            }
        }
    }
}

private fun onTransactionClick(transaction: Transaction, navController: NavController) {
    navController.navigate(Screen.TransactionDetail.createRoute(transaction.id))
}
