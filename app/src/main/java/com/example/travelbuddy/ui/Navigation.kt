package com.example.travelbuddy.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.travelbuddy.ui.screens.home.HomeScreen
import com.example.travelbuddy.ui.screens.login.LoginScreen
import kotlinx.serialization.Serializable

sealed interface TravelDiaryRoute {
    @Serializable data object Home : TravelDiaryRoute
    @Serializable data object Login : TravelDiaryRoute
}

@Composable
fun TravelBuddyNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = TravelDiaryRoute.Home
    ) {
        composable<TravelDiaryRoute.Home> {
            HomeScreen(navController)
        }

        composable<TravelDiaryRoute.Login> {
            LoginScreen(navController)
        }
    }
}
