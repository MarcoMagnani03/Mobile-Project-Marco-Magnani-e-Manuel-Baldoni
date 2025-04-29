package com.example.travelbuddy.ui.composables

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
    onDeleteClick: (() -> Unit)? = null
) {
    var showDialog by remember { mutableStateOf(false) }
    val isError = notification.type.icon?.lowercase() == "error"
    val textColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSecondaryContainer
    val labelColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
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

    Surface(
        modifier = Modifier.size(40.dp),
        shape = MaterialTheme.shapes.small,
        color = if (isError) MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
        else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    ) {
        Box(contentAlignment = Alignment.Center) {
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
}
