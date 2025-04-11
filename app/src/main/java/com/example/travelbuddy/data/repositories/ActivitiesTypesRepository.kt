package com.example.travelbuddy.data.repositories

import com.example.travelbuddy.data.database.ActivityType
import com.example.travelbuddy.data.database.ActivitiesTypesDAO

class ActivitiesTypesRepository (
    private val dao: ActivitiesTypesDAO
) {
    suspend fun upsert(activityType: ActivityType) = dao.upsert(activityType)
    suspend fun delete(activityType: ActivityType) = dao.delete(activityType)
}