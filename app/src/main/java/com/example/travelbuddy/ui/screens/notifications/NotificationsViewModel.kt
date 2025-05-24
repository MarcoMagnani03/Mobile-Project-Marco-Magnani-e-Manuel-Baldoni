package com.example.travelbuddy.ui.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelbuddy.data.database.GroupInvitation
import com.example.travelbuddy.data.database.InvitationWithTripName
import com.example.travelbuddy.data.database.Notification
import com.example.travelbuddy.data.database.NotificationType
import com.example.travelbuddy.data.repositories.GroupInvitesRepository
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
    val errorMessage: String? = null,
    val groupInvites: List<InvitationWithTripName> = emptyList(),
)

interface NotificationsActions {
    fun loadNotifications()
    fun deleteNotification(notificationId: Long)
    fun markNotificationAsRead(notificationId: Long)
    fun acceptGroupInvite(receiverEmail: String, tripId: Long)
    fun declineGroupInvite(receiverEmail: String, tripId: Long)
    fun loadGroupInvites()
}

class NotificationsViewModel(
    private val notificationRepository: NotificationsRepository,
    private val notificationTypeRepository: NotificationsTypeRepository,
    private val sessionRepository: UserSessionRepository,
    private val groupInviteRepository: GroupInvitesRepository,
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
                                errorMessage = "User session not found",
                                isLoading = false
                            )
                        }
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            errorMessage = e.message ?: "Error during notifications loading",
                            isLoading = false
                        )
                    }
                }
            }
        }

        override fun loadGroupInvites() {
            viewModelScope.launch {
                try {
                    val email = sessionRepository.userEmail.first()
                    if (email != null) {
                        val invites = groupInviteRepository.getPendingInvitesForUser(email)
                        _state.update { it.copy(groupInvites = invites) }
                    }
                } catch (e: Exception) {
                    _state.update { it.copy(errorMessage = e.message ?: "Error loading group invites") }
                }
            }
        }

        override fun acceptGroupInvite(receiverEmail: String, tripId: Long)
        {
            viewModelScope.launch {
                try {
                    groupInviteRepository.acceptGroupInvite(receiverEmail = receiverEmail, tripId = tripId)
                    loadGroupInvites()
                } catch (e: Exception) {
                    _state.update { it.copy(errorMessage = e.message ?: "Error accepting invite") }
                }
            }
        }

        override fun declineGroupInvite(receiverEmail: String, tripId: Long) {
            viewModelScope.launch {
                try {
                    groupInviteRepository.declineGroupInvite(receiverEmail = receiverEmail, tripId = tripId)
                    loadGroupInvites()
                } catch (e: Exception) {
                    _state.update { it.copy(errorMessage = e.message ?: "Error declining invite") }
                }
            }
        }

        override fun deleteNotification(notificationId: Long) {
            viewModelScope.launch {
                try {
                    notificationRepository.deleteNotification(notificationId)
                    loadNotifications()
                } catch (e: Exception) {
                    _state.update { it.copy(errorMessage = e.message) }
                }
            }
        }

        override fun markNotificationAsRead(notificationId: Long){
            viewModelScope.launch {
                try {
                    notificationRepository.markNotificationAsRead(notificationId)
                    loadNotifications()
                } catch (e: Exception) {
                    _state.update { it.copy(errorMessage = e.message) }
                }
            }
        }
    }
}