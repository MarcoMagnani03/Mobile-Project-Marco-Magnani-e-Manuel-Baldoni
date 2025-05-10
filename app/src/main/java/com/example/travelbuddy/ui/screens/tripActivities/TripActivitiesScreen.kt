package com.example.travelbuddy.ui.screens.tripActivities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelbuddy.data.database.TripActivity
import com.example.travelbuddy.ui.TravelBuddyRoute
import com.example.travelbuddy.ui.composables.MapLocation
import com.example.travelbuddy.ui.composables.MapWithColoredMarkers
import com.example.travelbuddy.ui.composables.TravelBuddyBottomBar
import com.example.travelbuddy.ui.composables.TravelBuddyTopBar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TripActivitiesScreen(
    state: TripActivitiesState,
    actions: TripActivitiesActions,
    navController: NavController
){
    val scrollState = rememberScrollState()

    var selectedActivity by remember { mutableStateOf<TripActivity?>(null) }
    var isModalVisible by remember { mutableStateOf(false) }

    val activityTypesMap = remember(state.tripActivityTypes) {
        state.tripActivityTypes.associateBy { it.id }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm delete", style = MaterialTheme.typography.headlineLarge) },
            text = { Text("Are you sure you want to delete this expense?", style = MaterialTheme.typography.bodyLarge) },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                }) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        isModalVisible = false
                        selectedActivity?.let { actions.deleteTripActivity(it) }
                        selectedActivity = null
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            }
        )
    }

    if (isModalVisible && selectedActivity != null) {
        AlertDialog(
            onDismissRequest = { isModalVisible = false },
            title = {
                Text(
                    text = selectedActivity?.name ?: "Activity Details",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Start:", fontWeight = FontWeight.Bold)
                        Text(selectedActivity?.startDate ?: "")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("End:", fontWeight = FontWeight.Bold)
                        Text(selectedActivity?.endDate ?: "")
                    }

                    Text("Location:", fontWeight = FontWeight.Bold)
                    Text(selectedActivity?.position.toString())

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Description:", fontWeight = FontWeight.Bold)
                    Text(selectedActivity?.notes ?: "")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        isModalVisible = false
                        selectedActivity = null
                    },
                    modifier = Modifier.padding(0.dp)
                ) {
                    Text("Close")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = true
                    },
                    modifier = Modifier.padding(0.dp)
                ) {
                    Text("Delete", color = Color.Red)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TravelBuddyTopBar(
                navController = navController,
                title = "Activities",
                canNavigateBack = true,
            )
        },
        bottomBar = { TravelBuddyBottomBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(TravelBuddyRoute.NewTripActivity(state.tripId.toString())) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add activity",
                    tint = Color.White
                )
            }
        }
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
            MapWithColoredMarkers(
                locations = state.tripActivities.map { tripActivity ->
                    MapLocation(
                        address = tripActivity.position.toString(),
                        tripActivity = tripActivity
                    )
                },
                onMarkerClick = { location ->
                    selectedActivity = location.tripActivity
                    isModalVisible = true
                }
            )

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