package com.example.travelbuddy.ui.screens.code

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.travelbuddy.data.repositories.UsersRepository
import androidx.lifecycle.viewModelScope
import com.example.travelbuddy.data.repositories.UserSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CodeState(
    val code: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)

interface CodeActions {
    fun setCode(code: String)
    fun savePin(email: String, pin: String, onComplete: () -> Unit)
    fun verifyPin(email: String, pin: String, onResult: (Boolean) -> Unit)
    fun showError(message: String)
    fun clearError()
    fun clearUserSession(onComplete: () -> Unit)
}
class CodeViewModel(
    private val userRepository: UsersRepository,
    private val appPreferences: UserSessionRepository
) : ViewModel() {
    private val _email = mutableStateOf<String?>(null)
    val email: String? get() = _email.value

    init {
        loadEmail()
    }

    private fun loadEmail() {
        viewModelScope.launch {
            appPreferences.userEmail.collect { email ->
                _email.value = email
            }
        }
    }

    private val _state = MutableStateFlow(CodeState())
    val state: StateFlow<CodeState> = _state.asStateFlow()

    val actions = object : CodeActions {
        override fun setCode(code: String) {
            _state.update { it.copy(code = code) }
        }

        override fun savePin(email: String,pin: String, onComplete: () -> Unit) {
            if (pin.length != 6) return

            viewModelScope.launch {
                _state.value = _state.value.copy(isLoading = true)
                try {
                    userRepository.updateUserPin(email, pin)
                    appPreferences.saveHasPin(true)
                    onComplete()
                } catch (e: Exception) {
                    showError("Save failed: ${e.localizedMessage}")
                } finally {
                    _state.value = _state.value.copy(isLoading = false)
                }
            }
        }

        override fun verifyPin(email:String, pin: String, onResult: (Boolean) -> Unit) {
            viewModelScope.launch {
                _state.value = _state.value.copy(isLoading = true)
                try {
                    val isValid = userRepository.verifyPin(email, pin)
                    onResult(isValid)
                } catch (e: Exception) {
                    showError("Verification failed")
                } finally {
                    _state.value = _state.value.copy(isLoading = false)
                }
            }
        }

        override fun showError(message: String) {
            _state.value = _state.value.copy(errorMessage = message)
        }

        override fun clearError() {
            _state.value = _state.value.copy(errorMessage = null)
        }

        override fun clearUserSession(onComplete: () -> Unit) {
            viewModelScope.launch {
                appPreferences.clearAllSessionData()
                onComplete()
            }
        }
    }
}