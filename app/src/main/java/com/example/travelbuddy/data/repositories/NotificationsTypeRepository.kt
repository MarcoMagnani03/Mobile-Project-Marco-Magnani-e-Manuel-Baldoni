package com.example.travelbuddy.data.repositories

import com.example.travelbuddy.data.database.NotificationType
import com.example.travelbuddy.data.database.NotificationsTypesDAO

class NotificationsTypeRepository (
    private val dao: NotificationsTypesDAO
) {
    suspend fun upsert(notificationType: NotificationType) = dao.upsert(notificationType)
    suspend fun delete(notificationType: NotificationType) = dao.delete(notificationType)
}