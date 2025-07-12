package com.pixelpioneer.moneymaster.ui.components.transactions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pixelpioneer.moneymaster.data.model.Transaction
import com.pixelpioneer.moneymaster.util.FormatUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val date = Date(transaction.date)
    val formattedDate = dateFormat.format(date)

    val amountColor = if (transaction.isExpense) Color.Red else Color.Green
    val amountPrefix = if (transaction.isExpense) "-" else "+"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = transaction.category.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(transaction.category.color)
                )

                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (transaction.description.isNotBlank()) {
                    Text(
                        text = transaction.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "$amountPrefix${FormatUtils.formatCurrency(transaction.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = amountColor,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}