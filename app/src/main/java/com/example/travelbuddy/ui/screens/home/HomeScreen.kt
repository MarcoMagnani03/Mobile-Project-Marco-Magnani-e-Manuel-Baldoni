package com.example.travelbuddy.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelbuddy.data.database.Trip
import com.example.travelbuddy.ui.composables.ActiveTripCard
import com.example.travelbuddy.ui.composables.PastTripsSection
import com.example.travelbuddy.ui.composables.TravelBuddyBottomBar
import com.example.travelbuddy.ui.composables.TravelBuddyTopBar
import com.example.travelbuddy.ui.composables.UpcomingTripsSection

@Composable
fun HomeScreen(
    state: HomeState,
    actions: HomeActions,
    navController: NavController
) {
    val veneziaTrip = Trip(
        name = "Weekend a Venezia",
        destination = "Venezia",
        startDate = "Gen 15",
        endDate = "Gen 22",
        budget = 500.00,
        description = ""
    )

    val upcomingTrips = listOf(
        Trip(
            name = "Francia, Parigi",
            destination = "Parigi",
            startDate = "Giu 15",
            endDate = "Giu 22",
            budget = 1000.00,
            description = ""
        ),
        Trip(
            name = "Giappone, Tokyo",
            destination = "Giappone",
            startDate = "Ago 10",
            endDate = "Ago 20",
            budget = 3000.00,
            description = ""
        ),
        Trip(
            name = "Paesi Bassi, Amsterdam",
            destination = "Amsterdam",
            startDate = "Set 5",
            endDate = "Set 12",
            budget = 500.00,
            description = ""
        )
    )

    val pastTrips = listOf(
        Trip(
            name = "Italia, Roma",
            destination = "Roma",
            startDate = "Mar 1",
            endDate = "Mar 6",
            budget = 300.00,
            description = ""
        ),
        Trip(
            name = "Spagna, Barcellona",
            destination = "Barcellona",
            startDate = "Gen 15",
            endDate = "Gen 22",
            budget = 500.00,
            description = ""
        )
    )

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TravelBuddyTopBar(
                navController = navController,
                title = "Travel Buddy",
                subtitle = "Plan & Manage Your Adventures",
                canNavigateBack = false
            )
        },
        bottomBar = { TravelBuddyBottomBar(navController) }
    ) { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(contentPadding)
                .padding(12.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            ActiveTripCard(
                trip = veneziaTrip,
                onAddExpenseClick = {  },
                onAddEventClick = {  }
            )

            Spacer(Modifier.height(10.dp))

            state.userWithTrips?.let {
                UpcomingTripsSection(
                    trips = it.trips,
                    navController = navController
                )
            }

            Spacer(Modifier.height(10.dp))

            PastTripsSection(
                trips = pastTrips,
                navController = navController
            )

            Spacer(Modifier.height(10.dp))
        }
    }
}