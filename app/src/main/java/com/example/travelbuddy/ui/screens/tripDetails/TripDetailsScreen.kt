package com.example.travelbuddy.ui.screens.tripDetails

import TripDetailsCalendar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelbuddy.ui.TravelBuddyRoute
import com.example.travelbuddy.ui.composables.BalanceEntry
import com.example.travelbuddy.ui.composables.ButtonStyle
import com.example.travelbuddy.ui.composables.TravelBuddyBottomBar
import com.example.travelbuddy.ui.composables.TravelBuddyButton
import com.example.travelbuddy.ui.composables.TravelBuddyTopBar
import com.example.travelbuddy.ui.composables.TripDetailsQuickBalance
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TripDetailsScreen(
    state: TripDetailsState,
    actions: TripDetailsActions,
    navController: NavController
){
    val scrollState = rememberScrollState()

    val activityTypesMap = remember(state.tripActivityTypes) {
        state.tripActivityTypes.associateBy { it.id }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm delete", style = MaterialTheme.typography.headlineLarge) },
            text = { Text("Are you sure you want to delete this expense?", style = MaterialTheme.typography.bodyLarge) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        if(state.trip?.trip != null){
                            actions.deleteTrip(state.trip.trip)
                            navController.navigate(TravelBuddyRoute.Home)
                        }
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TravelBuddyTopBar(
                navController = navController,
                title = state.trip?.trip?.name ?: "Trip title",
                subtitle = formatTimeRange(state.trip?.trip?.startDate ?: "", state.trip?.trip?.endDate ?: ""),
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
                .padding(horizontal = 12.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            TripDetailsCalendar(
                activities =
                    state.trip?.activities
                        ?.filter { activity -> parseDate(activity.endDate).after(Date()) }
                        ?.sortedBy { activity -> parseDate(activity.startDate) }
                        ?.take(3) ?: emptyList(),
                activityTypes = activityTypesMap,
                onViewAllClick = {

                }
            )

            TripDetailsQuickBalance(
                balanceEntries = state.trip?.expenses?.map { expense ->
                    BalanceEntry(
                        name = expense.title,
                        amount = expense.amount
                    )
                }?.take(3) ?: emptyList(),
                totalExpenses = state.trip?.expenses?.sumOf { expense -> expense.amount } ?: 0.0,
                onViewAllExpensesClick = {
                    navController.navigate(TravelBuddyRoute.BudgetOverview(tripId = state.trip?.trip?.id.toString()))
                }
            )

            Spacer(Modifier.height(10.dp))

            Column (
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TravelBuddyButton(
                    style = ButtonStyle.PRIMARY,
                    label = "Edit trip",
                    onClick = {
                        // TODO: con modale di conferma

                    }
                )

                TravelBuddyButton(
                    style = ButtonStyle.ERROR,
                    label = "Delete trip",
                    onClick = {
                        showDeleteDialog = true
                    }
                )
            }

            Spacer(Modifier.height(10.dp))
        }
    }
}

private fun parseDate(dateString: String): Date {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return try {
        format.parse(dateString) ?: Date()
    } catch (e: Exception) {
        Date()
    }
}

private fun formatDateToMonthDay(dateString: String): String {
    val date = parseDate(dateString)
    val format = SimpleDateFormat("MMM dd", Locale.getDefault())
    return format.format(date)
}

private fun formatTimeRange(startDate: String, endDate: String): String {
    val start = parseDate(startDate)
    val end = parseDate(endDate)
    val timeFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    return "${timeFormat.format(start)} - ${timeFormat.format(end)}"
}