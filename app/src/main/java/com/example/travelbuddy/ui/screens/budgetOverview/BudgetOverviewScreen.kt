package com.example.travelbuddy.ui.screens.budgetOverview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelbuddy.ui.TravelBuddyRoute
import com.example.travelbuddy.ui.composables.TravelBuddyBottomBar
import com.example.travelbuddy.ui.composables.TravelBuddyTopBar
import com.example.travelbuddy.ui.composables.BudgetStatusCard
import com.example.travelbuddy.ui.composables.ExpenseItem

@Composable
fun BudgetOverviewScreen(
    state: BudgetOverviewState,
    actions: BudgetOverviewActions,
    navController: NavController
) {
    Scaffold(
        topBar = {
            TravelBuddyTopBar(
                navController = navController,
                title = "Budget overview",
                subtitle = state.tripDateRange,
                canNavigateBack = true
            )
        },
        bottomBar = { TravelBuddyBottomBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(TravelBuddyRoute.NewExpense(state.tripId.toString())) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Expense",
                    tint = Color.White
                )
            }
        }
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                BudgetStatusCard(state)
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Text(
                    text = "Expenses List",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(state.expenses) { expense ->
                ExpenseItem(
                    expense,
                    onEditClick = {

                    },
                    onDeleteClick = {
                        actions.deleteExpense(expense, state.tripId)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }
}