package com.example.travelbuddy

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.travelbuddy.data.database.TravelBuddyDatabase
import com.example.travelbuddy.data.network.PredictHQApiService
import com.example.travelbuddy.data.repositories.EventsRepository
import com.example.travelbuddy.data.repositories.ExpensesRepository
import com.example.travelbuddy.data.repositories.FriendRequestsRepository
import com.example.travelbuddy.data.repositories.FriendshipsRepository
import com.example.travelbuddy.data.repositories.TripActivitiesTypesRepository
import com.example.travelbuddy.data.repositories.GroupsRepository
import com.example.travelbuddy.data.repositories.NotificationsRepository
import com.example.travelbuddy.data.repositories.NotificationsTypeRepository
import com.example.travelbuddy.data.repositories.PhotosRepository
import com.example.travelbuddy.data.repositories.SettingPreferenceRepository
import com.example.travelbuddy.data.repositories.TripActivitiesRepository
import com.example.travelbuddy.data.repositories.TripsRepository
import com.example.travelbuddy.data.repositories.UserSessionRepository
import com.example.travelbuddy.data.repositories.UsersRepository
import com.example.travelbuddy.ui.TravelBuddyViewModel
import com.example.travelbuddy.ui.screens.budgetOverview.BudgetOverviewViewModel
import com.example.travelbuddy.ui.screens.code.CodeViewModel
import com.example.travelbuddy.ui.screens.discoverEvents.EventsViewModel
import com.example.travelbuddy.ui.screens.editExpense.EditExpenseViewModel
import com.example.travelbuddy.ui.screens.friend.FriendViewModel
import com.example.travelbuddy.ui.screens.home.HomeViewModel
import com.example.travelbuddy.ui.screens.login.LoginViewModel
import com.example.travelbuddy.ui.screens.newExpense.NewExpenseViewModel
import com.example.travelbuddy.ui.screens.newTrip.NewTripViewModel
import com.example.travelbuddy.ui.screens.editTrip.EditTripViewModel
import com.example.travelbuddy.ui.screens.editTripActivity.EditTripActivityViewModel
import com.example.travelbuddy.ui.screens.newTripActivity.NewTripActivityViewModel
import com.example.travelbuddy.ui.screens.notifications.NotificationsViewModel
import com.example.travelbuddy.ui.screens.profile.ProfileViewModel
import com.example.travelbuddy.ui.screens.profile.ViewProfileViewModel
import com.example.travelbuddy.ui.screens.setting.SettingViewModel
import com.example.travelbuddy.ui.screens.signup.SignUpViewModel
import com.example.travelbuddy.ui.screens.tripActivities.TripActivitiesViewModel
import com.example.travelbuddy.ui.screens.tripDetails.TripDetailsViewModel
import com.example.travelbuddy.ui.screens.tripPhotos.TripPhotosViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val Context.dataStore by preferencesDataStore("settings")
val Context.themeDataStore by preferencesDataStore("user_preferences")

val appModule = module {
    single { get<Context>().dataStore }
    single { get<Context>().themeDataStore}

    single {
        Room.databaseBuilder(
            get(),
            TravelBuddyDatabase::class.java,
            "travel-buddy"
        ).fallbackToDestructiveMigration().build()
    }

    single { get<TravelBuddyDatabase>().usersDAO() }
    single { get<TravelBuddyDatabase>().tripsDAO() }
    single { get<TravelBuddyDatabase>().groupsDAO() }
    single { get<TravelBuddyDatabase>().activitiesDAO() }
    single { get<TravelBuddyDatabase>().activitiesTypesDAO() }
    single { get<TravelBuddyDatabase>().expensesDAO() }
    single { get<TravelBuddyDatabase>().friendRequestDAO() }
    single { get<TravelBuddyDatabase>().friendshipsDAO() }
    single { get<TravelBuddyDatabase>().notificationsDAO() }
    single { get<TravelBuddyDatabase>().notificationsTypesDAO() }
    single { get<TravelBuddyDatabase>().photosDAO() }
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                })
            }
            defaultRequest {
                header("Authorization", "Bearer Cl1x_upBnRI1Pr3V39FjaMqY9bbJpLOOdTw-H_Vh")
                contentType(ContentType.Application.Json)
            }
        }
    }

    single { PredictHQApiService(get()) }

    single { UsersRepository(get()) }
    single { UserSessionRepository(get()) }
    single { SettingPreferenceRepository(get()) }
    single { TripsRepository(get()) }
    single { GroupsRepository(get()) }
    single { TripActivitiesRepository(get()) }
    single { TripActivitiesTypesRepository(get()) }
    single { ExpensesRepository(get()) }
    single { FriendRequestsRepository(get(),get())}
    single { FriendshipsRepository(get())}
    single { NotificationsRepository(get())}
    single { NotificationsTypeRepository(get())}
    single { PhotosRepository(get())}
    single { EventsRepository(get())}


    viewModel { HomeViewModel(get(), get()) }
    viewModel { TravelBuddyViewModel() }
    viewModel { LoginViewModel(get(),get()) }
    viewModel { CodeViewModel(get(),get()) }
    viewModel { SignUpViewModel(get())}
    viewModel { NewTripViewModel(get(), get(), get()) }
    viewModel { EditTripViewModel(get(), get(), get()) }
    viewModel { TripDetailsViewModel(get(), get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { SettingViewModel(get(),get(),get()) }
    viewModel { NewTripActivityViewModel(get(), get()) }
    viewModel { NewExpenseViewModel(get(), get()) }
    viewModel { FriendViewModel(get(),get(),get(),get(),get()) }
    viewModel { ViewProfileViewModel(get(),get(),get(),get(),get()) }
    viewModel { NotificationsViewModel(get(),get(),get()) }
    viewModel { BudgetOverviewViewModel(get(),get()) }
    viewModel { TripActivitiesViewModel(get(),get()) }
    viewModel { EditExpenseViewModel(get(),get()) }
    viewModel { EditTripActivityViewModel(get(),get()) }
    viewModel { TripPhotosViewModel(get()) }
    viewModel { EventsViewModel(get(), get()) }
}
