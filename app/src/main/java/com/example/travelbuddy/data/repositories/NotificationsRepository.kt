package com.example.travelbuddy.data.repositories

import com.example.travelbuddy.data.database.Notification
import com.example.travelbuddy.data.database.NotificationsDAO

class NotificationsRepository(
    val dao: NotificationsDAO
) {
    suspend fun getNotificationsForUser(userEmail: String): List<Notification> {
        return dao.getNotificationsForUser(userEmail)
    }

    suspend fun addInfoNotification(title: String, description: String, userEmail:String) {
        val notification = Notification(description = description, title = title, notificationTypeId = 1, userEmail = userEmail)
        dao.updateNotification(notification)
    }

    suspend fun addErrorNotification(title: String, description: String, userEmail:String) {
        val notification = Notification(description = description, title = title, notificationTypeId = 0, userEmail = userEmail)
        dao.updateNotification(notification)
    }

    suspend fun markNotificationAsRead(notificationId: Long) {
        dao.markNotificationAsRead(notificationId)
    }


    suspend fun deleteNotification(notificationId: Long) {
        dao.deleteNotification(notificationId)
    }
}