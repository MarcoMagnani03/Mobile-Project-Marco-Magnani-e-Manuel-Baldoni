package com.example.travelbuddy.ui.screens.launch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.example.travelbuddy.data.repositories.UserSessionRepository
import com.example.travelbuddy.ui.TravelBuddyRoute
import org.koin.compose.koinInject

@Composable
fun LaunchScreen(navController: NavHostController) {
    val userSession = koinInject<UserSessionRepository>()

    val email by userSession.userEmail.collectAsState(initial = "")

    LaunchedEffect(email) {
        when {
            email.isNullOrBlank() -> {
                navController.navigate(TravelBuddyRoute.Login) {
                    popUpTo(0) { inclusive = true }
                }
            }
            else -> {
                navController.navigate(TravelBuddyRoute.Code) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

}