package com.pixelpioneer.moneymaster.ui.screens.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.ui.components.ErrorMessage
import com.pixelpioneer.moneymaster.ui.navigation.Screen
import com.pixelpioneer.moneymaster.ui.viewmodel.TransactionViewModel
import com.pixelpioneer.moneymaster.util.UiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel
) {
    val categoriesState = transactionViewModel.categoriesState.collectAsState().value
    val formState = transactionViewModel.transactionFormState.collectAsState().value

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = formState.date
    )

    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Transaction") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painterResource(R.drawable.arrow_back), contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Transaction Type",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                SegmentedButton(
                    selected = !formState.isExpense,
                    onClick = { transactionViewModel.updateIsExpense(false) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Text("Income")
                }
                SegmentedButton(
                    selected = formState.isExpense,
                    onClick = { transactionViewModel.updateIsExpense(true) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Text("Expense")
                }
            }

            Text(
                text = "Amount",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = if (formState.amount == 0.0) "" else formState.amount.toString(),
                onValueChange = {
                    val amount = it.toDoubleOrNull() ?: 0.0
                    transactionViewModel.updateAmount(amount)
                },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = formState.amountError != null,
                supportingText = {
                    formState.amountError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Text(
                text = "Title",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = formState.title,
                onValueChange = { transactionViewModel.updateTitle(it) },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                isError = formState.titleError != null,
                supportingText = {
                    formState.titleError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Text(
                text = "Description (Optional)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = formState.description,
                onValueChange = { transactionViewModel.updateDescription(it) },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            when (categoriesState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Success -> {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = formState.selectedCategory?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                            isError = formState.categoryError != null,
                            supportingText = {
                                formState.categoryError?.let {
                                    Text(it, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categoriesState.data.forEach { category ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(category.color))
                                            )
                                            Text(
                                                text = category.name,
                                                modifier = Modifier.padding(start = 8.dp)
                                            )
                                        }
                                    },
                                    onClick = {
                                        transactionViewModel.updateSelectedCategory(category)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                is UiState.Error -> {
                    ErrorMessage(
                        message = categoriesState.message,
                        onRetry = {}
                    )
                }

                is UiState.Empty -> {
                    Text(
                        text = "No cate     gories available. Please create categories first.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Text(
                text = "Date",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(Date(formState.date))

            OutlinedTextField(
                value = formattedDate,
                onValueChange = {},
                readOnly = true,
                label = { Text("Date") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    transactionViewModel.updateDate(it)
                                }
                                showDatePicker = false
                            }
                        ) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    transactionViewModel.createTransaction()
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Transaction")
            }

            Button(
                onClick = {
                     navController.navigate(Screen.ReceiptScan.route)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Kassenzettel scannen")
            }
        }
    }
}