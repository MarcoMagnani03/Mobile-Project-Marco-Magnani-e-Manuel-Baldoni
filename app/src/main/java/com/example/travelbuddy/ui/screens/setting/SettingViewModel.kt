package com.example.travelbuddy.ui.screens.setting


import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelbuddy.data.repositories.SettingPreferenceRepository
import com.example.travelbuddy.data.repositories.UserSessionRepository
import com.example.travelbuddy.data.repositories.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingState(
    val selectedTheme: ThemeOption = ThemeOption.SYSTEM,
    val errorMessage: String? = null,
    val successMessage: String? = null,
)

interface SettingActions {
    fun onThemeSelected(option: ThemeOption)
    fun onChangePassword(old: String, new: String)
    fun onSetPin(old: String, new: String)
    fun clearMessages()
}

class SettingViewModel(
    private val settingPreferenceRepository: SettingPreferenceRepository,
    private val userRepository: UsersRepository,
    private val userSessionRepository: UserSessionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingState())
    val state: StateFlow<SettingState> = _state


    init {
        viewModelScope.launch {
            settingPreferenceRepository.getTheme().collect { theme ->
                _state.update { it.copy(selectedTheme = theme) }
            }
        }
    }

    val actions = object : SettingActions {
        override fun onThemeSelected(option: ThemeOption) {
            viewModelScope.launch {
                settingPreferenceRepository.setTheme(option)
            }
        }


        override fun onChangePassword(old: String, new: String) {
            viewModelScope.launch {
                val userEmail = userSessionRepository.userEmail.first()
                if (old.isBlank() || new.isBlank()) {
                    _state.update { it.copy(errorMessage = "Password fields cannot be empty") }
                    return@launch
                }

                if(userEmail.isNullOrBlank()){
                    _state.update { it.copy(errorMessage = "Session is terminated sign out") }
                    return@launch
                }
                else{
                    val result = userRepository.changePassword(userEmail,old ,new)
                    _state.update {
                        if (result.isSuccess) {
                            it.copy(successMessage = "Password changed successfully", errorMessage = null)
                        } else {
                            it.copy(errorMessage = result.exceptionOrNull()?.message ?: "Error changing password")
                        }
                    }
                }
            }
        }

        override fun onSetPin(old: String, new: String) {
            viewModelScope.launch {
                val userEmail = userSessionRepository.userEmail.first()
                if (new.length != 6) {
                    _state.update { it.copy(errorMessage = "PIN must be of 6 digits") }
                    return@launch
                }
                if(userEmail.isNullOrBlank()){
                    _state.update { it.copy(errorMessage = "Session is terminated sign out") }
                    return@launch
                }
                else {
                    val result = userRepository.setPin(userEmail, old, new)
                    _state.update {
                        if (result.isSuccess) {
                            it.copy(
                                successMessage = "PIN updated successfully",
                                errorMessage = null
                            )
                        } else {
                            it.copy(
                                errorMessage = result.exceptionOrNull()?.message
                                    ?: "Error setting PIN"
                            )
                        }
                    }
                }
            }
        }

        override fun clearMessages() {
            _state.update { it.copy(errorMessage = null, successMessage = null) }
        }

    }
}


