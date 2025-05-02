package com.example.travelbuddy.ui.screens.tripDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelbuddy.data.database.TripActivityType
import com.example.travelbuddy.data.database.TripWithActivitiesAndExpensesAndPhotosAndUsers
import com.example.travelbuddy.data.repositories.TripActivitiesTypesRepository
import com.example.travelbuddy.data.repositories.TripsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TripDetailsState(
    val trip: TripWithActivitiesAndExpensesAndPhotosAndUsers? = null,
    val tripActivityTypes: List<TripActivityType> = emptyList(),
    val error: String? = null
)

interface TripDetailsActions {
    fun loadTrip(tripId: Long)
    fun loadTripActivityTypes()
}

class TripDetailsViewModel(
    private val tripsRepository: TripsRepository,
    private val tripActivitiesTypesRepository: TripActivitiesTypesRepository
): ViewModel() {
    private val _state = MutableStateFlow(TripDetailsState())
    val state = _state.asStateFlow()

    val actions = object : TripDetailsActions {
        override fun loadTrip(tripId: Long) {
            viewModelScope.launch {
                try {
                    val trip = tripsRepository.getTripWithAllRelationsById(tripId)
                    _state.value = _state.value.copy(trip = trip)
                } catch (e: Exception) {
                    _state.value = _state.value.copy(error = "Error loading the trip")
                }
            }
        }

        override fun loadTripActivityTypes() {
            viewModelScope.launch {
                try {
                    val tripActivityTypes = tripActivitiesTypesRepository.getAll()
                    _state.value = _state.value.copy(tripActivityTypes = tripActivityTypes)
                } catch (e: Exception) {
                    _state.value = _state.value.copy(error = "Error loading the activity types")
                }
            }
        }
    }
}