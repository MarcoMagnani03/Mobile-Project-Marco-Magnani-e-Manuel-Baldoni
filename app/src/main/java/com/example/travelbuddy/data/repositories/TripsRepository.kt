package com.example.travelbuddy.data.repositories

import com.example.travelbuddy.data.database.Trip
import com.example.travelbuddy.data.database.TripsDAO

class TripsRepository(
    private val dao: TripsDAO
) {
    suspend fun upsert(trip: Trip): Long = dao.upsert(trip)
    suspend fun delete(trip: Trip) = dao.delete(trip)
    suspend fun getTripById(tripId: Long) = dao.getTripById(tripId)
}