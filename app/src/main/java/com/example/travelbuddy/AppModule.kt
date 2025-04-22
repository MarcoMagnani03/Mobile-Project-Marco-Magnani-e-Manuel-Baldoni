package com.example.travelbuddy

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.travelbuddy.data.database.TravelBuddyDatabase
import com.example.travelbuddy.data.repositories.GroupsRepository
import com.example.travelbuddy.data.repositories.TripsRepository
import com.example.travelbuddy.data.repositories.UserSessionRepository
import com.example.travelbuddy.data.repositories.UsersRepository
import com.example.travelbuddy.ui.TravelBuddyViewModel
import com.example.travelbuddy.ui.screens.code.CodeViewModel
import com.example.travelbuddy.ui.screens.home.HomeViewModel
import com.example.travelbuddy.ui.screens.login.LoginViewModel
import com.example.travelbuddy.ui.screens.newTrip.NewTripViewModel
import com.example.travelbuddy.ui.screens.profile.ProfileViewModel
import com.example.travelbuddy.ui.screens.signup.SignUpViewModel
import com.example.travelbuddy.ui.screens.tripDetails.TripDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val Context.dataStore by preferencesDataStore("settings")

val appModule = module {
    single { get<Context>().dataStore }

    single {
        Room.databaseBuilder(
            get(),
            TravelBuddyDatabase::class.java,
            "travel-buddy"
        ).build()
    }

    single { get<TravelBuddyDatabase>().usersDAO() }
    single { get<TravelBuddyDatabase>().tripsDAO() }
    single { get<TravelBuddyDatabase>().groupsDAO() }

    single { UsersRepository(get()) }
    single { UserSessionRepository(get()) }
    single { TripsRepository(get()) }
    single { GroupsRepository(get()) }

    viewModel { HomeViewModel(get(), get()) }
    viewModel { TravelBuddyViewModel() }
    viewModel { LoginViewModel(get(),get()) }
    viewModel { CodeViewModel(get(),get()) }
    viewModel { SignUpViewModel(get())}
    viewModel { NewTripViewModel(get(), get(), get()) }
    viewModel { TripDetailsViewModel(get()) }
    viewModel { ProfileViewModel(get(), get()) }
}
