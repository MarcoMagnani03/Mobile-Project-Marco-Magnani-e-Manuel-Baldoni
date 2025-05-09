package com.example.travelbuddy.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelbuddy.ui.screens.notifications.NotificationWithType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getNotificationIcon(iconName: String?): ImageVector {
    return when (iconName?.lowercase()) {
        "info" -> Icons.Filled.Info
        "error" -> Icons.Filled.Error
        else -> Icons.Filled.Notifications
    }
}

@Composable
fun NotificationItem(
    notification: NotificationWithType,
    onDeleteClick: (() -> Unit)? = null,
    onMarkAsRead: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val isError = notification.type.icon?.lowercase() == "error"
    val textColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSecondaryContainer
    val labelColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    val sentDate = remember(notification.notification.sentAt) {
        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        formatter.format(Date(notification.notification.sentAt))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onMarkAsRead),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.notification.isRead)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon area
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                NotificationIconVector(iconName = notification.type.icon)
            }

            // Content area
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = notification.notification.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.notification.description,
                    fontSize = 14.sp,
                    color = textColor.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = notification.type.label,
                    fontSize = 12.sp,
                    color = labelColor,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Data invio
                Text(
                    text = sentDate,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Delete button with confirmation dialog
            if (onDeleteClick != null) {
                IconButton(
                    onClick = { showDialog = true },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete notification",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    if (showDialog && onDeleteClick != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onDeleteClick()
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Delete notification?") },
            text = { Text("Are you sure you want to delete this notification?") },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = "Warning",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        )
    }
}

@Composable
fun NotificationIconVector(iconName: String?) {
    val isError = iconName?.lowercase() == "error"

    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.size(40.dp)) {
        val icon = getNotificationIcon(iconName)
        Icon(
            imageVector = icon,
            contentDescription = "Notification icon",
            tint = if (isError) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
    }
}
