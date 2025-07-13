package com.pixelpioneer.moneymaster.ui.components.dashboard

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixelpioneer.moneymaster.data.model.Transaction

/**
 * A component for displaying a list of recent transactions
 */
@Composable
fun RecentTransactionsList(
    transactions: List<Transaction>,
    onTransactionClick: (Transaction) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(transactions) { transaction ->
            RecentTransactionItem(
                transaction = transaction,
                onClick = { onTransactionClick(transaction) }
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
