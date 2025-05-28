package com.example.travelbuddy.ui.screens.notifications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.travelbuddy.data.database.GroupInvitation
import com.example.travelbuddy.data.database.InvitationWithTripName
import com.example.travelbuddy.ui.composables.NotificationItem
import com.example.travelbuddy.ui.composables.TravelBuddyBottomBar
import com.example.travelbuddy.ui.composables.TravelBuddyTopBar
import kotlinx.coroutines.delay

enum class NotificationFilter {
    ALL,
    READ,
    UNREAD
}

// Classe per gestire il feedback delle azioni
data class InviteActionFeedback(
    val inviteId: String,
    val action: InviteAction,
    val tripName: String,
    val isVisible: Boolean = true
)

enum class InviteAction {
    ACCEPTED,
    DECLINED
}

@Composable
fun NotificationsScreen(
    state: NotificationsState,
    actions: NotificationsActions,
    navController: NavController
) {
    var currentFilter by remember { mutableStateOf(NotificationFilter.ALL) }

    // Stato per gestire il feedback delle azioni
    var actionFeedbacks by remember { mutableStateOf<List<InviteActionFeedback>>(emptyList()) }

    // Snackbar host state per messaggi di conferma
    val snackbarHostState = remember { SnackbarHostState() }

    // LaunchedEffect per mostrare snackbar quando ci sono nuovi feedback
    LaunchedEffect(actionFeedbacks.size) {
        if (actionFeedbacks.isNotEmpty()) {
            val lastFeedback = actionFeedbacks.last()
            val message = when (lastFeedback.action) {
                InviteAction.ACCEPTED -> "You joined the group for \"${lastFeedback.tripName}\"!"
                InviteAction.DECLINED -> "Invitation for \"${lastFeedback.tripName}\" declined"
            }
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }

    LaunchedEffect(Unit) {
        actions.loadNotifications()
        actions.loadGroupInvites()
    }

    Scaffold(
        topBar = {
            TravelBuddyTopBar(
                navController = navController,
                title = "Notifications & Invites",
                subtitle = "Your updates, alerts and group invitations",
                canNavigateBack = true
            )
        },
        bottomBar = { TravelBuddyBottomBar(navController = navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
        ) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = state.errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    // Mostra i feedback delle azioni completate
                    items(actionFeedbacks.filter { it.isVisible }) { feedback ->
                        ActionFeedbackCard(
                            feedback = feedback,
                            onDismiss = {
                                actionFeedbacks = actionFeedbacks.map {
                                    if (it.inviteId == feedback.inviteId) it.copy(isVisible = false) else it
                                }
                            }
                        )
                    }

                    // Group Invites Section
                    if (state.groupInvites.isNotEmpty()) {
                        item {
                            GroupInvitesSection(
                                invites = state.groupInvites,
                                onAcceptInvite = { email, tripId ->
                                    val invite = state.groupInvites.find { it.invitation.tripId == tripId }
                                    invite?.let {
                                        // Aggiungi feedback
                                        val feedback = InviteActionFeedback(
                                            inviteId = "${email}_${tripId}",
                                            action = InviteAction.ACCEPTED,
                                            tripName = it.tripName
                                        )
                                        actionFeedbacks = actionFeedbacks + feedback
                                    }
                                    actions.acceptGroupInvite(email, tripId)
                                },
                                onDeclineInvite = { email, tripId ->
                                    val invite = state.groupInvites.find { it.invitation.tripId == tripId }
                                    invite?.let {
                                        // Aggiungi feedback
                                        val feedback = InviteActionFeedback(
                                            inviteId = "${email}_${tripId}",
                                            action = InviteAction.DECLINED,
                                            tripName = it.tripName
                                        )
                                        actionFeedbacks = actionFeedbacks + feedback
                                    }
                                    actions.declineGroupInvite(email, tripId)
                                }
                            )
                        }
                    }

                    // Notifications Section
                    item {
                        Text(
                            text = "Notifications",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    item {
                        NotificationFilterTabs(
                            currentFilter = currentFilter,
                            onFilterSelected = { currentFilter = it }
                        )
                    }

                    // Filter notifications based on current filter
                    val filteredNotifications = when (currentFilter) {
                        NotificationFilter.ALL -> state.notifications
                        NotificationFilter.READ -> state.notifications.filter { it.notification.isRead }
                        NotificationFilter.UNREAD -> state.notifications.filter { !it.notification.isRead }
                    }

                    // Sort notifications by sentAt date (newest first)
                    val sortedNotifications = filteredNotifications.sortedByDescending { it.notification.sentAt }

                    if (sortedNotifications.isEmpty()) {
                        item {
                            EmptyNotificationsMessage(currentFilter)
                        }
                    } else {
                        items(sortedNotifications) { notification ->
                            NotificationItem(
                                notification = notification,
                                onDeleteClick = { actions.deleteNotification(notification.notification.id) },
                                onMarkAsRead = {
                                    if (!notification.notification.isRead) {
                                        actions.markNotificationAsRead(notification.notification.id)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActionFeedbackCard(
    feedback: InviteActionFeedback,
    onDismiss: () -> Unit
) {
    val backgroundColor = when (feedback.action) {
        InviteAction.ACCEPTED -> MaterialTheme.colorScheme.primaryContainer
        InviteAction.DECLINED -> MaterialTheme.colorScheme.errorContainer
    }

    val contentColor = when (feedback.action) {
        InviteAction.ACCEPTED -> MaterialTheme.colorScheme.onPrimaryContainer
        InviteAction.DECLINED -> MaterialTheme.colorScheme.onErrorContainer
    }

    val icon = when (feedback.action) {
        InviteAction.ACCEPTED -> Icons.Filled.CheckCircle
        InviteAction.DECLINED -> Icons.Filled.Cancel
    }

    val message = when (feedback.action) {
        InviteAction.ACCEPTED -> "You successfully joined the group for \"${feedback.tripName}\""
        InviteAction.DECLINED -> "You declined the invitation for \"${feedback.tripName}\""
    }

    // Auto-dismiss dopo 5 secondi
    LaunchedEffect(feedback.inviteId) {
        delay(5000)
        onDismiss()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Cancel,
                    contentDescription = "Dismiss",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun GroupInvitesSection(
    invites: List<InvitationWithTripName>,
    onAcceptInvite: (String, Long) -> Unit,
    onDeclineInvite: (String, Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Group Invitations",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            if (invites.size > 5) {
                TextButton(
                    onClick = { expanded = !expanded },
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(if (expanded) "View Less" else "View All")
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = if (expanded) "View less invites" else "View all invites",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        val displayedInvites = if (expanded) invites else invites.take(5)

        displayedInvites.forEach { invite ->
            GroupInviteItem(
                invite = invite,
                onAccept = { onAcceptInvite(invite.invitation.receiverEmail, invite.invitation.tripId) },
                onDecline = { onDeclineInvite(invite.invitation.receiverEmail, invite.invitation.tripId) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun GroupInviteItem(
    invite: InvitationWithTripName,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    var isProcessing by remember { mutableStateOf(false) }
    var actionCompleted by remember { mutableStateOf<InviteAction?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = "Group invite",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Invitation to join",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = invite.tripName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Invited by ${invite.invitation.senderEmail}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (actionCompleted != null) {
                // Mostra il risultato dell'azione
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val (icon, text, color) = when (actionCompleted) {
                        InviteAction.ACCEPTED -> Triple(
                            Icons.Filled.CheckCircle,
                            "Invitation accepted!",
                            MaterialTheme.colorScheme.primary
                        )
                        InviteAction.DECLINED -> Triple(
                            Icons.Filled.Cancel,
                            "Invitation declined",
                            MaterialTheme.colorScheme.error
                        )
                        null -> Triple(Icons.Filled.CheckCircle, "", MaterialTheme.colorScheme.primary)
                    }

                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = color,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                // Mostra i pulsanti di azione
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            isProcessing = true
                            onDecline()
                            actionCompleted = InviteAction.DECLINED
                            isProcessing = false
                        },
                        enabled = !isProcessing,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        if (isProcessing && actionCompleted == InviteAction.DECLINED) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Decline")
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            isProcessing = true
                            onAccept()
                            actionCompleted = InviteAction.ACCEPTED
                            isProcessing = false
                        },
                        enabled = !isProcessing
                    ) {
                        if (isProcessing && actionCompleted == InviteAction.ACCEPTED) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Accept")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationFilterTabs(
    currentFilter: NotificationFilter,
    onFilterSelected: (NotificationFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        NotificationFilter.entries.forEach { filter ->
            FilterTab(
                filter = filter,
                isSelected = filter == currentFilter,
                onClick = { onFilterSelected(filter) }
            )
        }
    }
}

@Composable
fun FilterTab(
    filter: NotificationFilter,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val text = when (filter) {
        NotificationFilter.ALL -> "All"
        NotificationFilter.READ -> "Read"
        NotificationFilter.UNREAD -> "Unread"
    }

    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    }

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = if (isSelected) 0.dp else 2.dp,
        shadowElevation = if (isSelected) 2.dp else 0.dp,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun EmptyNotificationsMessage(filter: NotificationFilter = NotificationFilter.ALL) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = "No notifications",
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = when (filter) {
                    NotificationFilter.ALL -> "You have no notifications"
                    NotificationFilter.READ -> "You have no read notifications"
                    NotificationFilter.UNREAD -> "You have no unread notifications"
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}