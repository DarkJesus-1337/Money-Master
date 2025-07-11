package com.pixelpioneer.moneymaster.ui.components.receipt

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ScannedItemCard(
    item: com.pixelpioneer.moneymaster.data.model.Transaction,
    index: Int,
    onItemUpdated: (com.pixelpioneer.moneymaster.data.model.Transaction) -> Unit,
    onItemRemoved: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedTitle by remember { mutableStateOf(item.title) }
    var editedAmount by remember { mutableStateOf(item.amount.toString()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        if (isEditing) {
            EditableItemContent(
                index = index,
                title = editedTitle,
                amount = editedAmount,
                onTitleChange = { editedTitle = it },
                onAmountChange = { editedAmount = it },
                onSave = {
                    val newAmount = editedAmount.toDoubleOrNull()
                    if (newAmount != null && editedTitle.isNotBlank()) {
                        val updatedItem = item.copy(
                            title = editedTitle.trim(),
                            amount = newAmount
                        )
                        onItemUpdated(updatedItem)
                        isEditing = false
                    }
                },
                onCancel = {
                    editedTitle = item.title
                    editedAmount = item.amount.toString()
                    isEditing = false
                },
                onRemove = {
                    onItemRemoved()
                    isEditing = false
                }
            )
        } else {
            ReadOnlyItemContent(
                item = item,
                index = index,
                onEdit = { isEditing = true }
            )
        }
    }
}
