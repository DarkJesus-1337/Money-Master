package com.pixelpioneer.moneymaster.ui.components.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.data.model.Transaction
import com.pixelpioneer.moneymaster.data.model.TransactionCategory
import com.pixelpioneer.moneymaster.ui.theme.MoneyMasterTheme
import com.pixelpioneer.moneymaster.util.FormatUtils

/**
 * A component for displaying a single transaction in the recent transactions list
 */
@Composable
fun RecentTransactionItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(transaction.category.color)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = transaction.category.name.first().toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = transaction.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = transaction.category.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = FormatUtils.formatDate(transaction.date),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            horizontalAlignment = Alignment.End
        ) {
            val amountColor = if (transaction.isExpense) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.tertiary
            }

            val prefix = if (transaction.isExpense) "-" else "+"

            Text(
                text = "$prefix${FormatUtils.formatCurrency(transaction.amount)}",
                style = MaterialTheme.typography.titleMedium,
                color = amountColor,
                fontWeight = FontWeight.Bold
            )

            Icon(
                painter = painterResource(R.drawable.keyboard_arrow_right),
                contentDescription = "View Details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun RecentTransactionItemPreview() {
    MoneyMasterTheme {
        Column {
            RecentTransactionItem(
                transaction = Transaction(
                    id = 1,
                    amount = 45.67,
                    title = "Supermarkt Einkauf",
                    description = "Wocheneinkauf bei Rewe",
                    category = TransactionCategory(
                        id = 1,
                        name = "Lebensmittel",
                        color = "#4CAF50".toColorInt(),
                        icon = R.drawable.ic_food
                    ),
                    date = System.currentTimeMillis() - 86400000,
                    isExpense = true
                ),
                onClick = { }
            )

            RecentTransactionItem(
                transaction = Transaction(
                    id = 2,
                    amount = 2500.00,
                    title = "Gehalt",
                    description = "Monatliches Gehalt",
                    category = TransactionCategory(
                        id = 6,
                        name = "Einkommen",
                        color = "#2196F3".toColorInt(),
                        icon = R.drawable.ic_finance_chip
                    ),
                    date = System.currentTimeMillis() - 172800000,
                    isExpense = false
                ),
                onClick = { }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecentTransactionItemDarkPreview() {
    MoneyMasterTheme(darkTheme = true){
            RecentTransactionItem(
                transaction = Transaction(
                    id = 3,
                    amount = 89.99,
                    title = "Netflix Premium",
                    description = "Monatliches Streaming-Abo",
                    category = TransactionCategory(
                        id = 3,
                        name = "Unterhaltung",
                        color = "#FF9800".toColorInt(),
                        icon = R.drawable.ic_entertainment
                    ),
                    date = System.currentTimeMillis() - 259200000,
                    isExpense = true
                ),
                onClick = { }
            )
        }
    }


