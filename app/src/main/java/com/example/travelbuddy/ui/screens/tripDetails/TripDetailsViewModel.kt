package com.example.travelbuddy.ui.screens.tripDetails

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelbuddy.data.database.FriendRequest
import com.example.travelbuddy.data.database.Friendship
import com.example.travelbuddy.data.database.Group
import com.example.travelbuddy.data.database.Trip
import com.example.travelbuddy.data.database.TripActivityType
import com.example.travelbuddy.data.database.TripWithActivitiesAndExpensesAndPhotosAndUsers
import com.example.travelbuddy.data.database.User
import com.example.travelbuddy.data.repositories.FriendshipsRepository
import com.example.travelbuddy.data.repositories.GroupInvitesRepository
import com.example.travelbuddy.data.repositories.GroupsRepository
import com.example.travelbuddy.data.repositories.NotificationsRepository
import com.example.travelbuddy.data.repositories.TripActivitiesTypesRepository
import com.example.travelbuddy.data.repositories.TripsRepository
import com.example.travelbuddy.data.repositories.UserSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class TripDetailsState(
    val trip: TripWithActivitiesAndExpensesAndPhotosAndUsers? = null,
    val tripActivityTypes: List<TripActivityType> = emptyList(),
    val error: String? = null,
    val friendList: List<User> = emptyList()
)

interface TripDetailsActions {
    fun loadTrip(tripId: Long)
    fun loadTripActivityTypes()
    fun deleteTrip()
    fun removeUserFromGroup()
    fun sendInvitations(selectedFriends: List<User>)
}

class TripDetailsViewModel(
    private val tripsRepository: TripsRepository,
    private val tripActivitiesTypesRepository: TripActivitiesTypesRepository,
    private val groupsRepository: GroupsRepository,
    private val userSessionRepository: UserSessionRepository,
    private val friendshipsRepository: FriendshipsRepository,
    private val groupInvitesRepository: GroupInvitesRepository,
    private val notificationsRepository: NotificationsRepository
): ViewModel() {
    private val _state = MutableStateFlow(TripDetailsState())
    val state = _state.asStateFlow()

    val actions = object : TripDetailsActions {
        override fun loadTrip(tripId: Long) {
            viewModelScope.launch {
                try {
                    val userEmail = userSessionRepository.userEmail.first()
                    val trip = tripsRepository.getTripWithAllRelationsById(tripId)
                    val friendList = friendshipsRepository.getFriendshipsUsersByUser(userEmail.toString())
                    _state.value = _state.value.copy(trip = trip)
                    _state.value = _state.value.copy(friendList = friendList)
                } catch (e: Exception) {
                    _state.value = _state.value.copy(error = "Error loading the trip")
                }
            }
        }

        override fun loadTripActivityTypes() {
            viewModelScope.launch {
                try {
                    val tripActivityTypes = tripActivitiesTypesRepository.getAll()
                    _state.value = _state.value.copy(tripActivityTypes = tripActivityTypes)
                } catch (e: Exception) {
                    _state.value = _state.value.copy(error = "Error loading the activity types")
                }
            }
        }

        override fun deleteTrip() {
            viewModelScope.launch {
                try {
                    state.value.trip?.let { tripsRepository.delete(it.trip) }

                    userSessionRepository.userEmail.collect { currentUserEmail ->
                        if (currentUserEmail.isNullOrBlank()) return@collect

                        val groupEntries = groupsRepository.getGroupMembersByTripId(
                            state.value.trip?.trip?.id ?: 0)
                        groupEntries.toSet().forEach { group ->
                            notificationsRepository.addInfoNotification(description = "$currentUserEmail deleted a trip", title = "Delete trip", userEmail = group.userEmail)
                        }
                    }
                } catch (e: Exception) {
                    _state.value = _state.value.copy(error = "Error deleting trip")
                }
            }
        }

        override fun removeUserFromGroup() {
            viewModelScope.launch {
                try {
                    val userEmail = userSessionRepository.userEmail.first()
                    val tripId = state.value.trip?.trip?.id

                    if (tripId != null) {
                        val group = Group(
                            userEmail = userEmail.toString(),
                            tripId = tripId
                        )
                        groupsRepository.delete(group)
                    }

                    val groupEntries = groupsRepository.getGroupMembersByTripId(
                        state.value.trip?.trip?.id ?: 0)
                    groupEntries.toSet().forEach { group ->
                        notificationsRepository.addInfoNotification(description = "$userEmail left the group of trip: ${state.value.trip?.trip?.name}", title = "$userEmail left the group", userEmail = group.userEmail)
                    }
                } catch (e: Exception) {
                    _state.value = _state.value.copy(error = "Error deleting trip")
                }
            }
        }

        override fun sendInvitations(selectedFriends: List<User>) {
            viewModelScope.launch {
                try {
                    val userEmail = userSessionRepository.userEmail.first()
                    val tripId = state.value.trip?.trip?.id ?: return@launch

                    selectedFriends.forEach { friend ->
                        groupInvitesRepository.sendGroupInvite(receiverEmail = friend.email, senderEmail = userEmail.toString(), tripId = tripId) // o usa un repository dedicato se esiste
                    }

                    loadTrip(tripId)

                } catch (e: Exception) {
                    _state.value = _state.value.copy(error = "Error sending invitations")
                }
            }
        }
    }
}