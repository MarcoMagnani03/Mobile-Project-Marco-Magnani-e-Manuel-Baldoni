package com.example.travelbuddy.ui.screens.tripActivities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelbuddy.data.database.TripActivity
import com.example.travelbuddy.data.database.TripActivityType
import com.example.travelbuddy.data.repositories.TripActivitiesRepository
import com.example.travelbuddy.data.repositories.TripActivitiesTypesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TripActivitiesFilter(
    val name: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val pricePerPerson: Double? = null,
    val tripActivityTypeId: Int? = null
)

data class TripActivitiesState(
    val tripId: Long = 0,
    val tripActivities: List<TripActivity> = emptyList(),
    val tripActivityTypes: List<TripActivityType> = emptyList(),
    val error: String? = null
)

interface TripActivitiesActions {
    fun loadActivities(tripId: Long)
    fun searchActivities(filters: TripActivitiesFilter)
    fun loadTripActivityTypes()

    fun filterByName(name: String?)
    fun filterByDateRange(startDate: String?, endDate: String?)
    fun filterByPricePerPerson(maxPrice: Double?)
    fun filterByActivityType(typeId: Int?)
    fun resetFilters()
}

class TripActivitiesViewModel(
    private val tripActivitiesRepository: TripActivitiesRepository,
    private val tripActivitiesTypesRepository: TripActivitiesTypesRepository
): ViewModel() {
    private val _state = MutableStateFlow(TripActivitiesState())
    val state = _state.asStateFlow()

    private var currentFilters = TripActivitiesFilter()

    val actions = object : TripActivitiesActions {
        override fun loadActivities(tripId: Long) {
            viewModelScope.launch {
                try {
                    _state.value = _state.value.copy(tripId = tripId)
                    val tripActivities = tripActivitiesRepository.getAll(tripId)
                    _state.value = _state.value.copy(tripActivities = tripActivities)
                } catch (e: Exception) {
                    _state.value = _state.value.copy(error = "Error loading the trip activities")
                }
            }
        }

        override fun searchActivities(filters: TripActivitiesFilter) {
            viewModelScope.launch {
                try {
                    val tripId = _state.value.tripId
                    if (tripId <= 0) {
                        _state.value = _state.value.copy(error = "Invalid trip ID")
                        return@launch
                    }

                    val filteredActivities = tripActivitiesRepository.getFilteredActivities(tripId, filters)
                    _state.value = _state.value.copy(tripActivities = filteredActivities)
                } catch (e: Exception) {
                    _state.value = _state.value.copy(error = "Error filtering trip activities: ${e.message}")
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

        override fun filterByName(name: String?) {
            currentFilters = currentFilters.copy(name = name)
            searchActivities(currentFilters)
        }

        override fun filterByDateRange(startDate: String?, endDate: String?) {
            currentFilters = currentFilters.copy(startDate = startDate, endDate = endDate)
            searchActivities(currentFilters)
        }

        override fun filterByPricePerPerson(maxPrice: Double?) {
            currentFilters = currentFilters.copy(pricePerPerson = maxPrice)
            searchActivities(currentFilters)
        }

        override fun filterByActivityType(typeId: Int?) {
            currentFilters = currentFilters.copy(tripActivityTypeId = typeId)
            searchActivities(currentFilters)
        }

        override fun resetFilters() {
            currentFilters = TripActivitiesFilter()
            val tripId = _state.value.tripId
            if (tripId > 0) {
                loadActivities(tripId)
            }
        }
    }
}