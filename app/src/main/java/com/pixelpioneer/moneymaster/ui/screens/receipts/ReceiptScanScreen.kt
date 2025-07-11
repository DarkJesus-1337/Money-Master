package com.pixelpioneer.moneymaster.ui.screens.receipts

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pixelpioneer.moneymaster.data.model.TransactionCategory
import com.pixelpioneer.moneymaster.ui.components.receipt.AddAllTransactionsButton
import com.pixelpioneer.moneymaster.ui.components.receipt.CategorySelectionCard
import com.pixelpioneer.moneymaster.ui.components.receipt.ErrorCard
import com.pixelpioneer.moneymaster.ui.components.receipt.ImageSelectionCard
import com.pixelpioneer.moneymaster.ui.components.receipt.InfoCard
import com.pixelpioneer.moneymaster.ui.components.receipt.ScanButton
import com.pixelpioneer.moneymaster.ui.components.receipt.ScannedItemCard
import com.pixelpioneer.moneymaster.ui.viewmodel.CategoryViewModel
import com.pixelpioneer.moneymaster.ui.viewmodel.ReceiptScanViewModel
import com.pixelpioneer.moneymaster.ui.viewmodel.TransactionViewModel
import com.pixelpioneer.moneymaster.util.uriToFile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScanScreen(
    navController: NavController,
    receiptScanViewModel: ReceiptScanViewModel,
    transactionViewModel: TransactionViewModel,
    categoryViewModel: CategoryViewModel
) {
    val context = LocalContext.current
    val scannedItems by receiptScanViewModel.scannedItems.collectAsState()
    val isLoading by receiptScanViewModel.isLoading.collectAsState()
    val error by receiptScanViewModel.error.collectAsState()
    val categories by categoryViewModel.categories.collectAsState(initial = emptyList())

    var editableItems by remember {
        mutableStateOf<List<com.pixelpioneer.moneymaster.data.model.Transaction>>(
            emptyList()
        )
    }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedCategory by remember { mutableStateOf<TransactionCategory?>(null) }
    var scanTriggered by remember { mutableStateOf(false) }

    LaunchedEffect(scannedItems) {
        editableItems = scannedItems.toList()
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        scanTriggered = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Beleg scannen") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ImageSelectionCard(
                    selectedImageUri = selectedImageUri,
                    onImageSelect = { imagePickerLauncher.launch("image/*") }
                )
            }

            if (selectedImageUri != null) {
                item {
                    CategorySelectionCard(
                        categories = categories,
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it }
                    )
                }

                item {
                    ScanButton(
                        isEnabled = !isLoading && selectedCategory != null,
                        isLoading = isLoading,
                        onScan = {
                            selectedImageUri?.let { uri ->
                                selectedCategory?.let { category ->
                                    try {
                                        val file = uriToFile(uri, context)
                                        Log.d(
                                            "ReceiptScan",
                                            "Starte Scan für Datei: ${file.absolutePath}"
                                        )
                                        receiptScanViewModel.scanReceipt(file, category)
                                        scanTriggered = true
                                    } catch (e: Exception) {
                                        Log.e(
                                            "ReceiptScan",
                                            "Fehler beim Laden der Datei: ${e.message}"
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }

            error?.let {
                item {
                    ErrorCard(error = it)
                }
            }

            if (scanTriggered && !isLoading && scannedItems.isEmpty() && error == null) {
                item {
                    InfoCard(message = "Keine Artikel auf dem Beleg erkannt. Versuchen Sie es mit einem anderen Bild.")
                }
            }

            if (editableItems.isNotEmpty()) {
                item {
                    Text(
                        text = "Gefundene Artikel (${editableItems.size})",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                itemsIndexed(editableItems) { index, item ->
                    ScannedItemCard(
                        item = item,
                        index = index + 1,
                        onItemUpdated = { updatedItem ->
                            editableItems = editableItems.toMutableList().apply {
                                set(index, updatedItem)
                            }
                        },
                        onItemRemoved = {
                            editableItems = editableItems.toMutableList().apply {
                                removeAt(index)
                            }
                        }
                    )
                }

                item {
                    AddAllTransactionsButton(
                        totalAmount = editableItems.sumOf { it.amount },
                        onAddAll = {
                            editableItems.forEach { transactionViewModel.addTransactionDirect(it) }
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
