package com.example.travelbuddy.ui.screens.friend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelbuddy.data.database.FriendRequest
import com.example.travelbuddy.data.database.User
import com.example.travelbuddy.data.repositories.FriendRequestsRepository
import com.example.travelbuddy.data.repositories.FriendshipsRepository
import com.example.travelbuddy.data.repositories.UserSessionRepository
import com.example.travelbuddy.data.repositories.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

interface FriendActions {
    fun loadFriends()
    fun searchFriends(query: String)
    fun acceptFriendRequest(friend: FriendItemData)
    fun rejectFriendRequest(friend: FriendItemData)
    fun sendFriendRequest(friend: FriendItemData)
}

data class FriendRequestWithUser(
    val request: FriendRequest,
    val sender: User
)

class FriendViewModel(
    private val usersRepository: UsersRepository,
    private val friendshipsRepository: FriendshipsRepository,
    private val friendRequestsRepository: FriendRequestsRepository,
    private val userSessionRepository: UserSessionRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(FriendState())
    val state = _state.asStateFlow()

    private var originalFriends: List<User> = emptyList()

    val actions = object : FriendActions {
        override fun loadFriends() {
            viewModelScope.launch {
                try {
                    userSessionRepository.userEmail.collect { email ->
                        if (email.isNullOrBlank()) return@collect

                        val friendships = friendshipsRepository.getFriendshipsByUser(email)
                        val friends = friendships.mapNotNull { friendship ->
                            val friendEmail = if (friendship.emailFirstUser == email)
                                friendship.emailSecondUser
                            else
                                friendship.emailFirstUser

                            usersRepository.getUserByEmail(friendEmail)
                        }
                        originalFriends = friends

                        val requests = friendRequestsRepository.getFriendRequests(email)

                        val friendRequestsWithUsers: List<FriendRequestWithUser> = requests.mapNotNull { request ->
                            try {
                                val sender = usersRepository.getUserByEmail(request.senderEmail)
                                sender?.let {
                                    FriendRequestWithUser(request, it)
                                }
                            } catch (e: Exception) {
                                null
                            }
                        }

                        val sentRequests = friendRequestsRepository.getSentFriendRequests(email)
                        val sentRequestsEmails = sentRequests.map { it.receiverEmail }

                        val suggestedFriends = usersRepository.getRandomUsersExcluding(
                            excludeEmails = listOf(email) + friends.map { it.email } + sentRequestsEmails +friendRequestsWithUsers.map{it.sender.email},
                            limit = 3
                        )

                        _state.value = _state.value.copy(
                            friends = friends,
                            friendRequests = friendRequestsWithUsers,
                            suggestedFriends = suggestedFriends,
                            sentFriendRequests = sentRequestsEmails,
                            error = null
                        )
                    }
                } catch (e: Exception) {
                    _state.value = _state.value.copy(error = "Error loading friends: ${e.message}")
                }
            }
        }

        override fun searchFriends(query: String) {
            val filteredFriends = if (query.isBlank()) {
                originalFriends
            } else {
                originalFriends.filter { friend ->
                    "${friend.firstname} ${friend.lastname}".contains(query, ignoreCase = true)
                }
            }
            _state.value = _state.value.copy(friends = filteredFriends)
        }

        override fun acceptFriendRequest(friend: FriendItemData) {
            viewModelScope.launch {
                try {
                    userSessionRepository.userEmail.collect { currentUserEmail ->
                        if (currentUserEmail.isNullOrBlank()) return@collect

                        friendRequestsRepository.acceptFriendRequest(friend.email, currentUserEmail)

                        loadFriends()
                    }
                } catch (e: Exception) {
                    _state.value = _state.value.copy(error = "Error accepting friend request: ${e.message}")
                }
            }
        }

        override fun rejectFriendRequest(friend: FriendItemData) {
            viewModelScope.launch {
                try {
                    userSessionRepository.userEmail.collect { currentUserEmail ->
                        if (currentUserEmail.isNullOrBlank()) return@collect

                        friendRequestsRepository.refuseFriendRequest(friend.email, currentUserEmail)

                        loadFriends()
                    }
                } catch (e: Exception) {
                    _state.value = _state.value.copy(error = "Error rejecting friend request: ${e.message}")
                }
            }
        }

        override fun sendFriendRequest(friend: FriendItemData) {
            viewModelScope.launch {
                try {
                    userSessionRepository.userEmail.collect { currentUserEmail ->
                        if (currentUserEmail.isNullOrBlank()) return@collect

                        friendRequestsRepository.sendFriendRequest(currentUserEmail, friend.email)

                        loadFriends()
                    }
                } catch (e: Exception) {
                    _state.value = _state.value.copy(error = "Error sending friend request: ${e.message}")
                }
            }
        }
    }
}

data class FriendState(
    val friends: List<User> = emptyList(),
    val friendRequests: List<FriendRequestWithUser> = emptyList(),
    val suggestedFriends: List<User> = emptyList(),
    val sentFriendRequests: List<String> = emptyList(),
    val error: String? = null
)