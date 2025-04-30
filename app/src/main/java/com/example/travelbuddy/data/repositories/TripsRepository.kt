package com.example.travelbuddy.data.repositories

import androidx.room.Query
import androidx.room.Transaction
import com.example.travelbuddy.data.database.Trip
import com.example.travelbuddy.data.database.TripWithActivitiesAndExpensesAndPhotosAndUsers
import com.example.travelbuddy.data.database.TripWithTripActivities
import com.example.travelbuddy.data.database.TripWithTripActivitiesAndExpenses
import com.example.travelbuddy.data.database.TripsDAO

class TripsRepository(
    private val dao: TripsDAO
) {
    suspend fun upsert(trip: Trip): Long = dao.upsert(trip)
    suspend fun delete(trip: Trip) = dao.delete(trip)
    suspend fun getTripById(tripId: Long) = dao.getTripById(tripId)
    suspend fun getAllTrips(): List<Trip> = dao.getAllTrips()
    suspend fun getTripWithActivitiesById(tripId: Long): TripWithTripActivities? = dao.getTripWithActivitiesById(tripId)
    suspend fun getTripWithActivitiesAndExpensesById(tripId: Long): TripWithTripActivitiesAndExpenses? = dao.getTripWithActivitiesAndExpensesById(tripId)
    suspend fun getTripWithAllRelationsById(tripId: Long): TripWithActivitiesAndExpensesAndPhotosAndUsers? = dao.getTripWithAllRelationsById(tripId)
    suspend fun getAllTripsWithAllRelations(): List<TripWithActivitiesAndExpensesAndPhotosAndUsers> = dao.getAllTripsWithAllRelations()
    suspend fun getTripsForUser(userEmail: String): List<Trip> = dao.getTripsForUser(userEmail)
    suspend fun getTripsWithAllRelationsForUser(userEmail: String): List<TripWithActivitiesAndExpensesAndPhotosAndUsers> = dao.getTripsWithAllRelationsForUser(userEmail)
}