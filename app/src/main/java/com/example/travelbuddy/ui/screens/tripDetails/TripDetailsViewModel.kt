package com.example.travelbuddy.ui.screens.tripDetails

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelbuddy.data.database.Group
import com.example.travelbuddy.data.database.Trip
import com.example.travelbuddy.data.database.TripActivityType
import com.example.travelbuddy.data.database.TripWithActivitiesAndExpensesAndPhotosAndUsers
import com.example.travelbuddy.data.repositories.GroupsRepository
import com.example.travelbuddy.data.repositories.TripActivitiesTypesRepository
import com.example.travelbuddy.data.repositories.TripsRepository
import com.example.travelbuddy.data.repositories.UserSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class TripDetailsState(
    val trip: TripWithActivitiesAndExpensesAndPhotosAndUsers? = null,
    val tripActivityTypes: List<TripActivityType> = emptyList(),
    val error: String? = null
)

interface TripDetailsActions {
    fun loadTrip(tripId: Long)
    fun loadTripActivityTypes()
    fun deleteTrip()
    fun removeUserFromGroup()
}

class TripDetailsViewModel(
    private val tripsRepository: TripsRepository,
    private val tripActivitiesTypesRepository: TripActivitiesTypesRepository,
    private val groupsRepository: GroupsRepository,
    private val userSessionRepository: UserSessionRepository
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

        override fun deleteTrip() {
            viewModelScope.launch {
                try {
                    state.value.trip?.let { tripsRepository.delete(it.trip) }
                } catch (e: Exception) {
                    _state.value = _state.value.copy(error = "Error deleting trip")
                }
            }
        }

        override fun removeUserFromGroup() {
            viewModelScope.launch {
                try {
                    val userEmail = userSessionRepository.userEmail.first()
                    val tripId = state.value.trip?.trip?.id

                    if (tripId != null) {
                        val group = Group(
                            userEmail = userEmail.toString(),
                            tripId = tripId,
                            state = true
                        )
                        groupsRepository.delete(group)
                    }
                } catch (e: Exception) {
                    _state.value = _state.value.copy(error = "Error deleting trip")
                }
            }
        }
    }
}