package com.example.travelbuddy.data.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.travelbuddy.ui.screens.setting.ThemeOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingPreferenceRepository(
    private val dataStore: DataStore<Preferences>
) {
    private val THEME_KEY = stringPreferencesKey("theme")

    fun getTheme(): Flow<ThemeOption> {
        return dataStore.data.map { prefs ->
            val value = prefs[THEME_KEY] ?: ThemeOption.SYSTEM.name
            ThemeOption.valueOf(value)
        }
    }

    suspend fun setTheme(option: ThemeOption) {
        dataStore.edit { prefs ->
            prefs[THEME_KEY] = option.name
        }
    }
}
