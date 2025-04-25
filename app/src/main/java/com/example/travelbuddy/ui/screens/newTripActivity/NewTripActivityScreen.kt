package com.example.travelbuddy.ui.screens.newTripActivity

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
import com.example.travelbuddy.ui.composables.SelectOption
import com.example.travelbuddy.ui.composables.TravelBuddyBottomBar
import com.example.travelbuddy.ui.composables.TravelBuddyButton
import com.example.travelbuddy.ui.composables.TravelBuddyTopBar

@Composable
fun NewTripActivityScreen(
    state: NewTripActivityState,
    actions: NewTripActivityActions,
    navController: NavController
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(state.newTripActivityId) {
        state.newTripActivityId?.let {
            navController.navigate(TravelBuddyRoute.TripDetails(tripId = state.tripId.toString()))
        }
    }

    Scaffold(
        topBar = {
            TravelBuddyTopBar(
                navController = navController,
                title = "New Activity",
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
                value = state.tripActivityName,
                onValueChange = actions::setTripActivityName,
                label = "Activity name*",
                placeholder = "Enter activity name",
                type = InputFieldType.Text,
            )

            InputField(
                value = state.startDate,
                onValueChange = actions::setStartDate,
                label = "Start date*",
                type = InputFieldType.DateTime,
            )

            InputField(
                value = state.endDate,
                onValueChange = actions::setEndDate,
                label = "End date*",
                type = InputFieldType.DateTime,
            )

            state.tripActivityTypes?.let {
                InputField(
                    value = state.tripActivityTypeId.toString(),
                    onValueChange = actions::setTripActivityTypeId,
                    label = "Type",
                    placeholder = "Select type",
                    type = InputFieldType.Select,
                    options = it.map { tripActivityType ->
                        SelectOption(
                            label = tripActivityType.label,
                            value = tripActivityType.id.toString()
                        )
                    }
                )
            }

            InputField(
                value = state.pricePerPerson.toString(),
                onValueChange = { value ->
                    val newPricePerPerson = value.toDoubleOrNull() ?: 0.0
                    actions.setPricePerPerson(newPricePerPerson)
                },
                label = "Price per person",
                type = InputFieldType.Text,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            InputField(
                value = state.position?: "",
                onValueChange = actions::setPosition,
                label = "Position",
                type = InputFieldType.Text
            )

            InputField(
                value = state.notes ?: "",
                onValueChange = actions::setNotes,
                label = "Notes",
                type = InputFieldType.Text,
                modifier = Modifier
                    .defaultMinSize(0.dp, 100.dp),
            )

            TravelBuddyButton(
                enabled = state.canSubmit && !state.isLoading,
                onClick = actions::createTripActivity,
                label = "Create trip",
                isLoading = state.isLoading,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create activity",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            )
        }
    }
}