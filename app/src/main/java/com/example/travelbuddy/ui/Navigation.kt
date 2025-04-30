package com.example.travelbuddy.ui

import com.example.travelbuddy.ui.screens.home.HomeScreen
import com.example.travelbuddy.ui.screens.login.LoginScreen
import com.example.travelbuddy.ui.screens.login.LoginViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.travelbuddy.data.repositories.UserSessionRepository
import com.example.travelbuddy.ui.screens.camera.CameraCaptureScreen
import com.example.travelbuddy.ui.screens.camera.ImagePreviewScreen
import com.example.travelbuddy.ui.screens.code.CodeScreen
import com.example.travelbuddy.ui.screens.code.CodeViewModel
import com.example.travelbuddy.ui.screens.friend.FriendScreen
import com.example.travelbuddy.ui.screens.friend.FriendViewModel
import com.example.travelbuddy.ui.screens.home.HomeViewModel
import com.example.travelbuddy.ui.screens.launch.LaunchScreen
import com.example.travelbuddy.ui.screens.newExpense.NewExpenseScreen
import com.example.travelbuddy.ui.screens.newExpense.NewExpenseViewModel
import com.example.travelbuddy.ui.screens.newTrip.NewTripScreen
import com.example.travelbuddy.ui.screens.newTrip.NewTripViewModel
import com.example.travelbuddy.ui.screens.newTripActivity.NewTripActivityViewModel
import com.example.travelbuddy.ui.screens.newTripActivity.NewTripActivityScreen
import com.example.travelbuddy.ui.screens.notifications.NotificationsScreen
import com.example.travelbuddy.ui.screens.notifications.NotificationsViewModel
import com.example.travelbuddy.ui.screens.profile.EditProfileScreen
import com.example.travelbuddy.ui.screens.profile.ProfileScreen
import com.example.travelbuddy.ui.screens.profile.ProfileViewModel
import com.example.travelbuddy.ui.screens.setting.ChangePasswordScreen
import com.example.travelbuddy.ui.screens.setting.ChangePinScreen
import com.example.travelbuddy.ui.screens.setting.SettingScreen
import com.example.travelbuddy.ui.screens.setting.SettingViewModel
import com.example.travelbuddy.ui.screens.signup.SignUpViewModel
import com.example.travelbuddy.ui.screens.signup.SignUpScreen
import com.example.travelbuddy.ui.screens.tripDetails.TripDetailsScreen
import com.example.travelbuddy.ui.screens.tripDetails.TripDetailsViewModel
import com.example.travelbuddy.utils.ImageUtils
import com.example.travelbuddy.utils.getBackStackEntryOrNull
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

sealed interface TravelBuddyRoute {
    @Serializable data object Home : TravelBuddyRoute
    @Serializable data object Login : TravelBuddyRoute
    @Serializable data object Code : TravelBuddyRoute
    @Serializable data object SignUp : TravelBuddyRoute
    @Serializable data object Launch : TravelBuddyRoute
    @Serializable data object NewTrip : TravelBuddyRoute
    @Serializable data object CameraCapture : TravelBuddyRoute
    @Serializable data object ImagePreview : TravelBuddyRoute
    @Serializable data object Profile : TravelBuddyRoute
    @Serializable data object EditProfile : TravelBuddyRoute
    @Serializable data object Setting:TravelBuddyRoute
    @Serializable data object Friend:TravelBuddyRoute
    @Serializable data object Notification:TravelBuddyRoute
    @Serializable data object ChangePassword: TravelBuddyRoute
    @Serializable data object ChangePin: TravelBuddyRoute
    @Serializable data class TripDetails(val tripId: String) : TravelBuddyRoute
    @Serializable data class NewTripActivity(val tripId: String) : TravelBuddyRoute
    @Serializable data class NewExpense(val tripId: String) : TravelBuddyRoute
}

@Composable
fun TravelBuddyNavGraph(navController: NavHostController) {
    val userSession = koinInject<UserSessionRepository>()
    val hasPin by userSession.hasPinFlow.collectAsStateWithLifecycle(initialValue = false)

    NavHost(
        navController = navController,
        startDestination = TravelBuddyRoute.Launch
    ) {
        composable<TravelBuddyRoute.Launch> {
            LaunchScreen(navController)
        }

        composable<TravelBuddyRoute.Home> {
            val homeViewModel = koinViewModel<HomeViewModel>()
            val state by homeViewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                homeViewModel.actions.loadUserWithTrips()
            }

            HomeScreen(state, homeViewModel.actions, navController)
        }

        composable<TravelBuddyRoute.Friend> {
            val friendViewModel = koinViewModel<FriendViewModel>()
            val state by friendViewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                friendViewModel.actions.loadFriends()
            }

            FriendScreen(state, friendViewModel.actions, navController)
        }

        composable<TravelBuddyRoute.Notification> {
            val notificationsViewModel = koinViewModel<NotificationsViewModel>()
            val state by notificationsViewModel.state.collectAsStateWithLifecycle()

            NotificationsScreen(state, notificationsViewModel.actions, navController)
        }

        composable<TravelBuddyRoute.Login> {
            val loginViewModel = koinViewModel<LoginViewModel>()
            val state by loginViewModel.state.collectAsStateWithLifecycle()
            LoginScreen(state, loginViewModel.actions, navController)
        }

        composable<TravelBuddyRoute.Profile> {
            val profileViewModel = koinViewModel<ProfileViewModel>()
            val state by profileViewModel.state.collectAsStateWithLifecycle()
            ProfileScreen(state, profileViewModel.actions, navController)
        }

        composable<TravelBuddyRoute.EditProfile> {
            val parentEntry = navController.getBackStackEntry(TravelBuddyRoute.Profile)
            val profileViewModel = koinViewModel<ProfileViewModel>(
                viewModelStoreOwner = parentEntry
            )

            EditProfileScreen(
                state = profileViewModel.state.collectAsStateWithLifecycle().value,
                actions = profileViewModel.actions,
                navController = navController
            )
        }

        composable<TravelBuddyRoute.Code> {
            val codeViewModel = koinViewModel<CodeViewModel>()
            val state by codeViewModel.state.collectAsStateWithLifecycle()

            CodeScreen(
                state = state,
                actions = codeViewModel.actions,
                navController = navController,
                isNewPinSetup = !hasPin,
                appPreferences = userSession
            )
        }

        composable<TravelBuddyRoute.SignUp> {
            val loginViewModel = koinViewModel<SignUpViewModel>()
            val state by loginViewModel.state.collectAsStateWithLifecycle()
            SignUpScreen(state, loginViewModel.actions, navController)
        }

        composable<TravelBuddyRoute.Setting> {
            val settingViewModel = koinViewModel<SettingViewModel>()
            val state by settingViewModel.state.collectAsStateWithLifecycle()
            SettingScreen(state, settingViewModel.actions, navController)
        }

        composable<TravelBuddyRoute.ChangePassword> {
            val settingViewModel = koinViewModel<SettingViewModel>()
            val state by settingViewModel.state.collectAsStateWithLifecycle()
            ChangePasswordScreen(state, settingViewModel.actions, navController)
        }

        composable<TravelBuddyRoute.ChangePin> {
            val settingViewModel = koinViewModel<SettingViewModel>()
            val state by settingViewModel.state.collectAsStateWithLifecycle()
            ChangePinScreen(state, settingViewModel.actions, navController)
        }

        composable<TravelBuddyRoute.NewTrip> {
            val newTripViewModel = koinViewModel<NewTripViewModel>()
            val state by newTripViewModel.state.collectAsStateWithLifecycle()
            NewTripScreen(state, newTripViewModel.actions, navController)
        }

        composable<TravelBuddyRoute.CameraCapture> {
            CameraCaptureScreen(
                context = LocalContext.current,
                onImageCaptured = { uri, _ ->
                    navController.navigate(TravelBuddyRoute.ImagePreview) {
                        launchSingleTop = true
                        restoreState = true
                    }
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("previewImageUri", uri.toString())
                },
                onError = { navController.popBackStack() },
                onClose = { navController.popBackStack() }
            )
        }

        composable<TravelBuddyRoute.ImagePreview> {
            val context = LocalContext.current
            val uriString = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<String>("previewImageUri")
            val uri = uriString?.toUri()

            if (uri != null) {
                ImagePreviewScreen(
                    imageUri = uri,
                    onConfirm = {
                        val bytes = ImageUtils.uriToByteArray(context, uri)

                        val signupBackEntry = navController.getBackStackEntryOrNull(TravelBuddyRoute.SignUp)
                        val profileBackEntry = navController.getBackStackEntryOrNull(TravelBuddyRoute.EditProfile)
                        if(signupBackEntry != null){
                            signupBackEntry.savedStateHandle["imageBytes"] = bytes
                            signupBackEntry.savedStateHandle["confirmedImageUri"] = uri.toString()

                            navController.popBackStack(TravelBuddyRoute.SignUp, inclusive = false)
                        }

                        println(profileBackEntry)
                        if(profileBackEntry != null){
                            profileBackEntry.savedStateHandle["imageBytes"] = bytes
                            profileBackEntry.savedStateHandle["confirmedImageUri"] = uri.toString()

                            navController.popBackStack(TravelBuddyRoute.EditProfile, inclusive = false)
                        }
                    },
                    onRetake = {
                        navController.popBackStack(TravelBuddyRoute.ImagePreview, inclusive = true)
                        navController.navigate(TravelBuddyRoute.CameraCapture) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        composable<TravelBuddyRoute.TripDetails> { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            val tripIdLong = tripId.toLongOrNull() ?: -1L
            val tripDetailsViewModel = koinViewModel<TripDetailsViewModel>()
            val state by tripDetailsViewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(tripId) {
                tripDetailsViewModel.actions.loadTrip(tripIdLong)
                tripDetailsViewModel.actions.loadTripActivityTypes()
            }

            TripDetailsScreen(
                state = state,
                actions = tripDetailsViewModel.actions,
                navController = navController
            )
        }

        composable<TravelBuddyRoute.NewTripActivity> { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            val tripIdLong = tripId.toLongOrNull() ?: -1L
            val newTripActivityViewModel = koinViewModel<NewTripActivityViewModel>()
            val state by newTripActivityViewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(tripId) {
                newTripActivityViewModel.actions.setTripId(tripIdLong)
                newTripActivityViewModel.actions.loadTripActivityTypes()
            }

            NewTripActivityScreen(
                state = state,
                actions = newTripActivityViewModel.actions,
                navController = navController
            )
        }

        composable<TravelBuddyRoute.NewExpense> { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            val tripIdLong = tripId.toLongOrNull() ?: -1L
            val newExpenseViewModel = koinViewModel<NewExpenseViewModel>()
            val state by newExpenseViewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(tripId) {
                newExpenseViewModel.actions.setTripId(tripIdLong)
            }

            NewExpenseScreen(
                state = state,
                actions = newExpenseViewModel.actions,
                navController = navController
            )
        }
    }
}
