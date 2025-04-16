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
import com.example.travelbuddy.data.repositories.UserSessionRepository
import com.example.travelbuddy.ui.screens.code.CodeScreen
import com.example.travelbuddy.ui.screens.code.CodeViewModel
import com.example.travelbuddy.ui.screens.signup.SignUpViewModel
import com.example.travelbuddy.ui.screens.signup.SignUpScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

sealed interface TravelBuddyRoute {
    @Serializable data object Home : TravelBuddyRoute
    @Serializable data object Login : TravelBuddyRoute
    @Serializable data object Code : TravelBuddyRoute
    @Serializable data object SignUp : TravelBuddyRoute
}

@Composable
fun TravelBuddyNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = TravelBuddyRoute.Home
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
            val userSession = koinInject<UserSessionRepository>()

            CodeScreen(
                state = state,
                actions = codeViewModel.actions,
                navController = navController,
                isNewPinSetup = true,
                appPreferences = userSession
            )
        }


        composable<TravelBuddyRoute.SignUp> {
            val loginViewModel = koinViewModel<SignUpViewModel>()
            val state by loginViewModel.state.collectAsStateWithLifecycle()
            SignUpScreen(state, loginViewModel.actions, navController)
        }
    }
}
