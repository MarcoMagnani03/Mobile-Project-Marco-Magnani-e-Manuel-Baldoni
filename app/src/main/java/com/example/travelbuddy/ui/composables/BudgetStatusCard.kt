package com.example.travelbuddy.ui.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.travelbuddy.ui.screens.budgetOverview.BudgetOverviewState
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun BudgetStatusCard(state: BudgetOverviewState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Expense distribution",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Canvas(modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp)
            ) {
                var startAngle = 0f // Starting angle for the pie chart
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.minDimension / 2
                val remainingAmount = state.totalBudget - state.spentSoFar
                val remainingAngle = (remainingAmount / state.totalBudget) * 360f

                state.userContributions.forEach { (user, amount) ->
                    val sweepAngle = (amount / state.totalBudget) * 360f

                    drawArc(
                        color = Color(user.hashCode()).copy(alpha = 0.8f), // Color based on user hash
                        startAngle = startAngle,
                        sweepAngle = sweepAngle.toFloat(),
                        useCenter = true,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Fill
                    )

                    startAngle += sweepAngle.toFloat() // Update start angle for next segment
                }

                drawArc(
                    color = Color.LightGray,
                    startAngle = startAngle,
                    sweepAngle = remainingAngle.toFloat(),
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Fill
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // User contributions
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                state.userContributions.forEach { (userName, amount) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(userName.hashCode()).copy(alpha = 0.8f))
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = userName,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "€${amount.toInt()}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Total Budget",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )

                Text(
                    text = "€${state.totalBudget}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (state.totalBudget > 0) MaterialTheme.colorScheme.primary else Color.Red
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Spent so far",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "€${state.spentSoFar}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Budget used: ${calculatePercentage(state.spentSoFar, state.totalBudget)}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { (state.spentSoFar / state.totalBudget).toFloat().coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.LightGray,
            )
        }
    }
}

private fun calculatePercentage(spentAmount: Double, totalBudget: Double): Int {
    return if (totalBudget > 0) {
        ((spentAmount / totalBudget) * 100).toInt()
    } else {
        0
    }
}