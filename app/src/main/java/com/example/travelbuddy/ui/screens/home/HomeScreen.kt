package com.example.travelbuddy.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelbuddy.ui.TravelBuddyRoute

@Composable
fun HomeScreen(
    navController: NavController
) {
    Scaffold() { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(contentPadding).padding(12.dp).fillMaxSize()
        ) {
            Text(
                text = "HOME"
            )

            Button(
                onClick = {
                    navController.navigate(TravelBuddyRoute.Login)
                }
            ) {
                Text(
                    text = "login"
                )
            }
        }
    }
}