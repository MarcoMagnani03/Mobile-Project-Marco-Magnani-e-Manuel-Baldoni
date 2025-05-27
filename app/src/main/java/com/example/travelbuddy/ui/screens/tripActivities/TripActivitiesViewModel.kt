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
    val currentFilters: TripActivitiesFilter = TripActivitiesFilter(),
    val showFilters: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

interface TripActivitiesActions {
    fun setTripId(tripId: Long)
    fun loadActivities()
    fun searchActivities(filters: TripActivitiesFilter)
    fun loadTripActivityTypes()
    fun deleteTripActivity(tripActivity: TripActivity)

    fun filterByName(name: String?)
    fun filterByDateRange(startDate: String?, endDate: String?)
    fun filterByPricePerPerson(maxPrice: Double?)
    fun filterByActivityType(typeId: Int?)
    fun resetFilters()
    fun toggleFiltersVisibility()
    fun updateFilters(filters: TripActivitiesFilter)
    fun applyFilters()
    fun hasActiveFilters(): Boolean
    fun getNumberOfFilters(): Int
}

class TripActivitiesViewModel(
    private val tripActivitiesRepository: TripActivitiesRepository,
    private val tripActivitiesTypesRepository: TripActivitiesTypesRepository
): ViewModel() {
    private val _state = MutableStateFlow(TripActivitiesState())
    val state = _state.asStateFlow()

    val actions = object : TripActivitiesActions {
        override fun setTripId(tripId: Long) {
            _state.value = _state.value.copy(tripId = tripId)
        }

        override fun loadActivities() {
            searchActivities(_state.value.currentFilters)
        }

        override fun searchActivities(filters: TripActivitiesFilter) {
            viewModelScope.launch {
                try {
                    _state.value = _state.value.copy(isLoading = true, error = null)

                    val tripId = _state.value.tripId
                    if (tripId <= 0) {
                        _state.value = _state.value.copy(
                            error = "Invalid trip ID",
                            isLoading = false
                        )
                        return@launch
                    }

                    val filteredActivities = tripActivitiesRepository.getFilteredActivities(tripId, filters)
                    _state.value = _state.value.copy(
                        tripActivities = filteredActivities,
                        currentFilters = filters,
                        isLoading = false
                    )
                } catch (e: Exception) {
                    _state.value = _state.value.copy(
                        error = "Error filtering trip activities: ${e.message}",
                        isLoading = false
                    )
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
            val newFilters = _state.value.currentFilters.copy(name = name?.takeIf { it.isNotBlank() })
            searchActivities(newFilters)
        }

        override fun filterByDateRange(startDate: String?, endDate: String?) {
            val newFilters = _state.value.currentFilters.copy(
                startDate = startDate?.takeIf { it.isNotBlank() },
                endDate = endDate?.takeIf { it.isNotBlank() }
            )
            searchActivities(newFilters)
        }

        override fun filterByPricePerPerson(maxPrice: Double?) {
            val newFilters = _state.value.currentFilters.copy(pricePerPerson = maxPrice)
            searchActivities(newFilters)
        }

        override fun filterByActivityType(typeId: Int?) {
            val newFilters = _state.value.currentFilters.copy(tripActivityTypeId = typeId)
            searchActivities(newFilters)
        }

        override fun resetFilters() {
            val emptyFilters = TripActivitiesFilter()
            _state.value = _state.value.copy(currentFilters = emptyFilters)
            searchActivities(emptyFilters)
        }

        override fun toggleFiltersVisibility() {
            _state.value = _state.value.copy(showFilters = !_state.value.showFilters)
        }

        override fun updateFilters(filters: TripActivitiesFilter) {
            _state.value = _state.value.copy(currentFilters = filters)
        }

        override fun applyFilters() {
            searchActivities(_state.value.currentFilters)
        }

        override fun hasActiveFilters(): Boolean {
            val filters = _state.value.currentFilters
            return filters.name != null ||
                    filters.startDate != null ||
                    filters.endDate != null ||
                    filters.pricePerPerson != null ||
                    filters.tripActivityTypeId != null
        }

        override fun getNumberOfFilters(): Int {
            val filters = _state.value.currentFilters

            var counter = 0

            if(filters.name != null){
                counter++
            }
            if(filters.startDate != null){
                counter++
            }
            if(filters.endDate != null){
                counter++
            }
            if(filters.pricePerPerson != null){
                counter++
            }
            if(filters.tripActivityTypeId != null){
                counter++
            }
            return counter
        }

        override fun deleteTripActivity(tripActivity: TripActivity) {
            viewModelScope.launch {
                try {
                    tripActivitiesRepository.delete(tripActivity)
                    loadActivities()
                } catch (e: Exception) {
                    _state.value = _state.value.copy(
                        error = e.message ?: "Error deleting the trip activity, try again later"
                    )
                }
            }
        }
    }
}