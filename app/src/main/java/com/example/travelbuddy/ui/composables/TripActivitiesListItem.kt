package com.example.travelbuddy.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelbuddy.data.database.TripActivity
import com.example.travelbuddy.data.database.TripActivityType
import com.example.travelbuddy.ui.theme.Green20
import com.example.travelbuddy.utils.formatDateToMonthDay
import com.example.travelbuddy.utils.formatTimeRange

@Composable
fun TripActivityListItem(
    tripActivity: TripActivity,
    tripActivityType: TripActivityType?,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onLocateOnMapClick: () -> Unit,
    onCardClick: () -> Unit,
    onAddToCalendar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = onCardClick
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .padding(start = 16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f) // Ensures it doesn't take up infinite width
            ) {
                if(tripActivityType != null){
                    Box(
                        modifier = Modifier
                            .background(
                                color = Green20,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = tripActivityType.label,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Text(
                    text = tripActivity.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = "${formatDateToMonthDay(tripActivity.startDate)} · ${formatTimeRange(tripActivity.startDate, tripActivity.endDate)}",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                if(tripActivity.pricePerPerson != null){
                    Text(
                        text = "Price per person: €${tripActivity.pricePerPerson}",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                Text(
                    text = tripActivity.notes.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 8.dp),
                    fontSize = 14.sp
                )
            }

            TravelBuddyDropDownMenu(
                menuItems = listOf(
                    TravelBuddyDropDownMenuItem(
                        text = {
                            Text("Locate on map")
                        },
                        onClick = onLocateOnMapClick,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Locate activity",
                            )
                        }
                    ),
                    TravelBuddyDropDownMenuItem(
                        text = {
                            Text("Edit")
                        },
                        onClick = onEditClick,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit trip activity",
                            )
                        }
                    ),
                    TravelBuddyDropDownMenuItem(
                        text = {
                            Text("Add to calendar")
                        },
                        onClick = onAddToCalendar,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.EditCalendar,
                                contentDescription = "Edit trip activity",
                            )
                        }
                    ),
                    TravelBuddyDropDownMenuItem(
                        text = {
                            Text("Delete")
                        },
                        onClick = onDeleteClick,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete trip activity",
                            )
                        },
                        color = Color.Red
                    )
                )
            )
        }
    }
}