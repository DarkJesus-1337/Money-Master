package com.pixelpioneer.moneymaster.ui.components.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pixelpioneer.moneymaster.util.FormatUtils

/**
 * A component for displaying a single budget item in the budget overview
 */
@Composable
fun BudgetOverviewItem(
    budget: com.pixelpioneer.moneymaster.data.model.Budget
) {
    val progress = if (budget.amount > 0) {
        (budget.spent / budget.amount).coerceIn(0.0, 1.0)
    } else {
        0.0
    }

    val progressColor = when {
        progress >= 0.9 -> MaterialTheme.colorScheme.error
        progress >= 0.7 -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.primary
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color(budget.category.color))
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = budget.category.name,
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Text(
                text = FormatUtils.formatPercentage(progress),
                style = MaterialTheme.typography.titleSmall,
                color = progressColor
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { progress.toFloat() },
            modifier = Modifier.fillMaxWidth(),
            color = progressColor,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${FormatUtils.formatCurrency(budget.spent)} of ${
                    FormatUtils.formatCurrency(
                        budget.amount
                    )
                }",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Remaining: ${FormatUtils.formatCurrency(budget.amount - budget.spent)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}