package com.example.travelbuddy.ui.screens.newExpense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelbuddy.ui.TravelBuddyRoute
import com.example.travelbuddy.ui.composables.InputField
import com.example.travelbuddy.ui.composables.InputFieldType
import com.example.travelbuddy.ui.composables.TravelBuddyBottomBar
import com.example.travelbuddy.ui.composables.TravelBuddyButton
import com.example.travelbuddy.ui.composables.TravelBuddyTopBar

@Composable
fun NewExpenseScreen(
    state: NewExpenseState,
    actions: NewExpenseActions,
    navController: NavController
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(state.newExpenseId) {
        state.newExpenseId?.let {
            navController.navigate(TravelBuddyRoute.BudgetOverview(tripId = state.tripId.toString()))
        }
    }

    Scaffold(
        topBar = {
            TravelBuddyTopBar(
                navController = navController,
                title = "New Expense",
                subtitle = "Add a new expense to your trip",
                canNavigateBack = true,
            )
        },
        bottomBar = { TravelBuddyBottomBar(navController) }
    ) { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(contentPadding)
                .padding(12.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            InputField(
                value = state.title,
                onValueChange = actions::setTitle,
                label = "Expense title*",
                placeholder = "Enter expense title",
                type = InputFieldType.Text,
            )

            InputField(
                value = state.amount.toString(),
                onValueChange = { value ->
                    val newAmount = value.toDoubleOrNull() ?: 0.0
                    actions.setAmount(newAmount)
                },
                label = "Amount*",
                placeholder = "Enter amount",
                type = InputFieldType.Text,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            InputField(
                value = state.date,
                onValueChange = actions::setDate,
                label = "Date*",
                type = InputFieldType.DateTime,
            )

            InputField(
                value = state.description,
                onValueChange = actions::setDescription,
                label = "Description",
                placeholder = "Enter description",
                type = InputFieldType.Text,
                modifier = Modifier
                    .defaultMinSize(0.dp, 100.dp),
            )

            if(state.errorMessage.isNotBlank()){
                Text(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            TravelBuddyButton(
                enabled = state.canSubmit && !state.isLoading,
                onClick = actions::createExpense,
                label = "Add expense",
                isLoading = state.isLoading,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add expense",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            )
        }
    }
}