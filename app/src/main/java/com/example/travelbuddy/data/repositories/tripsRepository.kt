package com.example.travelbuddy.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.travelbuddy.data.database.Trip
import com.example.travelbuddy.data.database.TripsDAO
import kotlinx.coroutines.flow.Flow

class TripsRepository(
    private val dao: TripsDAO
) {
    suspend fun upsert(trip: Trip) = dao.upsert(trip)
    suspend fun delete(trip: Trip) = dao.delete(trip)
}