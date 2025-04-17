package com.example.travelbuddy.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserSessionRepository(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val HAS_PIN = booleanPreferencesKey("has_pin")
    }

    val userEmail: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_EMAIL]
    }

    val hasPinFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[HAS_PIN] ?: false
    }

    suspend fun saveUserEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[USER_EMAIL] = email
        }
    }

    suspend fun clearUserEmail() {
        dataStore.edit { preferences ->
            preferences.remove(USER_EMAIL)
        }
    }

    suspend fun saveHasPin(hasPin: Boolean) {
        dataStore.edit { preferences ->
            preferences[HAS_PIN] = hasPin
        }
    }

    suspend fun clearAllSessionData() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
