package com.example.travelbuddy.ui.screens.notifications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.travelbuddy.ui.composables.NotificationItem
import com.example.travelbuddy.ui.composables.TravelBuddyBottomBar
import com.example.travelbuddy.ui.composables.TravelBuddyTopBar

enum class NotificationFilter {
    ALL,
    READ,
    UNREAD
}

@Composable
fun NotificationsScreen(
    state: NotificationsState,
    actions: NotificationsActions,
    navController: NavController
) {
    var currentFilter by remember { mutableStateOf(NotificationFilter.ALL) }
    // Removed showFilterMenu variable as it's no longer needed

    LaunchedEffect(Unit) {
        actions.loadNotifications()
    }

    Scaffold(
        topBar = {
            TravelBuddyTopBar(
                navController = navController,
                title = "Notifications",
                subtitle = "Your updates and alerts",
                canNavigateBack = true
            )
        },
        bottomBar = { TravelBuddyBottomBar(navController = navController) }
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
                // Filter tabs at the top
                NotificationFilterTabs(
                    currentFilter = currentFilter,
                    onFilterSelected = { currentFilter = it }
                )

                // Filter notifications based on current filter
                val filteredNotifications = when (currentFilter) {
                    NotificationFilter.ALL -> state.notifications
                    NotificationFilter.READ -> state.notifications.filter { it.notification.isRead }
                    NotificationFilter.UNREAD -> state.notifications.filter { !it.notification.isRead }
                }

                // Sort notifications by sentAt date (newest first)
                val sortedNotifications = filteredNotifications.sortedByDescending { it.notification.sentAt }

                if (sortedNotifications.isEmpty()) {
                    EmptyNotificationsMessage(currentFilter)
                } else {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
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
        modifier = Modifier.fillMaxSize(),
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