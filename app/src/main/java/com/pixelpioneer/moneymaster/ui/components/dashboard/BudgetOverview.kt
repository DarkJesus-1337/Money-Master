package com.pixelpioneer.moneymaster.ui.components.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pixelpioneer.moneymaster.R

/**
 * A component for displaying a compact version of recent transactions (for dashboard)
 */
@Composable
fun BudgetOverview(
    budgets: List<com.pixelpioneer.moneymaster.data.model.Budget>,
    onBudgetsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onBudgetsClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val topBudgets = budgets.sortedByDescending { it.spent / it.amount }.take(2)

            topBudgets.forEach { budget ->
                BudgetOverviewItem(budget = budget)

                if (budget != topBudgets.last()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                }
            }

            if (budgets.size > 2) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "View All Budgets",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Icon(
                        painter = painterResource(R.drawable.keyboard_arrow_right),
                        contentDescription = "View All",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
