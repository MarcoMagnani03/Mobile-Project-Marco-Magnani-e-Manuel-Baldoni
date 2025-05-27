package com.example.travelbuddy.ui.screens.editTrip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelbuddy.data.database.Trip
import com.example.travelbuddy.data.database.Group
import com.example.travelbuddy.data.repositories.GroupsRepository
import com.example.travelbuddy.data.repositories.NotificationsRepository
import com.example.travelbuddy.data.repositories.TripsRepository
import com.example.travelbuddy.data.repositories.UserSessionRepository
import com.example.travelbuddy.utils.parseDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditTripState(
    val tripId: Long? = null,
    val tripName: String = "",
    val destination: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val budget: Double? = 0.0,
    val description: String? = null,
    val errorMessage: String = "",
    val isLoading: Boolean = false
) {
    val canSubmit: Boolean
        get() = tripName.isNotBlank() &&
                destination.isNotBlank() &&
                startDate.isNotBlank() &&
                endDate.isNotBlank() &&
                parseDate(startDate).before(parseDate(endDate))
}

interface EditTripActions {
    fun setTripId(value: Long)
    fun setTripName(value: String)
    fun setDestination(value: String)
    fun setStartDate(value: String)
    fun setEndDate(value: String)
    fun setBudget(value: Double)
    fun setDescription(value: String)
    fun setErrorMessage(value: String)
    fun setIsLoading(value: Boolean)
    fun editTrip()
    fun loadTrip()
}

class EditTripViewModel(
    private val tripsRepository: TripsRepository,
    private val groupsRepository: GroupsRepository,
    private val userSessionRepository: UserSessionRepository,
    private val notificationsRepository: NotificationsRepository
): ViewModel() {
    private val _state = MutableStateFlow(EditTripState())
    val state = _state.asStateFlow()

    val actions = object : EditTripActions {
        override fun setTripId(value: Long) {
            _state.update { it.copy(tripId = value) }
        }

        override fun setTripName(value: String) {
            _state.update { it.copy(tripName = value) }
        }

        override fun setDestination(value: String) {
            _state.update { it.copy(destination = value) }
        }

        override fun setStartDate(value: String) {
            _state.update { it.copy(startDate = value) }

            if(parseDate(_state.value.startDate).after(parseDate(_state.value.endDate))){
                setErrorMessage("Start date must be before end date")
            }
            else{
                setErrorMessage("")
            }
        }

        override fun setEndDate(value: String) {
            _state.update { it.copy(endDate = value) }

            if(parseDate(_state.value.startDate).after(parseDate(_state.value.endDate))){
                setErrorMessage("End date must be after start date")
            }
            else{
                setErrorMessage("")
            }
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

        override fun loadTrip() {
            viewModelScope.launch {
                val trip = state.value.tripId?.let { tripsRepository.getTripById(it) }
                if (trip != null) {
                    setTripName(trip.name)
                    setDestination(trip.destination)
                    setStartDate(trip.startDate)
                    setEndDate(trip.endDate)
                    trip.budget?.let { setBudget(it) }
                    trip.description?.let { setDescription(it) }
                }
            }
        }

        override fun editTrip() {
            viewModelScope.launch {
                _state.value = _state.value.copy(isLoading = true)
                try {
                    val newTrip = state.value.tripId?.let {
                        Trip(
                            id = it,
                            name = state.value.tripName,
                            destination = state.value.destination,
                            startDate = state.value.startDate,
                            endDate = state.value.endDate,
                            budget = state.value.budget,
                            description = state.value.description
                        )
                    }

                    if (newTrip != null) {
                        tripsRepository.upsert(newTrip)

                        userSessionRepository.userEmail.collect { currentUserEmail ->
                            if (currentUserEmail.isNullOrBlank()) return@collect

                            val groupEntries = groupsRepository.getGroupMembersByTripId(state.value.tripId ?: 0)
                            groupEntries.toSet().forEach { group ->
                                notificationsRepository.addInfoNotification(description = "$currentUserEmail edited the trip: ${state.value.tripName}", title = "Edited trip", userEmail = group.userEmail)
                            }
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