package com.example.travelbuddy.data.repositories

import com.example.travelbuddy.data.database.ActivityType
import com.example.travelbuddy.data.database.ActivityTypesDAO

class ActivityTypesRepository (
    private val dao: ActivityTypesDAO
) {
    suspend fun upsert(activityType: ActivityType) = dao.upsert(activityType)
    suspend fun delete(activityType: ActivityType) = dao.delete(activityType)
}