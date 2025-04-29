package com.example.travelbuddy.data.repositories

import com.example.travelbuddy.data.database.Notification
import com.example.travelbuddy.data.database.NotificationsDAO

class NotificationsRepository(
    val dao: NotificationsDAO
) {
    suspend fun getNotificationsForUser(userEmail: String): List<Notification> {
        return dao.getNotificationsForUser(userEmail)
    }

    suspend fun addOrUpdateNotification(notification: Notification) {
        dao.updateNotification(notification)
    }


    suspend fun deleteNotification(notificationId: Long) {
        dao.deleteNotification(notificationId)
    }
}