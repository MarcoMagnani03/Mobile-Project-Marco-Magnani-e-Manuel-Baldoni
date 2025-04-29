package com.example.travelbuddy.data.repositories

import com.example.travelbuddy.data.database.NotificationType
import com.example.travelbuddy.data.database.NotificationsTypesDAO

class NotificationsTypeRepository(
    val dao:NotificationsTypesDAO
) {
    suspend fun getNotificationTypeById(typeId: Int): NotificationType {
        return dao.getNotificationTypeById(typeId)
    }

    suspend fun getAllNotificationTypes(): List<NotificationType> {
        return dao.getAllNotificationTypes()
    }

    suspend fun addOrUpdateNotificationType(notificationType: NotificationType){
        dao.upsert(notificationType)
    }
}

