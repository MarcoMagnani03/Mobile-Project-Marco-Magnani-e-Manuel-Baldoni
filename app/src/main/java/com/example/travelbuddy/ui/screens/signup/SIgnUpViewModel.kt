package com.example.travelbuddy.ui.screens.signup

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SignUpState(
    val email: String = "",
    val password: String = ""
) {
    val canSubmit get() = email.isNotBlank() && password.isNotBlank()
}

interface SignUpActions {
    fun setEmail(email: String)
    fun setPassword(password: String)
}

class SignUpViewModel : ViewModel() {
    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    val actions = object : SignUpActions {
        override fun setEmail(email: String) =
            _state.update { it.copy(email = email) }

        override fun setPassword(password: String) =
            _state.update { it.copy(password = password) }
    }
}