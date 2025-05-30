package com.example.travelbuddy.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelbuddy.data.repositories.UserSessionRepository
import com.example.travelbuddy.data.repositories.UsersRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow


data class LoginState(
    val email: String = "",
    val password: String = "",
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val navigateToCode: Boolean = false
) {
    val canSubmit: Boolean
        get() = email.isNotBlank() && password.isNotBlank()
}

interface LoginActions {
    fun setEmail(email: String)
    fun setPassword(password: String)
    fun resetNavigation()
    fun loginWithEmail()
    fun handleGoogleSignIn(account: GoogleSignInAccount)
}

class LoginViewModel(
    private val appPreferences: UserSessionRepository,
    private val userRepository: UsersRepository
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()


    val actions = object : LoginActions {
        override fun setEmail(email: String) =
            _state.update { it.copy(email = email) }

        override fun setPassword(password: String) =
            _state.update { it.copy(password = password) }

        override fun resetNavigation() {
            _state.value = _state.value.copy(navigateToCode = false)
        }

        override fun loginWithEmail() {
            viewModelScope.launch {
                _state.value = _state.value.copy(isLoading = true)
                try {
                    val success = userRepository.signInWithEmailAndPassword(
                        _state.value.email,
                        _state.value.password
                    )

                    val message = if (!success) "Invalid credentials" else null;
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isSuccess = success,
                        errorMessage = message
                    )

                    if(success){
                        onLoginSuccess(_state.value.email)
                    }
                    else{
                        _toastEvent.emit(message.toString())
                    }
                } catch (e: Exception) {
                    val message = e.message ?: "Login failed";
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = message
                    )
                    _toastEvent.emit(message)
                }
            }
        }

        override fun handleGoogleSignIn(account: GoogleSignInAccount) {
            viewModelScope.launch {
                val email = userRepository.handleGoogleSignIn(account)
                if (email != null) {
                    onLoginSuccess(email)
                } else {
                    //TODO FARE ERRORE NON TOAST
                }
            }
        }
    }


    fun onLoginSuccess(email: String) {
        viewModelScope.launch {
            val hasPin = userRepository.hasPin(email)
            appPreferences.saveHasPin(hasPin)

            appPreferences.saveUserEmail(email)

            _state.value = _state.value.copy(
                navigateToCode = true,
                email = email
            )
        }
    }
}