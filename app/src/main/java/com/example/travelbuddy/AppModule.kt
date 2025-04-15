package com.example.travelbuddy

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.travelbuddy.data.database.TravelBuddyDatabase
import com.example.travelbuddy.data.repositories.UserSessionRepository
import com.example.travelbuddy.ui.TravelBuddyViewModel
import com.example.travelbuddy.ui.screens.code.CodeViewModel
import com.example.travelbuddy.ui.screens.login.LoginViewModel
import com.example.travelbuddy.ui.screens.signup.SignUpViewModel
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

    single{ UserSessionRepository(get())}

    viewModel { TravelBuddyViewModel() }

    viewModel { LoginViewModel(get()) }

    viewModel { CodeViewModel(get(),get()) }

    viewModel { SignUpViewModel(get()) }
}
