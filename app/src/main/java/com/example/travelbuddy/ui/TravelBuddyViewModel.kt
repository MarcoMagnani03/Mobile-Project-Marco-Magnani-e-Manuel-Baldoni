package com.example.travelbuddy.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelbuddy.data.database.Trip
import com.example.travelbuddy.data.repositories.UsersRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TripsState(val trips: List<Trip>)

class TravelBuddyViewModel(
    private val usersRepository: UsersRepository
) : ViewModel() {

}
