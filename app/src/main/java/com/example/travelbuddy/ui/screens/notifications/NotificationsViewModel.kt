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