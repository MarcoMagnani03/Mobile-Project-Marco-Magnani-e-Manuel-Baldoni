package com.example.travelbuddy.data.repositories

import com.example.travelbuddy.data.database.Notification
import com.example.travelbuddy.data.database.NotificationsDAO

class NotificationsRepository (
    private val dao: NotificationsDAO
) {
    suspend fun upsert(notification: Notification) = dao.upsert(notification)
    suspend fun delete(notification: Notification) = dao.delete(notification)
}