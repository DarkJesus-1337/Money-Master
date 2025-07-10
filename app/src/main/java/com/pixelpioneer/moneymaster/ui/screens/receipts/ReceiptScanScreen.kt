package com.pixelpioneer.moneymaster.ui.screens.receipts

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ListItem
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pixelpioneer.moneymaster.data.model.TransactionCategory
import com.pixelpioneer.moneymaster.ui.viewmodel.CategoryViewModel
import com.pixelpioneer.moneymaster.ui.viewmodel.ReceiptScanViewModel
import com.pixelpioneer.moneymaster.ui.viewmodel.TransactionViewModel
import java.io.File

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

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedCategory by remember { mutableStateOf<TransactionCategory?>(null) }
    var scanTriggered by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        scanTriggered = false
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Beleg scannen", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        if (selectedImageUri == null) {
            Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                Text("Bild auswählen")
            }
        } else {
            Text("Bild ausgewählt: ${selectedImageUri}")
            Spacer(modifier = Modifier.height(8.dp))
            if (categories.isNotEmpty()) {
                DropdownMenuCategorySelector(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    if (selectedImageUri != null && selectedCategory != null) {
                        try {
                            val file = uriToFile(selectedImageUri!!, context)
                            Log.d("ReceiptScan", "Starte Scan für Datei: ${file.absolutePath}")
                            receiptScanViewModel.scanReceipt(file, selectedCategory!!)
                            scanTriggered = true
                        } catch (e: Exception) {
                            Log.e("ReceiptScan", "Fehler beim Laden der Datei: ${e.message}")
                        }
                    }
                },
                enabled = !isLoading && selectedCategory != null && selectedImageUri != null
            ) {
                Text("Scannen")
            }
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }

        error?.let {
            Text("Fehler: $it", color = MaterialTheme.colorScheme.error)
        }

        if (scanTriggered && !isLoading && scannedItems.isEmpty() && error == null) {
            Text("Keine Artikel erkannt.", color = MaterialTheme.colorScheme.error)
        }

        if (scannedItems.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Gefundene Artikel:", style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                items(scannedItems.size) { idx ->
                    val item = scannedItems[idx]
                    ListItem(
                        headlineContent = { Text(item.title) },
                        supportingContent = { Text("%.2f €".format(item.amount)) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                scannedItems.forEach { transactionViewModel.addTransactionDirect(it) }
                navController.popBackStack()
            }) {
                Text("Alle als Transaktionen hinzufügen")
            }
        }
    }
}

@Composable
fun DropdownMenuCategorySelector(
    categories: List<TransactionCategory>,
    selectedCategory: TransactionCategory?,
    onCategorySelected: (TransactionCategory) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Button(
            onClick = { expanded = true }
        ) {
            Text(selectedCategory?.name ?: "Kategorie wählen")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Hilfsfunktion: Kopiert das Bild in einen temporären File
fun uriToFile(uri: Uri, context: android.content.Context): File {
    val inputStream = context.contentResolver.openInputStream(uri)
    val tempFile = File.createTempFile("receipt", ".jpg", context.cacheDir)
    inputStream?.use { input ->
        tempFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }
    return tempFile
}
