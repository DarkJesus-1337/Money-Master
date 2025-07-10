package com.pixelpioneer.moneymaster.ui.screens.camera

import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.pixelpioneer.moneymaster.data.model.Receipt
import com.pixelpioneer.moneymaster.ui.viewmodel.TransactionViewModel

@Composable
fun ReceiptScannerScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel
) {
    var currentScreen by remember { mutableStateOf(ReceiptScannerState.CAMERA) }
    var capturedReceipt by remember { mutableStateOf<Receipt?>(null) }

    when (currentScreen) {
        ReceiptScannerState.CAMERA -> {
            CameraScreen(
                onNavigateBack = { navController.popBackStack() },
                onReceiptCaptured = { receipt ->
                    capturedReceipt = receipt
                    currentScreen = ReceiptScannerState.CONFIRMATION
                }
            )
        }
        
        ReceiptScannerState.CONFIRMATION -> {
            capturedReceipt?.let { receipt ->
                ReceiptConfirmationScreen(
                    receipt = receipt,
                    onNavigateBack = {
                        currentScreen = ReceiptScannerState.CAMERA
                    },
                    onConfirm = { confirmedReceipt ->
                        confirmedReceipt.items.forEach { item ->
                            transactionViewModel.updateTitle(item.name)
                            transactionViewModel.updateAmount(item.price)
                            transactionViewModel.updateDescription("Aus Kassenzettel: ${confirmedReceipt.storeName ?: "Unbekannt"}")
                            transactionViewModel.updateIsExpense(true)
                            transactionViewModel.createTransaction()
                        }
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

enum class ReceiptScannerState {
    CAMERA, CONFIRMATION
}