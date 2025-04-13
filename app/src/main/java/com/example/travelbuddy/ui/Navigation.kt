package com.example.travelbuddy.ui

import com.example.travelbuddy.ui.screens.home.HomeScreen
import com.example.travelbuddy.ui.screens.login.LoginScreen
import com.example.travelbuddy.ui.screens.login.LoginViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.travelbuddy.ui.screens.code.CodeScreen
import com.example.travelbuddy.ui.screens.code.CodeViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

sealed interface TravelBuddyRoute {
    @Serializable data object Home : TravelBuddyRoute
    @Serializable data object Login : TravelBuddyRoute
    @Serializable data object Code : TravelBuddyRoute
}

@Composable
fun TravelBuddyNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = TravelBuddyRoute.Code
    ) {
        composable<TravelBuddyRoute.Home> {
            HomeScreen(navController)
        }

        composable<TravelBuddyRoute.Login> {
            val loginViewModel = koinViewModel<LoginViewModel>()
            val state by loginViewModel.state.collectAsStateWithLifecycle()
            LoginScreen(state, loginViewModel.actions, navController)
        }

        composable<TravelBuddyRoute.Code> {
            val codeViewModel = koinViewModel<CodeViewModel>()
            val state by codeViewModel.state.collectAsStateWithLifecycle()
            CodeScreen(state, codeViewModel.actions, navController)
        }
    }
}
