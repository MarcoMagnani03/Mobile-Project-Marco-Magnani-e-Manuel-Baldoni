package com.example.travelbuddy.data.repositories

import com.example.travelbuddy.data.database.TripActivityType
import com.example.travelbuddy.data.database.TripActivitiesTypesDAO

class TripActivitiesTypesRepository (
    private val dao: TripActivitiesTypesDAO
) {
    suspend fun upsert(activityType: TripActivityType) = dao.upsert(activityType)
    suspend fun delete(activityType: TripActivityType) = dao.delete(activityType)

    suspend fun getAll() = dao.getAll()
}