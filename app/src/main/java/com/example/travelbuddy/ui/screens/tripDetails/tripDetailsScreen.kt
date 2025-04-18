package com.example.travelbuddy.ui.screens.tripDetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelbuddy.ui.composables.ButtonStyle
import com.example.travelbuddy.ui.composables.InputField
import com.example.travelbuddy.ui.composables.InputFieldType
import com.example.travelbuddy.ui.composables.TravelBuddyBottomBar
import com.example.travelbuddy.ui.composables.TravelBuddyButton
import com.example.travelbuddy.ui.composables.TravelBuddyTopBar

@Composable
fun TripDetailsScreen(
    state: TripDetailsState,
    actions: TripDetailsActions,
    navController: NavController
){
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TravelBuddyTopBar(
                navController = navController,
                title = state.trip?.name ?: "Trip title",
                subtitle = "Apr 16 - Apr 20",
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
            TravelBuddyButton(
                style = ButtonStyle.ERROR,
                label = "Delete trip",
                onClick = {
                    // TODO: con modale di conferma

                }
            )
            Spacer(Modifier.height(10.dp))
        }
    }
}