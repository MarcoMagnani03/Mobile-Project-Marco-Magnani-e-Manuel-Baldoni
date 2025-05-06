package com.example.travelbuddy.ui.screens.profile


import androidx.lifecycle.ViewModel
import com.example.travelbuddy.data.repositories.FriendRequestsRepository
import com.example.travelbuddy.data.repositories.FriendshipsRepository
import com.example.travelbuddy.data.repositories.UsersRepository
import androidx.lifecycle.viewModelScope
import com.example.travelbuddy.data.database.Notification
import com.example.travelbuddy.data.repositories.NotificationsRepository
import com.example.travelbuddy.data.repositories.UserSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ViewProfileState(
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val city: String = "",
    val bio: String = "",
    val profileBitmap: ByteArray? = null,
    val isAlreadyFriend: Boolean = false,
    val hasSentRequest: Boolean = false,
    val hasReceivedRequest: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)


// Actions interface
interface ViewProfileActions {
    fun sendFriendRequest(email: String)
    fun acceptFriendRequest(email: String)
    fun rejectFriendRequest(email: String)
    fun unfriend(email: String)
    fun loadUserData(email:String)
}

class ViewProfileViewModel(
    private val userRepository: UsersRepository,
    private val friendsRepository: FriendshipsRepository,
    private val friendRequestRepository: FriendRequestsRepository,
    private val userSessionRepository: UserSessionRepository,
    private val notificationRepository:NotificationsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ViewProfileState())
    val state: StateFlow<ViewProfileState> = _state

    private suspend fun getCurrentUserEmailOrThrow(): String {
        return userSessionRepository.userEmail.first()
            ?: throw IllegalStateException("User email not available")
    }

    val actions = object : ViewProfileActions {

        override fun sendFriendRequest(email: String) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, error = null) }
                try {
                    val currentUserEmail = getCurrentUserEmailOrThrow()
                    friendRequestRepository.sendFriendRequest(currentUserEmail, email)

                    notificationRepository.addInfoNotification(description = "$currentUserEmail sent you a friend request", title = "Received a friend request", userEmail = email)
                    loadUserData(email)
                    _state.update {
                        it.copy(
                            hasSentRequest = true,
                            isLoading = false
                        )
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to send friend request: ${e.localizedMessage}"
                        )
                    }
                }
            }
        }

        override fun acceptFriendRequest(email: String) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, error = null) }
                try {
                    val currentUserEmail = getCurrentUserEmailOrThrow()
                    friendRequestRepository.acceptFriendRequest(email,currentUserEmail )

                    notificationRepository.addInfoNotification(description = "$currentUserEmail accepted your friend request", title = "Friend request status changed", userEmail = email)
                    loadUserData(email)
                    _state.update {
                        it.copy(
                            isAlreadyFriend = true,
                            hasReceivedRequest = false,
                            isLoading = false
                        )
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to accept friend request: ${e.localizedMessage}"
                        )
                    }
                }
            }
        }

        override fun rejectFriendRequest(email: String) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, error = null) }
                try {
                    val currentUserEmail = getCurrentUserEmailOrThrow()
                    friendRequestRepository.refuseFriendRequest(email,currentUserEmail)
                    notificationRepository.addInfoNotification(description = "$currentUserEmail rejected your friend request", title = "Friend request status changed", userEmail = email)
                    loadUserData(email)
                    _state.update {
                        it.copy(
                            hasReceivedRequest = false,
                            isLoading = false
                        )
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to reject friend request: ${e.localizedMessage}"
                        )
                    }
                }
            }
        }

        override fun unfriend(email: String){
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, error = null) }
                try {
                    val currentUserEmail = getCurrentUserEmailOrThrow()
                    friendsRepository.deleteFriendshipBetween(currentUserEmail, email)
                    loadUserData(email)
                    _state.update {
                        it.copy(
                            isAlreadyFriend = false,
                            isLoading = false
                        )
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to remove friend: ${e.localizedMessage}"
                        )
                    }
                }
            }
        }

        override fun loadUserData(email: String) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, error = null) }
                try {
                    val user = userRepository.getUserByEmail(email)
                        ?: throw Exception("User not found")
                    val currentUserEmail = getCurrentUserEmailOrThrow()
                    val friendshipStatus = friendRequestRepository.getFriendshipStatus(currentUserEmail,email)

                    _state.update {
                        it.copy(
                            name = "${user.firstname} ${user.lastname}",
                            email = user.email,
                            phoneNumber = user.phoneNumber ?: "",
                            city = user.location ?: "",
                            bio = user.bio ?: "",
                            profileBitmap = user.profilePicture,
                            isAlreadyFriend = friendshipStatus.isFriend,
                            hasSentRequest = friendshipStatus.sentRequest,
                            hasReceivedRequest = friendshipStatus.receivedRequest,
                            isLoading = false
                        )
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to load user data: ${e.localizedMessage}"
                        )
                    }
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
