package com.example.travelbuddy.ui.screens.tripActivities

import android.content.Intent
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelbuddy.data.database.TripActivity
import com.example.travelbuddy.ui.TravelBuddyRoute
import com.example.travelbuddy.ui.composables.CopyToClipboardButton
import com.example.travelbuddy.ui.composables.MapLocation
import com.example.travelbuddy.ui.composables.MapWithColoredMarkers
import com.example.travelbuddy.ui.composables.TravelBuddyBottomBar
import com.example.travelbuddy.ui.composables.TravelBuddyTopBar
import com.example.travelbuddy.ui.composables.TripActivityListItem
import com.example.travelbuddy.utils.parseDateToMillis
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TripActivitiesScreen(
    state: TripActivitiesState,
    actions: TripActivitiesActions,
    navController: NavController
){
    val context = LocalContext.current

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var selectedActivity by remember { mutableStateOf<TripActivity?>(null) }
    var isModalVisible by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf<MapLocation?>(null) }

    val activityTypesMap = remember(state.tripActivityTypes) {
        state.tripActivityTypes.associateBy { it.id }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm delete", style = MaterialTheme.typography.headlineLarge) },
            text = { Text("Are you sure you want to delete this activity?", style = MaterialTheme.typography.bodyLarge) },
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Price per person:", fontWeight = FontWeight.Bold)
                        Text(selectedActivity?.pricePerPerson.toString())
                    }

                    Text("Location:", fontWeight = FontWeight.Bold)
                    Text(selectedActivity?.position.toString())
                    CopyToClipboardButton(
                        textToCopy = selectedActivity?.position.toString(),
                        color = MaterialTheme.colorScheme.primary
                    )

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
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            state = listState,
            modifier = Modifier
                .padding(contentPadding)
                .padding(horizontal = 12.dp)
                .fillMaxSize()
        ) {
            item {
                MapWithColoredMarkers(
                    locations = state.tripActivities.map { tripActivity ->
                        MapLocation(
                            address = tripActivity.position.toString(),
                            title = tripActivity.name,
                            id = tripActivity.id.toString()
                        )
                    },
                    onMarkerClick = { location ->
                        selectedActivity = state.tripActivities.first{ tripActivity ->
                            tripActivity.id.toString() == location.id
                        }
                        isModalVisible = true
                    },
                    selectedLocation = selectedLocation
                )
            }

            items(state.tripActivities) { tripActivity ->
                TripActivityListItem(
                    tripActivity = tripActivity,
                    tripActivityType = state.tripActivityTypes.first { tripActivityType ->
                        tripActivityType.id == tripActivity.tripActivityTypeId
                    },
                    onCardClick = {
                        selectedActivity = tripActivity
                        isModalVisible = true
                    },
                    onEditClick = {
                        navController.navigate(TravelBuddyRoute.EditTripActivity(state.tripId.toString(), tripActivity.id.toString()))
                    },
                    onDeleteClick = {
                        selectedActivity = tripActivity
                        showDeleteDialog = true
                    },
                    onLocateOnMapClick = {
                        coroutineScope.launch {
                            selectedLocation = MapLocation(
                                address = tripActivity.position.toString(),
                                title = tripActivity.name,
                                id = tripActivity.id.toString()
                            )
                            listState.animateScrollToItem(0)
                        }
                    },
                    onAddToCalendar = {
                        val intent = Intent(Intent.ACTION_INSERT).apply {
                            data = Events.CONTENT_URI
                            putExtra(Events.TITLE, tripActivity.name)
                            putExtra(Events.DESCRIPTION, tripActivity.notes + "\nPrice per person: €" + tripActivity.pricePerPerson)
                            putExtra(Events.EVENT_LOCATION, tripActivity.position)
                            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, parseDateToMillis(tripActivity.startDate))
                            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, parseDateToMillis(tripActivity.endDate))
                        }
                        context.startActivity(intent)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }
}