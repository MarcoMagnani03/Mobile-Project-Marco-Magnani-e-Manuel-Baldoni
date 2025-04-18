package com.example.travelbuddy.ui.screens.tripDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelbuddy.data.database.Trip
import com.example.travelbuddy.data.repositories.TripsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TripDetailsState(
    val trip: Trip? = null,
    val error: String? = null
)

interface TripDetailsActions {
    fun loadTrip(tripId: Long)
}

class TripDetailsViewModel(
    private val tripsRepository: TripsRepository
): ViewModel() {
    private val _state = MutableStateFlow(TripDetailsState())
    val state = _state.asStateFlow()

    val actions = object : TripDetailsActions {
        override fun loadTrip(tripId: Long) {
            viewModelScope.launch {
                try {
                    val trip = tripsRepository.getTripById(tripId)
                    _state.value = _state.value.copy(trip = trip)
                } catch (e: Exception) {
                    _state.value = _state.value.copy(error = "Error loading the trip")
                }
            }

        }
    }
}