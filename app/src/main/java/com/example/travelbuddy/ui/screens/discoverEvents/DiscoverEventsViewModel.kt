package com.example.travelbuddy.ui.screens.discoverEvents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelbuddy.data.database.Trip
import com.example.travelbuddy.data.models.Event
import com.example.travelbuddy.data.repositories.EventsRepository
import com.example.travelbuddy.data.repositories.TripsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DiscoverEventsState(
    val trip: Trip? = null,
    val events: List<Event> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

interface EventsActions {
    fun loadEvents()
    fun loadTrip(tripId: Long)
}

class EventsViewModel(
    private val eventsRepository: EventsRepository,
    private val tripsRepository: TripsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(DiscoverEventsState())
    val state = _state.asStateFlow()

    val actions = object : EventsActions {
        override fun loadEvents() {
            viewModelScope.launch {
                try {
                    _state.value = _state.value.copy(isLoading = true, error = null)

                    val events = eventsRepository.getEvents(
                        state.value.trip?.destination ?: "",
                        startDate = state.value.trip?.startDate,
                        endDate = state.value.trip?.endDate
                    )

                    _state.value = _state.value.copy(
                        events = events,
                        isLoading = false,
                    )
                } catch (e: Exception) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message ?: "No events found"
                    )
                }
            }
        }

        override fun loadTrip(tripId: Long) {
            viewModelScope.launch {
                try {
                    val trip = tripsRepository.getTripById(tripId)

                    _state.value = _state.value.copy(
                        trip = trip
                    )
                } catch (e: Exception) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load trip"
                    )
                }
            }
        }
    }
}