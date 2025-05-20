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
import com.example.travelbuddy.ui.screens.budgetOverview.BudgetOverviewScreen
import com.example.travelbuddy.ui.screens.budgetOverview.BudgetOverviewViewModel
import com.example.travelbuddy.ui.screens.camera.CameraCaptureScreen
import com.example.travelbuddy.ui.screens.camera.ImagePreviewScreen
import com.example.travelbuddy.ui.screens.code.CodeScreen
import com.example.travelbuddy.ui.screens.code.CodeViewModel
import com.example.travelbuddy.ui.screens.discoverEvents.DiscoverEventsScreen
import com.example.travelbuddy.ui.screens.discoverEvents.EventsViewModel
import com.example.travelbuddy.ui.screens.editExpense.EditExpenseScreen
import com.example.travelbuddy.ui.screens.editExpense.EditExpenseViewModel
import com.example.travelbuddy.ui.screens.editTrip.EditTripScreen
import com.example.travelbuddy.ui.screens.editTrip.EditTripViewModel
import com.example.travelbuddy.ui.screens.editTripActivity.EditTripActivityScreen
import com.example.travelbuddy.ui.screens.editTripActivity.EditTripActivityViewModel
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
import com.example.travelbuddy.ui.screens.profile.ViewProfileScreen
import com.example.travelbuddy.ui.screens.profile.ViewProfileViewModel
import com.example.travelbuddy.ui.screens.setting.ChangePasswordScreen
import com.example.travelbuddy.ui.screens.setting.ChangePinScreen
import com.example.travelbuddy.ui.screens.setting.SettingScreen
import com.example.travelbuddy.ui.screens.setting.SettingViewModel
import com.example.travelbuddy.ui.screens.signup.SignUpViewModel
import com.example.travelbuddy.ui.screens.signup.SignUpScreen
import com.example.travelbuddy.ui.screens.tripActivities.TripActivitiesScreen
import com.example.travelbuddy.ui.screens.tripActivities.TripActivitiesState
import com.example.travelbuddy.ui.screens.tripActivities.TripActivitiesViewModel
import com.example.travelbuddy.ui.screens.tripDetails.TripDetailsScreen
import com.example.travelbuddy.ui.screens.tripDetails.TripDetailsViewModel
import com.example.travelbuddy.ui.screens.tripPhotos.TripPhotosScreen
import com.example.travelbuddy.ui.screens.tripPhotos.TripPhotosViewModel
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
    @Serializable data class EditTrip(val tripId: String) : TravelBuddyRoute
    @Serializable data class NewTripActivity(
        val tripId: String,
        val name: String? = "",
        val startDate: String? = "",
        val endDate: String? = "",
        val pricePerPerson: Double? = 0.0,
        val position: String? = "",
        val notes: String? = ""
    ) : TravelBuddyRoute
    @Serializable data class NewExpense(val tripId: String) : TravelBuddyRoute
    @Serializable data class ViewProfile(val userEmail: String) : TravelBuddyRoute
    @Serializable data class BudgetOverview(val tripId: String) : TravelBuddyRoute
    @Serializable data class TripActivities(val tripId: String) : TravelBuddyRoute
    @Serializable data class EditExpense(val tripId: String, val expenseId: String) : TravelBuddyRoute
    @Serializable data class EditTripActivity(val tripId: String, val tripActivityId: String) : TravelBuddyRoute
    @Serializable data class TripPhotos(val tripId: String) : TravelBuddyRoute
    @Serializable data class DiscoverEvents(val tripId: String) : TravelBuddyRoute
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

        composable<TravelBuddyRoute.ViewProfile> { backStackEntry ->
            val userEmail = backStackEntry.arguments?.getString("userEmail") ?: ""
            val viewProfileViewModel = koinViewModel<ViewProfileViewModel>()

            val state by viewProfileViewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(userEmail) {
                viewProfileViewModel.actions.loadUserData(userEmail)
            }

            ViewProfileScreen(
                state = state,
                actions = viewProfileViewModel.actions,
                navController = navController,
                email = userEmail
            )
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

                val name = backStackEntry.arguments?.getString("name")
                if(name != null){
                    newTripActivityViewModel.actions.setTripActivityName(name)
                }

                val startDate = backStackEntry.arguments?.getString("startDate")
                if(startDate != null){
                    newTripActivityViewModel.actions.setStartDate(startDate)
                }

                val endDate = backStackEntry.arguments?.getString("endDate")
                if(endDate != null){
                    newTripActivityViewModel.actions.setEndDate(endDate)
                }

                val pricePerPerson = backStackEntry.arguments?.getString("pricePerPerson")
                if(pricePerPerson != null){
                    newTripActivityViewModel.actions.setPricePerPerson(pricePerPerson.toDouble())
                }

                val position = backStackEntry.arguments?.getString("position")
                if(position != null){
                    newTripActivityViewModel.actions.setPosition(position)
                }

                val notes = backStackEntry.arguments?.getString("notes")
                if(notes != null){
                    newTripActivityViewModel.actions.setNotes(notes)
                }
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

        composable<TravelBuddyRoute.BudgetOverview> { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            val tripIdLong = tripId.toLongOrNull() ?: -1L
            val budgetOverviewViewModel = koinViewModel<BudgetOverviewViewModel>()
            val state by budgetOverviewViewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(tripId) {
                budgetOverviewViewModel.actions.loadBudgetData(tripIdLong)
            }

            BudgetOverviewScreen(
                state = state,
                actions = budgetOverviewViewModel.actions,
                navController = navController
            )
        }

        composable<TravelBuddyRoute.TripActivities> { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            val tripIdLong = tripId.toLongOrNull() ?: -1L
            val tripActivitiesViewModel = koinViewModel<TripActivitiesViewModel>()
            val state by tripActivitiesViewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(tripId) {
                tripActivitiesViewModel.actions.setTripId(tripId = tripIdLong)
                tripActivitiesViewModel.actions.loadActivities()
                tripActivitiesViewModel.actions.loadTripActivityTypes()
            }

            TripActivitiesScreen(
                state = state,
                actions = tripActivitiesViewModel.actions,
                navController = navController
            )
        }

        composable<TravelBuddyRoute.EditTrip> { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            val tripIdLong = tripId.toLongOrNull() ?: -1L
            val editTripViewModel = koinViewModel<EditTripViewModel>()
            val state by editTripViewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(tripId) {
                editTripViewModel.actions.setTripId(tripIdLong)
                editTripViewModel.actions.loadTrip()
            }

            EditTripScreen(
                state = state,
                actions = editTripViewModel.actions,
                navController = navController
            )
        }

        composable<TravelBuddyRoute.EditExpense> { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            val tripIdLong = tripId.toLongOrNull() ?: -1L
            val expenseId = backStackEntry.arguments?.getString("expenseId") ?: ""
            val expenseIdLong = expenseId.toLongOrNull() ?: -1L
            val editExpenseViewModel = koinViewModel<EditExpenseViewModel>()
            val state by editExpenseViewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(tripId) {
                editExpenseViewModel.actions.setTripId(tripIdLong)
                editExpenseViewModel.actions.setExpenseId(expenseIdLong)
                editExpenseViewModel.actions.loadExpense()
            }

            EditExpenseScreen(
                state = state,
                actions = editExpenseViewModel.actions,
                navController = navController
            )
        }

        composable<TravelBuddyRoute.EditTripActivity> { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            val tripIdLong = tripId.toLongOrNull() ?: -1L
            val tripActivityId = backStackEntry.arguments?.getString("tripActivityId") ?: ""
            val tripActivityIdLong = tripActivityId.toLongOrNull() ?: -1L
            val editTripActivityViewModel = koinViewModel<EditTripActivityViewModel>()
            val state by editTripActivityViewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(tripId) {
                editTripActivityViewModel.actions.setTripId(tripIdLong)
                editTripActivityViewModel.actions.setTripActivityId(tripActivityIdLong)
                editTripActivityViewModel.actions.loadTripActivityTypes()
                editTripActivityViewModel.actions.loadTripActivity()
            }

            EditTripActivityScreen(
                state = state,
                actions = editTripActivityViewModel.actions,
                navController = navController
            )
        }

        composable<TravelBuddyRoute.TripPhotos> { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            val tripIdLong = tripId.toLongOrNull() ?: -1L
            val tripPhotosViewModel = koinViewModel<TripPhotosViewModel>()
            val state by tripPhotosViewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(tripId) {
                tripPhotosViewModel.actions.setTripId(tripIdLong)
                tripPhotosViewModel.actions.loadPhotos()
            }

            TripPhotosScreen(
                state = state,
                actions = tripPhotosViewModel.actions,
                navController = navController
            )
        }

        composable<TravelBuddyRoute.DiscoverEvents> { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            val tripIdLong = tripId.toLongOrNull() ?: -1L
            val discoverEventsViewModel = koinViewModel<EventsViewModel>()
            val state by discoverEventsViewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(tripId) {
                discoverEventsViewModel.actions.loadTrip(tripIdLong)
            }

            DiscoverEventsScreen(
                state = state,
                actions = discoverEventsViewModel.actions,
                navController = navController,
            )
        }
    }
}
