package com.pixelpioneer.moneymaster.ui.screens.camera

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pixelpioneer.moneymaster.data.model.Receipt
import com.pixelpioneer.moneymaster.data.model.ReceiptItem
import com.pixelpioneer.moneymaster.util.FormatUtils
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptConfirmationScreen(
    receipt: Receipt,
    onNavigateBack: () -> Unit,
    onConfirm: (Receipt) -> Unit
) {
    var editedReceipt by remember { mutableStateOf(receipt) }
    var showAddItemDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kassenzettel bestätigen") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(painterResource(R.drawable.arrow_back), contentDescription = "Zurück")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddItemDialog = true }
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Artikel hinzufügen")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            ReceiptHeaderSection(
                receipt = editedReceipt,
                onReceiptChanged = { editedReceipt = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Artikel",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(editedReceipt.items) { item ->
                    ReceiptItemCard(
                        item = item,
                        onItemChanged = { newItem ->
                            editedReceipt = editedReceipt.copy(
                                items = editedReceipt.items.map {
                                    if (it == item) newItem else it
                            )
                        },
                        onItemDeleted = {
                            editedReceipt = editedReceipt.copy(
                                items = editedReceipt.items.filter { it != item }
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Gesamtbetrag:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = FormatUtils.formatCurrencyReceipe(editedReceipt.items.sumOf { it.price }),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onConfirm(editedReceipt) },
                modifier = Modifier.fillMaxWidth(),
                enabled = editedReceipt.items.isNotEmpty()
            ) {
                Text("Transaktionen hinzufügen")
            }
        }
    }

    if (showAddItemDialog) {
        AddItemDialog(
            onDismiss = { showAddItemDialog = false },
            onItemAdded = { newItem ->
                editedReceipt = editedReceipt.copy(
                    items = editedReceipt.items + newItem
                )
                showAddItemDialog = false
            }
        )
    }
}

@Composable
fun ReceiptHeaderSection(
    receipt: Receipt,
    onReceiptChanged: (Receipt) -> Unit
) {
    var editingStore by remember { mutableStateOf(false) }
    var editingDate by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (editingStore) {
                OutlinedTextField(
                    value = receipt.storeName ?: "",
                    onValueChange = { 
                        onReceiptChanged(receipt.copy(storeName = it))
                    },
                    label = { Text("Geschäft") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { editingStore = false }) {
                    Icon(Icons.Default.Edit, contentDescription = "Fertig")
                }
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Geschäft",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = receipt.storeName ?: "Unbekannt",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                IconButton(onClick = { editingStore = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Bearbeiten")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (editingDate) {
                OutlinedTextField(
                    value = receipt.date ?: "",
                    onValueChange = { 
                        onReceiptChanged(receipt.copy(date = it))
                    },
                    label = { Text("Datum") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { editingDate = false }) {
                    Icon(Icons.Default.Edit, contentDescription = "Fertig")
                }
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Datum",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = receipt.date ?: SimpleDateFormat(
                            "dd.MM.yyyy",
                            Locale.getDefault()
                        ).format(Date()),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                IconButton(onClick = { editingDate = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Bearbeiten")
                }
            }
        }
    }
}

@Composable
fun ReceiptItemCard(
    item: ReceiptItem,
    onItemChanged: (ReceiptItem) -> Unit,
    onItemDeleted: () -> Unit
) {
    var editing by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (editing) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                OutlinedTextField(
                    value = item.name,
                    onValueChange = { onItemChanged(item.copy(name = it)) },
                    label = { Text("Artikel") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = if (item.price == 0.0) "" else item.price.toString(),
                    onValueChange = { 
                        val price = it.toDoubleOrNull() ?: 0.0
                        onItemChanged(item.copy(price = price))
                    },
                    label = { Text("Preis") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { editing = false }
                    ) {
                        Text("Fertig")
                    }

                    Button(
                        onClick = onItemDeleted,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Löschen")
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = FormatUtils.formatCurrency(item.price),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = { editing = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Bearbeiten")
                }
            }
        }
    }
}

@Composable
fun AddItemDialog(
    onDismiss: () -> Unit,
    onItemAdded: (ReceiptItem) -> Unit
) {
    var itemName by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Artikel hinzufügen") },
        text = {
            Column {
                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Artikel") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = itemPrice,
                    onValueChange = { itemPrice = it },
                    label = { Text("Preis") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = itemPrice.toDoubleOrNull() ?: 0.0
                    if (itemName.isNotBlank() && price > 0) {
                        onItemAdded(ReceiptItem(itemName, price))
                    }
                }
            ) {
                Text("Hinzufügen")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}