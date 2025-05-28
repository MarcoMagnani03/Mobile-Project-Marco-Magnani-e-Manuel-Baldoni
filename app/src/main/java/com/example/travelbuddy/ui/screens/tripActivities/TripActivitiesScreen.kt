package com.example.travelbuddy.ui.screens.tripActivities

import android.content.Intent
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.travelbuddy.data.database.TripActivity
import com.example.travelbuddy.data.database.TripActivityType
import com.example.travelbuddy.ui.TravelBuddyRoute
import com.example.travelbuddy.ui.composables.CopyToClipboardButton
import com.example.travelbuddy.ui.composables.InputField
import com.example.travelbuddy.ui.composables.InputFieldType
import com.example.travelbuddy.ui.composables.MapLocation
import com.example.travelbuddy.ui.composables.MapWithColoredMarkers
import com.example.travelbuddy.ui.composables.SelectOption
import com.example.travelbuddy.ui.composables.TravelBuddyBottomBar
import com.example.travelbuddy.ui.composables.TravelBuddyButton
import com.example.travelbuddy.ui.composables.TravelBuddyTopBar
import com.example.travelbuddy.ui.composables.TripActivityListItem
import com.example.travelbuddy.ui.theme.Green20
import com.example.travelbuddy.utils.parseDateToMillis
import kotlinx.coroutines.launch

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
                        Text("€${selectedActivity?.pricePerPerson}")
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

    // Usa Box invece di Scaffold per sovrapporre i filtri
    Box(modifier = Modifier.fillMaxSize()) {
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
                    // Aggiungi padding bottom quando i filtri sono visibili
                    .padding(bottom = if (state.showFilters) 280.dp else 0.dp)
            ) {
                // Map
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

                // Loading indicator
                if (state.isLoading) {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ){
                        TravelBuddyButton(
                            label = "Filters",
                            onClick = {
                                actions.toggleFiltersVisibility()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "Toggle filters",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            trailingIcon = {
                                if(actions.hasActiveFilters()){
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = Color.White,
                                                shape = RoundedCornerShape(100.dp)
                                            )
                                            .aspectRatio(1f),
                                        contentAlignment = Alignment.Center // This centers the content
                                    ) {
                                        Text(
                                            actions.getNumberOfFilters().toString(),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        )
                    }
                }

                // Activities list
                items(state.tripActivities) { tripActivity ->
                    TripActivityListItem(
                        tripActivity = tripActivity,
                        tripActivityType = state.tripActivityTypes.firstOrNull { tripActivityType ->
                            tripActivityType.id == tripActivity.tripActivityTypeId
                        } ?: state.tripActivityTypes.firstOrNull(),
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

                // Empty state
                if (!state.isLoading && state.tripActivities.isEmpty()) {
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(
                                text = if (actions.hasActiveFilters())
                                    "No activities found with current filters"
                                else
                                    "No activities yet",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (actions.hasActiveFilters()) {
                                TextButton(
                                    onClick = { actions.resetFilters() }
                                ) {
                                    Text("Clear filters")
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(70.dp))
                }
            }
        }

        AnimatedVisibility(
            visible = state.showFilters,
            enter = slideInVertically(
                initialOffsetY = { it }
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { it }
            ) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            FiltersBottomSheet(
                currentFilters = state.currentFilters,
                tripActivityTypes = state.tripActivityTypes,
                onFiltersUpdate = actions::updateFilters,
                onApplyFilters = {
                    actions.applyFilters()
                    actions.toggleFiltersVisibility()
                },
                onResetFilters = {
                    actions.resetFilters()
                    actions.toggleFiltersVisibility()
                },
                onClose = {
                    actions.toggleFiltersVisibility()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
    }
}

@Composable
private fun FiltersBottomSheet(
    currentFilters: TripActivitiesFilter,
    tripActivityTypes: List<TripActivityType>,
    onFiltersUpdate: (TripActivitiesFilter) -> Unit,
    onApplyFilters: () -> Unit,
    onResetFilters: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var tempFilters by remember(currentFilters) { mutableStateOf(currentFilters) }
    var expandedTypeDropdown by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with title and close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = onClose
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close filters"
                    )
                }
            }

            // Name filter
            OutlinedTextField(
                value = tempFilters.name ?: "",
                onValueChange = {
                    tempFilters = tempFilters.copy(name = it.takeIf { it.isNotBlank() })
                },
                label = { Text("Activity name") },
                placeholder = { Text("Search by name...") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (!tempFilters.name.isNullOrBlank()) {
                        IconButton(
                            onClick = {
                                tempFilters = tempFilters.copy(name = null)
                            }
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                }
            )

            // Date range filters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = tempFilters.startDate ?: "",
                    onValueChange = {
                        tempFilters = tempFilters.copy(startDate = it.takeIf { it.isNotBlank() })
                    },
                    label = { Text("Start date") },
                    placeholder = { Text("YYYY-MM-DD") },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = tempFilters.endDate ?: "",
                    onValueChange = {
                        tempFilters = tempFilters.copy(endDate = it.takeIf { it.isNotBlank() })
                    },
                    label = { Text("End date") },
                    placeholder = { Text("YYYY-MM-DD") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Price filter
            OutlinedTextField(
                value = tempFilters.pricePerPerson?.toString() ?: "",
                onValueChange = {
                    tempFilters = tempFilters.copy(
                        pricePerPerson = it.toDoubleOrNull()
                    )
                },
                label = { Text("Max price per person (€)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (tempFilters.pricePerPerson != null) {
                        IconButton(
                            onClick = {
                                tempFilters = tempFilters.copy(pricePerPerson = null)
                            }
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                }
            )

            InputField(
                value = tripActivityTypes.find { it.id == tempFilters.tripActivityTypeId }?.id.toString(),
                onValueChange = { id ->
                    tempFilters = tempFilters.copy(tripActivityTypeId = id.toInt())
                },
                label = "Type",
                placeholder = "Select type",
                type = InputFieldType.Select,
                options = tripActivityTypes.map { tripActivityType ->
                    SelectOption(
                        label = tripActivityType.label,
                        value = tripActivityType.id.toString()
                    )
                }
            )

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = {
                        tempFilters = TripActivitiesFilter()
                        onResetFilters()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset")
                }

                TextButton(
                    onClick = {
                        onFiltersUpdate(tempFilters)
                        onApplyFilters()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Apply")
                }
            }
        }
    }
}