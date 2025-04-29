package com.example.travelbuddy.ui.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelbuddy.data.database.Notification
import com.example.travelbuddy.data.database.NotificationType
import com.example.travelbuddy.data.repositories.NotificationsRepository
import com.example.travelbuddy.data.repositories.NotificationsTypeRepository
import com.example.travelbuddy.data.repositories.UserSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Data class to connect Notification with its type
data class NotificationWithType(
    val notification: Notification,
    val type: NotificationType
)

data class NotificationsState(
    val notifications: List<NotificationWithType> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

interface NotificationsActions {
    fun loadNotifications()
    fun markAsRead(notificationId: Long)
    fun deleteNotification(notificationId: Long)
}

class NotificationsViewModel(
    private val notificationRepository: NotificationsRepository,
    private val notificationTypeRepository: NotificationsTypeRepository,
    private val sessionRepository: UserSessionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationsState())
    val state = _state.asStateFlow()

    val actions = object : NotificationsActions {
        override fun loadNotifications() {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }
                try {
                    // Get current user email
                    val email = sessionRepository.userEmail.first()

                    if (email != null) {
                        // Get user notifications
                        val notifications = notificationRepository.getNotificationsForUser(email)

                        // Process notifications to include type information
                        val notificationsWithType = notifications.map { notification ->
                            val type = notificationTypeRepository.getNotificationTypeById(notification.notificationTypeId)
                            NotificationWithType(notification, type)
                        }

                        _state.update {
                            it.copy(
                                notifications = notificationsWithType,
                                isLoading = false
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                errorMessage = "Sessione utente non trovata",
                                isLoading = false
                            )
                        }
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            errorMessage = e.message ?: "Errore nel caricamento delle notifiche",
                            isLoading = false
                        )
                    }
                }
            }
        }

        override fun markAsRead(notificationId: Long) {
            // This would be implemented if you have a read/unread field in your notification model
            // For now, it's just a placeholder for future implementation
            viewModelScope.launch {
                try {
                    // Esempio di implementazione:
                    // 1. Recuperare la notifica dal repository
                    // val notification = notificationRepository.getNotificationById(notificationId)
                    // 2. Aggiornare lo stato di lettura
                    // notification.isRead = true
                    // 3. Salvare la notifica aggiornata
                    // notificationRepository.updateNotification(notification)
                    // 4. Ricaricare le notifiche per aggiornare l'UI
                    // loadNotifications()
                } catch (e: Exception) {
                    _state.update { it.copy(errorMessage = e.message) }
                }
            }
        }

        override fun deleteNotification(notificationId: Long) {
            viewModelScope.launch {
                try {
                    notificationRepository.deleteNotification(notificationId)
                    loadNotifications() // Reload notifications after deletion
                } catch (e: Exception) {
                    _state.update { it.copy(errorMessage = e.message) }
                }
            }
        }
    }
}