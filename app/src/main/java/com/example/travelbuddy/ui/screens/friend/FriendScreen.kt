package com.example.travelbuddy.ui.screens.friend

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelbuddy.ui.composables.ProfileImageSection
import com.example.travelbuddy.ui.composables.TravelBuddyBottomBar
import com.example.travelbuddy.ui.composables.TravelBuddyTopBar
import com.example.travelbuddy.utils.ImageUtils
import com.example.travelbuddy.utils.ImageUtils.toImageBitmapOrNull
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import com.example.travelbuddy.ui.composables.TravelBuddySearchBar
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.runtime.remember
import com.example.travelbuddy.ui.TravelBuddyRoute
import com.example.travelbuddy.ui.composables.TravelBuddyConfirmDialog

@Composable
private fun FriendSection(
    title: String,
    friends: List<FriendItemData>,
    onAcceptRequest: (FriendItemData) -> Unit,
    onRejectRequest: (FriendItemData) -> Unit,
    onSendRequest: (FriendItemData) -> Unit,
    onUnfriend: (FriendItemData) -> Unit,
    navController: NavController,
    searchBar: (@Composable () -> Unit)? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    val friendsToShow = if (expanded) friends else friends.take(5)


    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        searchBar?.invoke()

        Column(modifier = Modifier.fillMaxWidth()) {
            friendsToShow.forEach { friend ->
                FriendItem(
                    name = friend.name,
                    profileImage = friend.profileImage,
                    email = friend.email,
                    isRequest = title == "Friend Requests",
                    hasAlreadySentRequest = friend.hasAlreadySentRequest,
                    onAccept = { onAcceptRequest(friend) },
                    onReject = { onRejectRequest(friend) },
                    onSendRequest = { onSendRequest(friend) },
                    isAlreadyFriend = friend.isAlreadyFriend,
                    onUnfriend = {onUnfriend(friend)},
                    navController = navController
                )
            }

            if (!expanded && friends.size > 5) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = { expanded = true },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(text = "View All")
                    }
                }
            }

        }
    }
}

@Composable
fun FriendScreen(
    state: FriendState,
    actions: FriendActions,
    navController: NavController
) {
    Scaffold(
        topBar = {
            TravelBuddyTopBar(
                navController = navController,
                title = "Friends",
                subtitle = "Connect & Share Your Adventures",
                canNavigateBack = true
            )
        },
        bottomBar = { TravelBuddyBottomBar(navController) }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            var searchQuery by remember { mutableStateOf("") }

            // My Friends
            FriendSection(
                title = "My Friends",
                friends = state.friends.map { user ->
                    FriendItemData(
                        name = "${user.firstname} ${user.lastname}",
                        profileImage = user.profilePicture,
                        email = user.email,
                        isRequest = false,
                        isAlreadyFriend = true
                    )
                },
                onAcceptRequest = { },
                onRejectRequest = { },
                onSendRequest = { },
                onUnfriend={actions.unfriend(it)},
                navController = navController
            )

            // Friend Requests
            FriendSection(
                title = "Friend Requests",
                friends = state.friendRequests.map { reqWithUser ->
                    FriendItemData(
                        name = "${reqWithUser.sender.firstname} ${reqWithUser.sender.lastname}",
                        profileImage = reqWithUser.sender.profilePicture,
                        email = reqWithUser.sender.email,
                        isRequest = true
                    )
                },
                onAcceptRequest = { actions.acceptFriendRequest(it) },
                onRejectRequest = { actions.rejectFriendRequest(it) },
                onSendRequest = { },
                onUnfriend = {},
                navController = navController
            )

            FriendSection(
                title = "Suggested Friends",
                friends = state.suggestedFriends
                    .filter { user ->
                        searchQuery.isBlank() || user.email.contains(searchQuery, ignoreCase = true)
                    }
                    .map { user ->
                        FriendItemData(
                            name = "${user.firstname} ${user.lastname}",
                            profileImage = user.profilePicture,
                            email = user.email,
                            isRequest = false,
                            hasAlreadySentRequest = state.sentFriendRequests.contains(user.email),
                            isAlreadyFriend = state.friends.any { it.email == user.email }
                        )
                    },
                onAcceptRequest = { },
                onRejectRequest = { },
                onSendRequest = { actions.sendFriendRequest(it) },
                onUnfriend = {},
                navController = navController,
                searchBar ={
                    TravelBuddySearchBar(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = "Search by email"
                    )
                }
            )

        }
    }
}

data class FriendItemData(
    val name: String,
    val profileImage: ByteArray?,
    val email: String,
    val isRequest: Boolean,
    val hasAlreadySentRequest: Boolean = false,
    val isAlreadyFriend:Boolean? = false
)

@Composable
fun FriendItem(
    name: String,
    profileImage: ByteArray?,
    email: String,
    isRequest: Boolean,
    hasAlreadySentRequest: Boolean = false,
    isAlreadyFriend: Boolean? = false,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onSendRequest: () -> Unit,
    onUnfriend: () -> Unit,
    navController: NavController
) {
    // Stato per le dialog di conferma
    var showUnfriendDialog by remember { mutableStateOf(false) }
    var showAcceptDialog by remember { mutableStateOf(false) }
    var showRejectDialog by remember { mutableStateOf(false) }
    var showSendRequestDialog by remember { mutableStateOf(false) }

    if (showUnfriendDialog) {
        TravelBuddyConfirmDialog(
            title = "Remove friend",
            message = "Are you sure you want to remove $name from your friends?",
            onConfirm = {
                showUnfriendDialog = false
                onUnfriend()
            },
            onDismiss = { showUnfriendDialog = false }
        )
    }

    if (showAcceptDialog) {
        TravelBuddyConfirmDialog(
            title = "Accept friend request",
            message = "Accept friend request from $name?",
            onConfirm = {
                showAcceptDialog = false
                onAccept()
            },
            onDismiss = { showAcceptDialog = false }
        )
    }

    if (showRejectDialog) {
        TravelBuddyConfirmDialog(
            title = "Reject friend request",
            message = "Reject friend request from $name?",
            onConfirm = {
                showRejectDialog = false
                onReject()
            },
            onDismiss = { showRejectDialog = false }
        )
    }

    if (showSendRequestDialog) {
        TravelBuddyConfirmDialog(
            title = "Send friend request",
            message = "Send friend request to $name?",
            onConfirm = {
                showSendRequestDialog = false
                onSendRequest()
            },
            onDismiss = { showSendRequestDialog = false }
        )
    }


    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            )
            .padding(8.dp)
            .clickable {
                navController.navigate(TravelBuddyRoute.ViewProfile(userEmail = email))
            }
    ) {
        Box(modifier = Modifier.size(80.dp)) {
            ProfileImageSection(
                profileImageBitmap = profileImage?.let { ImageUtils.byteArrayToOrientedBitmap(it).toImageBitmapOrNull() },
                isClickable = false,
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = email,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (!isRequest) {
            if (isAlreadyFriend == true) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showUnfriendDialog = true },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PersonRemove,
                            contentDescription = "Unfriend",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            } else {
                if (!hasAlreadySentRequest) {
                    IconButton(onClick = { showSendRequestDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Send friend request"
                        )
                    }
                } else {
                    Text(
                        text = "Request sent",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        } else {
            Row {
                IconButton(
                    onClick = { showAcceptDialog = true },
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Accept",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = { showRejectDialog = true }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Reject",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

