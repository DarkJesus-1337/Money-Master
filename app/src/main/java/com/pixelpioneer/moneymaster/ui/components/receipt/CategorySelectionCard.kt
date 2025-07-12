package com.pixelpioneer.moneymaster.ui.components.receipt

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pixelpioneer.moneymaster.data.model.TransactionCategory

@Composable
fun CategorySelectionCard(
    categories: List<TransactionCategory>,
    selectedCategory: TransactionCategory?,
    onCategorySelected: (TransactionCategory) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Kategorie auswählen",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (categories.isNotEmpty()) {
                DropdownMenuCategorySelector(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = onCategorySelected
                )
            } else {
                Text(
                    text = "Keine Kategorien verfügbar",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
