package com.example.travelbuddy.data.repositories

import com.example.travelbuddy.data.database.TripActivitiesDAO
import com.example.travelbuddy.data.database.TripActivity
import com.example.travelbuddy.ui.screens.tripActivities.TripActivitiesFilter

class TripActivitiesRepository (
    private val dao: TripActivitiesDAO
) {
    suspend fun upsert(tripActivity: TripActivity): Long = dao.upsert(tripActivity)
    suspend fun delete(tripActivity: TripActivity) = dao.delete(tripActivity)

    suspend fun getAll(tripId: Long) = dao.getAll(tripId)

    suspend fun getFilteredActivities(tripId: Long, filters: TripActivitiesFilter): List<TripActivity> {
        return dao.getFilteredActivities(
            tripId = tripId,
            name = filters.name,
            startDate = filters.startDate,
            endDate = filters.endDate,
            pricePerPerson = filters.pricePerPerson,
            tripActivityTypeId = filters.tripActivityTypeId
        )
    }
}