package com.example.travelbuddy.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BalanceEntry(
    val name: String,
    val amount: Double
)

@Composable
fun TripDetailsQuickBalance(
    modifier: Modifier = Modifier,
    balanceEntries: List<BalanceEntry>,
    totalExpenses: Double,
    onViewAllExpensesClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Quick balance",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (balanceEntries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "No expenses made",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                balanceEntries.forEach { entry ->
                    BalanceEntryRow(entry = entry)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color.LightGray,
                thickness = 1.dp
            )

            // Total expenses
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Expenses",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "€${totalExpenses}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (totalExpenses < 0) Color.Red else MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onViewAllExpensesClick) {
                    Text(
                        text = "View All Expenses",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun BalanceEntryRow(entry: BalanceEntry) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Plus or minus symbol
            Text(
                text = if (entry.amount < 0) "+" else "-",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (entry.amount < 0) Color.Green else Color.Red
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Name
            Text(
                text = entry.name,
                fontSize = 16.sp
            )
        }

        // Amount
        Text(
            text = if (entry.amount < 0) "+€${entry.amount}" else "-€${Math.abs(entry.amount)}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (entry.amount < 0) Color.Green else Color.Red
        )
    }
}