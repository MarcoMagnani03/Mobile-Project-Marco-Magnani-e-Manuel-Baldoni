package com.example.travelbuddy.ui.screens.newTrip

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
import com.example.travelbuddy.ui.TravelBuddyRoute
import com.example.travelbuddy.ui.composables.ActiveTripCard
import com.example.travelbuddy.ui.composables.PastTripsSection
import com.example.travelbuddy.ui.composables.TravelBuddyBottomBar
import com.example.travelbuddy.ui.composables.TravelBuddyTopBar
import com.example.travelbuddy.ui.composables.UpcomingTripsSection

@Composable
fun NewTripScreen(
    navController: NavController
) {
    val scrollState = rememberScrollState()

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
            modifier = Modifier
                .padding(contentPadding)
                .padding(12.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {


            Spacer(Modifier.height(10.dp))
        }
    }
}