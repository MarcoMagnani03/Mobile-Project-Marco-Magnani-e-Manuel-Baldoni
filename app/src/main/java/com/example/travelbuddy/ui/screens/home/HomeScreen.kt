package com.example.travelbuddy.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelbuddy.ui.TravelBuddyRoute
import com.example.travelbuddy.ui.composables.ActiveTripsSection
import com.example.travelbuddy.ui.composables.PastTripsSection
import com.example.travelbuddy.ui.composables.TravelBuddyBottomBar
import com.example.travelbuddy.ui.composables.TravelBuddyTopBar
import com.example.travelbuddy.ui.composables.UpcomingTripsSection
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    state: HomeState,
    actions: HomeActions,
    navController: NavController
) {
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
        bottomBar = { TravelBuddyBottomBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(TravelBuddyRoute.NewTrip) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add trip",
                    tint = Color.White
                )
            }
        }
    ) { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(contentPadding)
                .padding(horizontal = 12.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            state.userWithTrips?.let {
                ActiveTripsSection(
                    trips = it.trips.filter { trip ->
                        LocalDate.parse(trip.trip.startDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")).isBefore(LocalDate.now()) &&
                                LocalDate.parse(trip.trip.endDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")).isAfter(LocalDate.now())
                    },
                    navController = navController
                )

                UpcomingTripsSection(
                    trips = it.trips.filter { trip ->
                        LocalDate.parse(trip.trip.startDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")).isAfter(LocalDate.now())
                    },
                    navController = navController
                )

                PastTripsSection(
                    trips = it.trips.filter { trip ->
                        LocalDate.parse(trip.trip.endDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")).isBefore(LocalDate.now())
                    },
                    navController = navController
                )

                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }
}