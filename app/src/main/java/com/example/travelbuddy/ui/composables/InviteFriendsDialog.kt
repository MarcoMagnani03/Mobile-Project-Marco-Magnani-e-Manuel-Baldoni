package com.example.travelbuddy.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.travelbuddy.data.database.User


@Composable
fun InviteFriendsDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    allFriends: List<User>, // tutti gli amici disponibili
    groupEmails: List<String>, // state.trip.usersGroup.email
    invitedEmails: List<String>, // state.trip.invitationGroup.invitation.receiverEmail
    onInvite: (List<User>) -> Unit
) {
    var selectedFriends by remember { mutableStateOf(setOf<User>()) }

    val eligibleFriends = remember(showDialog, allFriends, groupEmails, invitedEmails) {
        allFriends.filter { user ->
            user.email !in groupEmails && user.email !in invitedEmails
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = {
                Text("Add friends to trip", style = MaterialTheme.typography.headlineLarge)
            },
            text = {
                if (eligibleFriends.isEmpty()) {
                    Text("No eligible friends to invite.")
                } else {
                    LazyColumn {
                        items(eligibleFriends) { friend ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedFriends = if (friend in selectedFriends) {
                                            selectedFriends - friend
                                        } else {
                                            selectedFriends + friend
                                        }
                                    }
                                    .padding(8.dp)
                            ) {
                                Checkbox(
                                    checked = friend in selectedFriends,
                                    onCheckedChange = {
                                        selectedFriends = if (it) {
                                            selectedFriends + friend
                                        } else {
                                            selectedFriends - friend
                                        }
                                    }
                                )
                                Text("${friend.firstname} ${friend.lastname}" ?: friend.email, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onInvite(selectedFriends.toList())
                        onDismiss()
                    },
                    enabled = selectedFriends.isNotEmpty()
                ) {
                    Text("Add", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = Color.Red)
                }
            }
        )
    }
}
