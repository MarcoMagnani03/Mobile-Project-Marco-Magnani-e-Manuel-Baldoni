package com.example.travelbuddy.ui.screens.profile

import com.example.travelbuddy.ui.composables.ProfileDetailItem
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.travelbuddy.ui.composables.*
import com.example.travelbuddy.utils.ImageUtils
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.example.travelbuddy.utils.ImageUtils.toImageBitmapOrNull


@Composable
fun ViewProfileScreen(
    email: String,
    state: ViewProfileState,
    actions: ViewProfileActions,
    navController: NavController
) {
    val scrollState = rememberScrollState()

    // Dialog states
    var showSendRequestDialog by remember { mutableStateOf(false) }
    var showAcceptDialog by remember { mutableStateOf(false) }
    var showRejectDialog by remember { mutableStateOf(false) }
    var showUnfriendDialog by remember { mutableStateOf(false) }

    // Dialogs
    if (showSendRequestDialog) {
        TravelBuddyConfirmDialog(
            title = "Send friend request",
            message = "Send friend request to ${state.name}?",
            onConfirm = {
                showSendRequestDialog = false
                actions.sendFriendRequest(email)
            },
            onDismiss = { showSendRequestDialog = false }
        )
    }

    if (showAcceptDialog) {
        TravelBuddyConfirmDialog(
            title = "Accept friend request",
            message = "Accept friend request from ${state.name}?",
            onConfirm = {
                showAcceptDialog = false
                actions.acceptFriendRequest(email)
            },
            onDismiss = { showAcceptDialog = false }
        )
    }

    if (showRejectDialog) {
        TravelBuddyConfirmDialog(
            title = "Reject friend request",
            message = "Reject friend request from ${state.name}?",
            onConfirm = {
                showRejectDialog = false
                actions.rejectFriendRequest(email)
            },
            onDismiss = { showRejectDialog = false }
        )
    }

    if (showUnfriendDialog) {
        TravelBuddyConfirmDialog(
            title = "Remove friend",
            message = "Are you sure you want to remove ${state.name} from your friends?",
            onConfirm = {
                showUnfriendDialog = false
                actions.unfriend(email)
                navController.popBackStack()
            },
            onDismiss = { showUnfriendDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TravelBuddyTopBar(
                navController = navController,
                title = "Friend Profile",
                subtitle = state.name,
                canNavigateBack = true
            )
        },
        bottomBar = { TravelBuddyBottomBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!state.error.isNullOrEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = state.error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            ProfileImageSection(
                profileImageBitmap = state.profileBitmap
                    ?.let { ImageUtils.byteArrayToOrientedBitmap(it).toImageBitmapOrNull() },
                isClickable = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = state.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = state.email,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Friendship action button based on relationship status
            when {
                state.isAlreadyFriend -> {
                    TravelBuddyButton(
                        label = "Remove Friend",
                        onClick = { showUnfriendDialog = true },
                        style = ButtonStyle.ERROR,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.PersonRemove,
                                contentDescription = "Remove friend",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
                state.hasReceivedRequest -> {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                TravelBuddyButton(
                                    label = "Accept",
                                    onClick = { showAcceptDialog = true },
                                    style = ButtonStyle.PRIMARY,
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = "Accept",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(modifier = Modifier.weight(1f)) {
                                TravelBuddyButton(
                                    label = "Decline",
                                    onClick = { showRejectDialog = true },
                                    style = ButtonStyle.ERROR,
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = "Decline",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                )
                            }
                        }

                    }
                }
                state.hasSentRequest -> {
                    Text(
                        text = "Friend request sent",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                else -> {
                    TravelBuddyButton(
                        label = "Send Friend Request",
                        onClick = { showSendRequestDialog = true },
                        style = ButtonStyle.PRIMARY,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.PersonAdd,
                                contentDescription = "Add friend",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Profile Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    ProfileDetailItem("Name", state.name)
                    ProfileDetailItem("Email", state.email)
                    ProfileDetailItem("Phone", state.phoneNumber)
                    ProfileDetailItem("Location", state.city)
                    ProfileDetailItem("Bio", state.bio)
                }
            }
        }
    }
}