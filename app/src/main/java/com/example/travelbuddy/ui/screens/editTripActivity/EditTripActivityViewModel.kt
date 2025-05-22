package com.example.travelbuddy.ui.screens.editTripActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelbuddy.data.database.TripActivityType
import com.example.travelbuddy.data.database.TripActivity
import com.example.travelbuddy.data.repositories.TripActivitiesTypesRepository
import com.example.travelbuddy.data.repositories.TripActivitiesRepository
import com.example.travelbuddy.utils.parseDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditTripActivityState(
    val tripId: Long? = null,
    val tripActivityId: Long? = null,
    val tripActivityName: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val tripActivityTypeId: String? = null,
    val tripActivityTypes: List<TripActivityType>? = null,
    val pricePerPerson: Double = 0.0,
    val position: String = "",
    val notes: String = "",
    val errorMessage: String = "",
    val isLoading: Boolean = false,
) {
    val canSubmit: Boolean
        get() = tripActivityName.isNotBlank() &&
                startDate.isNotBlank() &&
                endDate.isNotBlank() &&
                parseDateTime(startDate).before(parseDateTime(endDate))
}

interface EditTripActivityActions {
    fun setTripId(value: Long)
    fun setTripActivityId(value: Long)
    fun setTripActivityName(value: String)
    fun setStartDate(value: String)
    fun setEndDate(value: String)
    fun setTripActivityTypeId(value: String)
    fun setPricePerPerson(value: Double)
    fun setPosition(value: String)
    fun setNotes(value: String)
    fun setErrorMessage(value: String)
    fun setIsLoading(value: Boolean)
    fun editTripActivity()
    fun loadTripActivityTypes()
    fun loadTripActivity()
}

class EditTripActivityViewModel(
    private val tripActivitiesRepository: TripActivitiesRepository,
    private val tripActivitiesTypesRepository: TripActivitiesTypesRepository
): ViewModel() {
    private val _state = MutableStateFlow(EditTripActivityState())
    val state = _state.asStateFlow()

    val actions = object : EditTripActivityActions {
        override fun setTripId(value: Long) {
            _state.update { it.copy(tripId = value) }
        }

        override fun setTripActivityId(value: Long) {
            _state.update { it.copy(tripActivityId = value) }
        }

        override fun setTripActivityName(value: String) {
            _state.update { it.copy(tripActivityName = value) }
        }

        override fun setStartDate(value: String) {
            _state.update { it.copy(startDate = value) }

            if(parseDateTime(_state.value.startDate).after(parseDateTime(_state.value.endDate))){
                setErrorMessage("Start date must be before end date")
            }
            else{
                setErrorMessage("")
            }
        }

        override fun setEndDate(value: String) {
            _state.update { it.copy(endDate = value) }

            if(parseDateTime(_state.value.startDate).after(parseDateTime(_state.value.endDate))){
                setErrorMessage("End date must be after start date")
            }
            else{
                setErrorMessage("")
            }
        }

        override fun setTripActivityTypeId(value: String) {
            _state.update { it.copy(tripActivityTypeId = value) }
        }

        override fun setPricePerPerson(value: Double) {
            _state.update { it.copy(pricePerPerson = value) }
        }

        override fun setPosition(value: String) {
            _state.update { it.copy(position = value) }
        }

        override fun setNotes(value: String) {
            _state.update { it.copy(notes = value) }
        }

        override fun setErrorMessage(value: String) {
            _state.update { it.copy(errorMessage = value) }
        }

        override fun setIsLoading(value: Boolean) {
            _state.update { it.copy(isLoading = value) }
        }

        override fun editTripActivity() {
            viewModelScope.launch {
                _state.value = _state.value.copy(isLoading = true)
                try {
                    val newTripActivity = state.value.tripId?.let {
                        state.value.tripActivityId?.let { it1 ->
                            TripActivity(
                                id = it1,
                                name = state.value.tripActivityName,
                                startDate = state.value.startDate,
                                endDate = state.value.endDate,
                                pricePerPerson = state.value.pricePerPerson,
                                position = state.value.position,
                                notes = state.value.notes,
                                tripId = it,
                                tripActivityTypeId = state.value.tripActivityTypeId?.toIntOrNull()
                            )
                        }
                    }

                    newTripActivity?.let { tripActivitiesRepository.upsert(it) }
                } catch (e: Exception) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error editing the trip activity, try again later"
                    )
                }
            }
        }

        override fun loadTripActivityTypes() {
            viewModelScope.launch {
                try {
                    val fetchedTripActivityTypes = tripActivitiesTypesRepository.getAll()

                    _state.value = _state.value.copy(
                        tripActivityTypes = fetchedTripActivityTypes
                    )
                } catch (e: Exception) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error loading trip activity types, try again later"
                    )
                }
            }
        }

        override fun loadTripActivity() {
            viewModelScope.launch {
                try {
                    val tripActivity = state.value.tripActivityId?.let {
                        tripActivitiesRepository.getTripActivityById(
                            it
                        )
                    }

                    if (tripActivity != null) {
                        setTripActivityName(tripActivity.name ?: "")
                        setStartDate(tripActivity.startDate ?: "")
                        setEndDate(tripActivity.endDate ?: "")
                        setTripActivityTypeId(tripActivity.tripActivityTypeId.toString())
                        setPricePerPerson(tripActivity.pricePerPerson ?: 0.0)
                        setPosition(tripActivity.position ?: "")
                        setNotes(tripActivity.notes ?: "")
                    }
                } catch (e: Exception) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error loading tripActivity, try again later"
                    )
                }
            }
        }
    }
}