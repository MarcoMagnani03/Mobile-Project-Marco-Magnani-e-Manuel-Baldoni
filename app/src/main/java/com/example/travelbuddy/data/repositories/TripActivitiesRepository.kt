package com.example.travelbuddy.data.repositories

import com.example.travelbuddy.data.database.TripActivitiesDAO
import com.example.travelbuddy.data.database.TripActivity

class TripActivitiesRepository (
    private val dao: TripActivitiesDAO
) {
    suspend fun upsert(tripActivity: TripActivity) = dao.upsert(tripActivity)
    suspend fun delete(tripActivity: TripActivity) = dao.delete(tripActivity)
}