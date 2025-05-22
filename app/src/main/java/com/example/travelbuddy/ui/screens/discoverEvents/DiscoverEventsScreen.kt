package com.example.travelbuddy.ui.screens.discoverEvents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Addchart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.travelbuddy.data.models.Event
import com.example.travelbuddy.ui.TravelBuddyRoute
import com.example.travelbuddy.ui.composables.CopyToClipboardButton
import com.example.travelbuddy.ui.composables.MapLocation
import com.example.travelbuddy.ui.composables.MapWithColoredMarkers
import com.example.travelbuddy.ui.composables.TravelBuddyBottomBar
import com.example.travelbuddy.ui.composables.TravelBuddyButton
import com.example.travelbuddy.ui.composables.TravelBuddyTopBar
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverEventsScreen(
    state: DiscoverEventsState,
    actions: EventsActions,
    navController: NavController
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var selectedEvent by remember { mutableStateOf<Event?>(null) }
    var selectedLocation by remember { mutableStateOf<MapLocation?>(null) }
    var isModalVisible by remember { mutableStateOf(false) }

    if (isModalVisible && selectedEvent != null) {
        AlertDialog(
            onDismissRequest = { isModalVisible = false },
            title = {
                Text(
                    text = selectedEvent?.title ?: "Event",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    selectedEvent!!.price?.let {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Event price: ",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )

                            Text(
                                text = "€${selectedEvent!!.price}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = selectedEvent!!.address,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            CopyToClipboardButton(
                                textToCopy = selectedEvent!!.address,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Date",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = selectedEvent!!.startDateTime.format(DateTimeFormatter.ofPattern("yyyy MMM dd, h:mm a")),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = selectedEvent!!.description.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 8.dp),
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TravelBuddyButton(
                        label = "Add to trip",
                        onClick = {
                            navController.navigate(TravelBuddyRoute.NewTripActivity(
                                tripId = state.trip?.id.toString(),
                                name = selectedEvent!!.title,
                                startDate = selectedEvent!!.startDateTime.toString(),
                                endDate = selectedEvent!!.endDateTime.toString(),
                                pricePerPerson = selectedEvent!!.price,
                                position = selectedEvent!!.address,
                                notes = selectedEvent!!.description
                            ))
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Addchart,
                                contentDescription = "Add to trip",
                            )
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        isModalVisible = false
                        selectedEvent = null
                    },
                    modifier = Modifier.padding(0.dp)
                ) {
                    Text("Close")
                }
            }
        )
    }

    LaunchedEffect(state.trip) {
        if(state.trip != null){
            actions.loadEvents()
        }
    }

    LaunchedEffect(state.events) {
        if(state.events.isNotEmpty()){
            selectedLocation = state.events.map { event ->
                MapLocation(
                    address = event.address.toString(),
                    title = event.title,
                    id = event.id
                )
            }.first()
        }
    }

    Scaffold(
        topBar = {
            TravelBuddyTopBar(
                navController = navController,
                title = "Discover Events",
                subtitle = state.trip?.destination ?: "Find events",
                canNavigateBack = true
            )
        },
        bottomBar = { TravelBuddyBottomBar(navController) }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            // Loading indicator
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.error != null) {
                // Error message
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Error: ${state.error}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    state = listState,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    )
                ) {
                    item {
                        MapWithColoredMarkers(
                            locations = state.events.map { event ->
                                MapLocation(
                                    address = event.address.toString(),
                                    title = event.title,
                                    id = event.id
                                )
                            },
                            onMarkerClick = { location ->
                                selectedEvent = state.events.first{ event ->
                                    event.id == location.id
                                }
                                isModalVisible = true
                            },
                            selectedLocation = selectedLocation
                        )
                    }

                    items(state.events) { event ->
                        EventCard(
                            event = event,
                            onClick = {
                                coroutineScope.launch {
                                    selectedLocation = MapLocation(
                                        address = event.address.toString(),
                                        title = event.title,
                                        id = event.id
                                    )
                                    listState.animateScrollToItem(0)
                                }
                            },
                            navController = navController,
                            tripId = state.trip?.id ?: 0
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EventCard(
    event: Event,
    onClick: () -> Unit,
    navController: NavController,
    tripId: Long
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                if(event.price != null){
                    Text(
                        text = "€${event.price}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = event.address,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    CopyToClipboardButton(
                        textToCopy = event.address,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Date",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = event.startDateTime.format(DateTimeFormatter.ofPattern("yyyy MMM dd, h:mm a")),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = event.description.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            TravelBuddyButton(
                label = "Add to trip",
                onClick = {
                    navController.navigate(TravelBuddyRoute.NewTripActivity(
                        tripId = tripId.toString(),
                        name = event.title,
                        startDate = event.startDateTime.toString(),
                        endDate = event.endDateTime.toString(),
                        pricePerPerson = event.price,
                        position = event.address,
                        notes = event.description
                    ))
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Addchart,
                        contentDescription = "Add to trip",
                    )
                }
            )
        }
    }
}