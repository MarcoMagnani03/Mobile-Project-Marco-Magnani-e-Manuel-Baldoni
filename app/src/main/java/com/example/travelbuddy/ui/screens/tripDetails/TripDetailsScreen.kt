package com.example.travelbuddy.ui.screens.tripDetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelbuddy.ui.TravelBuddyRoute
import com.example.travelbuddy.ui.composables.BalanceEntry
import com.example.travelbuddy.ui.composables.ButtonStyle
import com.example.travelbuddy.ui.composables.InviteFriendsDialog
import com.example.travelbuddy.ui.composables.TravelBuddyBottomBar
import com.example.travelbuddy.ui.composables.TravelBuddyButton
import com.example.travelbuddy.ui.composables.TravelBuddyTopBar
import com.example.travelbuddy.ui.composables.TripDetailsCalendar
import com.example.travelbuddy.ui.composables.TripDetailsQuickBalance
import com.example.travelbuddy.ui.composables.TripGroupMembers
import java.util.Date
import com.example.travelbuddy.utils.formatTimeRange
import com.example.travelbuddy.utils.parseDate

@Composable
fun TripDetailsScreen(
    state: TripDetailsState,
    actions: TripDetailsActions,
    navController: NavController
){
    val isPastTrip = parseDate(state.trip?.trip?.endDate ?: "").before(Date())

    val scrollState = rememberScrollState()

    val activityTypesMap = remember(state.tripActivityTypes) {
        state.tripActivityTypes.associateBy { it.id }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm delete", style = MaterialTheme.typography.headlineLarge) },
            text = { Text("Are you sure you want to delete this expense?", style = MaterialTheme.typography.bodyLarge) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        if(state.trip?.trip != null){
                            actions.deleteTrip()
                            navController.navigate(TravelBuddyRoute.Home)
                        }
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    var showLeaveDialog by remember { mutableStateOf(false) }

    if (showLeaveDialog) {
        AlertDialog(
            onDismissRequest = { showLeaveDialog = false },
            title = { Text("Confirm leave group", style = MaterialTheme.typography.headlineLarge) },
            text = { Text("Are you sure you want to leave this group?", style = MaterialTheme.typography.bodyLarge) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLeaveDialog = false
                        if(state.trip?.trip != null){
                            actions.removeUserFromGroup()
                            navController.navigate(TravelBuddyRoute.Home)
                        }
                    }
                ) {
                    Text("Leave", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    var showAddToGroup by remember { mutableStateOf(false) }

    InviteFriendsDialog(
        showDialog = showAddToGroup,
        onDismiss = { showAddToGroup = false },
        allFriends = state.friendList,
        groupEmails = state.trip?.usersGroup?.map { it.email } ?: emptyList(),
        invitedEmails = state.trip?.invitationGroup?.map { it.invitation.receiverEmail } ?: emptyList(),
        onInvite = { selectedFriends ->
            actions.sendInvitations(selectedFriends)
        }
    )

    Scaffold(
        topBar = {
            TravelBuddyTopBar(
                navController = navController,
                title = state.trip?.trip?.name ?: "Trip title",
                subtitle = formatTimeRange(state.trip?.trip?.startDate ?: "", state.trip?.trip?.endDate ?: ""),
                canNavigateBack = true,
            )
        },
        bottomBar = { TravelBuddyBottomBar(navController) }
    ) { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(contentPadding)
                .padding(horizontal = 12.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            TripDetailsCalendar(
                activities =
                    state.trip?.activities
                        ?.filter { activity -> parseDate(activity.endDate).after(Date()) }
                        ?.sortedBy { activity -> parseDate(activity.startDate) }
                        ?.take(3) ?: emptyList(),
                activityTypes = activityTypesMap,
                onViewAllClick = {
                    navController.navigate(TravelBuddyRoute.TripActivities(tripId = state.trip?.trip?.id.toString()))
                }
            )

            TripDetailsQuickBalance(
                balanceEntries = state.trip?.expenses?.map { expense ->
                    BalanceEntry(
                        name = expense.title,
                        amount = expense.amount
                    )
                }?.take(3) ?: emptyList(),
                totalExpenses = state.trip?.expenses?.sumOf { expense -> expense.amount } ?: 0.0,
                onViewAllExpensesClick = {
                    navController.navigate(TravelBuddyRoute.BudgetOverview(tripId = state.trip?.trip?.id.toString()))
                }
            )

            TripGroupMembers(
                members = state.trip?.usersGroup ?: emptyList(),
                groupInvitations = state.trip?.invitationGroup ?: emptyList(),
                displayAddMemberButton = !isPastTrip,
                onAddMemberClick = {
                    showAddToGroup = true
                },
                onLeaveGroupClick = {
                    showLeaveDialog = true
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    ),
                    onClick = {
                        navController.navigate(TravelBuddyRoute.TripPhotos(tripId = state.trip?.trip?.id.toString()))
                    }
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp).fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = "Go to photos",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Trip Photos",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if(!isPastTrip){
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        ),
                        onClick = {
                            navController.navigate(TravelBuddyRoute.DiscoverEvents(tripId = state.trip?.trip?.id.toString()))
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp).fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationSearching,
                                contentDescription = "Discover events",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "Discover Events",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Column (
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TravelBuddyButton(
                    style = ButtonStyle.PRIMARY,
                    label = "Edit trip",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Trip",
                            tint = Color.White
                        )
                    },
                    onClick = {
                        navController.navigate(TravelBuddyRoute.EditTrip(tripId = state.trip?.trip?.id.toString()))
                    }
                )

                TravelBuddyButton(
                    style = ButtonStyle.ERROR,
                    label = "Delete trip",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete trip",
                            tint = Color.White
                        )
                    },
                    onClick = {
                        showDeleteDialog = true
                    }
                )
            }

            Spacer(Modifier.height(10.dp))
        }
    }
}