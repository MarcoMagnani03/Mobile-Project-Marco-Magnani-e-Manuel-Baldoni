package com.example.travelbuddy.ui.screens.setting


import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelbuddy.data.repositories.SettingPreferenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingState(
    val selectedTheme: ThemeOption = ThemeOption.SYSTEM
)

interface SettingActions {
    fun onThemeSelected(option: ThemeOption)
}



class SettingViewModel(
    private val settingPreferenceRepository: SettingPreferenceRepository
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
    }
}


