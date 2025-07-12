package com.pixelpioneer.moneymaster.ui.screens.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.ui.components.ErrorMessage
import com.pixelpioneer.moneymaster.ui.navigation.Screen
import com.pixelpioneer.moneymaster.ui.viewmodel.TransactionViewModel
import com.pixelpioneer.moneymaster.util.FormatUtils
import com.pixelpioneer.moneymaster.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    navController: NavController,
    transactionId: Long,
    transactionViewModel: TransactionViewModel
) {
    transactionViewModel.loadTransactionById(transactionId)

    val transactionState = transactionViewModel.selectedTransaction.collectAsState().value

    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaction Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painterResource(R.drawable.arrow_back), contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        },
        floatingActionButton = {
            if (transactionState is UiState.Success) {
                FloatingActionButton(
                    onClick = {
                        transactionViewModel.initFormWithTransaction(transactionState.data)
                        navController.navigate(Screen.AddTransaction.route)
                    }
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Transaction")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (transactionState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Success -> {
                    val transaction = transactionState.data

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    val amountColor = if (transaction.isExpense) {
                                        MaterialTheme.colorScheme.error
                                    } else {
                                        Color.Green
                                    }

                                    val prefix = if (transaction.isExpense) "-" else "+"

                                    Text(
                                        text = "$prefix${FormatUtils.formatCurrency(transaction.amount)}",
                                        style = MaterialTheme.typography.headlineLarge,
                                        color = amountColor,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = if (transaction.isExpense) "Expense" else "Income",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        item { Spacer(modifier = Modifier.height(16.dp)) }

                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "Title",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Text(
                                        text = transaction.title,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "Category",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(Color(transaction.category.color))
                                        )

                                        Text(
                                            text = transaction.category.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            modifier = Modifier.padding(start = 8.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "Date",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Text(
                                        text = FormatUtils.formatDateTime(transaction.date),
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    if (transaction.description.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(16.dp))

                                        Text(
                                            text = "Description",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        Text(
                                            text = transaction.description,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (showDeleteDialog) {
                        AlertDialog(
                            onDismissRequest = { showDeleteDialog = false },
                            title = {
                                Text(
                                    "Delete Transaction",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            },
                            text = {
                                Text(
                                    "Are you sure you want to delete this transaction? This action cannot be undone.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        transactionViewModel.deleteTransaction(transaction)
                                        showDeleteDialog = false
                                        navController.popBackStack()
                                    }
                                ) {
                                    Text("Delete", style = MaterialTheme.typography.labelLarge)
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { showDeleteDialog = false }
                                ) {
                                    Text("Cancel", style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        )
                    }
                }

                is UiState.Error -> {
                    ErrorMessage(
                        message = transactionState.message,
                        onRetry = { transactionViewModel.loadTransactionById(transactionId) }
                    )
                }

                is UiState.Empty -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Transaction not found",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }
        }
    }
}