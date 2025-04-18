package com.example.travelbuddy.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelbuddy.data.database.UserWithTrips
import com.example.travelbuddy.data.repositories.UserSessionRepository
import com.example.travelbuddy.data.repositories.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeState(
    val userWithTrips: UserWithTrips? = null,
    val error: String? = null
)

interface HomeActions {
    fun loadUserWithTrips()
}

class HomeViewModel(
    private val usersRepository: UsersRepository,
    private val userSessionRepository: UserSessionRepository
): ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    val actions = object : HomeActions {
        override fun loadUserWithTrips() {

            viewModelScope.launch {
                try {
                    userSessionRepository.userEmail.collect { email ->
                        if(email.isNullOrBlank()){
                            return@collect
                        }

                        val userWithTrips = usersRepository.getUserWithTrips(email)

                        _state.value = _state.value.copy(userWithTrips = userWithTrips)
                    }
                } catch (e: Exception) {
                    _state.value = _state.value.copy(error = "Error loading the trip")
                }
            }
        }
    }
}
