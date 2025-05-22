package com.example.travelbuddy.ui.screens.newTrip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelbuddy.ui.TravelBuddyRoute
import com.example.travelbuddy.ui.composables.InputField
import com.example.travelbuddy.ui.composables.InputFieldType
import com.example.travelbuddy.ui.composables.TravelBuddyBottomBar
import com.example.travelbuddy.ui.composables.TravelBuddyButton
import com.example.travelbuddy.ui.composables.TravelBuddyTopBar

@Composable
fun NewTripScreen(
    state: NewTripState,
    actions: NewTripActions,
    navController: NavController
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(state.newTripId) {
        state.newTripId?.let { tripId ->
            navController.navigate(TravelBuddyRoute.TripDetails(tripId = tripId.toString()))
        }
    }

    Scaffold(
        topBar = {
            TravelBuddyTopBar(
                navController = navController,
                title = "New trip",
                subtitle = "Plan your next adventure",
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
                value = state.tripName,
                onValueChange = actions::setTripName,
                label = "Trip name*",
                placeholder = "Enter trip name",
                type = InputFieldType.Text,
            )

            InputField(
                value = state.destination,
                onValueChange = actions::setDestination,
                label = "Destination*",
                placeholder = "Where are you going?",
                type = InputFieldType.Text,
            )

            InputField(
                value = state.startDate,
                onValueChange = actions::setStartDate,
                label = "Start date*",
                type = InputFieldType.Date,
            )

            InputField(
                value = state.endDate,
                onValueChange = actions::setEndDate,
                label = "End date*",
                type = InputFieldType.Date,
            )

            InputField(
                value = state.budget.toString(),
                onValueChange = { value ->
                    val newBudget = value.toDoubleOrNull() ?: 0.0
                    actions.setBudget(newBudget)
                },
                label = "Budget",
                placeholder = "Enter your budget",
                type = InputFieldType.Text,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            InputField(
                value = state.description ?: "",
                onValueChange = actions::setDescription,
                label = "Description",
                placeholder = "Enter a description of the trip",
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
                onClick = actions::createTrip,
                label = "Create trip",
                isLoading = state.isLoading,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create trip",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            )

            Spacer(Modifier.height(10.dp))
        }
    }
}