package com.example.travelbuddy.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelbuddy.data.repositories.UserSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val canSubmit: Boolean
        get() = email.isNotBlank() && password.isNotBlank()
}

interface LoginActions {
    fun setEmail(email: String)
    fun setPassword(password: String)
}

class LoginViewModel(
    private val appPreferences: UserSessionRepository
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    val actions = object : LoginActions {
        override fun setEmail(email: String) =
            _state.update { it.copy(email = email) }

        override fun setPassword(password: String) =
            _state.update { it.copy(password = password) }
    }

    //TODO UNA VOLTA AGGIUNTA LA GESTIONE DELLA LOGIN CHIAMARE QUESTO
    // METODO PRIMA DI PASSARE ALLA SCHERMATA CODE
    fun onLoginSuccess(email: String) {
        viewModelScope.launch {
            appPreferences.saveUserEmail(email)
        }
    }
}