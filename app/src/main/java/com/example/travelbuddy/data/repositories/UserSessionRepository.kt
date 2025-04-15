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
    }

    val mail = dataStore.data.map { it[USER_EMAIL] ?: "" }

    suspend fun saveUserEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[USER_EMAIL] = email
        }
    }

    val userEmail: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[USER_EMAIL]
        }

    suspend fun clearUserEmail() {
        dataStore.edit { preferences ->
            preferences.remove(USER_EMAIL)
        }
    }
}