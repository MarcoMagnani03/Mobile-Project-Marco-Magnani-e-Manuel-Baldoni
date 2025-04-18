package com.example.travelbuddy.ui.screens.newTrip

import androidx.compose.ui.graphics.vector.Group
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelbuddy.data.database.Trip
import com.example.travelbuddy.data.database.Group
import com.example.travelbuddy.data.repositories.GroupsRepository
import com.example.travelbuddy.data.repositories.TripsRepository
import com.example.travelbuddy.data.repositories.UserSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NewTripState(
    val tripName: String = "",
    val destination: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val budget: Double? = null,
    val description: String? = null,
    val errorMessage: String = "",
    val isLoading: Boolean = false,
    val newTripId: Long? = null
) {
    val canSubmit: Boolean
        get() = tripName.isNotBlank() &&
                destination.isNotBlank() &&
                startDate.isNotBlank() &&
                endDate.isNotBlank()
}

interface NewTripActions {
    fun setTripName(value: String)
    fun setDestination(value: String)
    fun setStartDate(value: String)
    fun setEndDate(value: String)
    fun setBudget(value: Double)
    fun setDescription(value: String)
    fun setErrorMessage(value: String)
    fun setIsLoading(value: Boolean)
    fun setNewTripId(value: Long)
    fun createTrip()
}

class NewTripViewModel(
    private val tripsRepository: TripsRepository,
    private val groupsRepository: GroupsRepository,
    private val userSessionRepository: UserSessionRepository
): ViewModel() {
    private val _state = MutableStateFlow(NewTripState())
    val state = _state.asStateFlow()

    val actions = object : NewTripActions {
        override fun setTripName(value: String) {
            _state.update { it.copy(tripName = value) }
        }

        override fun setDestination(value: String) {
            _state.update { it.copy(destination = value) }
        }

        override fun setStartDate(value: String) {
            _state.update { it.copy(startDate = value) }
        }

        override fun setEndDate(value: String) {
            _state.update { it.copy(endDate = value) }
        }

        override fun setBudget(value: Double) {
            _state.update { it.copy(budget = value) }
        }

        override fun setDescription(value: String) {
            _state.update { it.copy(description = value) }
        }

        override fun setErrorMessage(value: String) {
            _state.update { it.copy(errorMessage = value) }
        }

        override fun setIsLoading(value: Boolean) {
            _state.update { it.copy(isLoading = value) }
        }

        override fun setNewTripId(value: Long) {
            _state.update { it.copy(newTripId = value) }
        }

        override fun createTrip() {
            viewModelScope.launch {
                _state.value = _state.value.copy(isLoading = true)
                try {
                    val newTrip = Trip(
                        name = state.value.tripName,
                        destination = state.value.destination,
                        startDate = state.value.startDate,
                        endDate = state.value.endDate,
                        budget = state.value.budget,
                        description = state.value.description
                    )

                    val tripId = tripsRepository.upsert(newTrip)

                    viewModelScope.launch {
                        userSessionRepository.userEmail.collect { email ->
                            if(email.isNullOrBlank()){
                                return@collect
                            }

                            val newGroup = Group(
                                userEmail = email,
                                tripId = tripId
                            )

                            groupsRepository.upsert(newGroup)

                            _state.value = _state.value.copy(
                                newTripId = tripId
                            )
                        }
                    }
                } catch (e: Exception) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error creating the trip, try again later"
                    )
                }
            }
        }

    }
}