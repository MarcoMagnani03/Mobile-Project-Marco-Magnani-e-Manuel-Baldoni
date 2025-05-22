package com.example.travelbuddy.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Euro
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
import androidx.navigation.NavController
import com.example.travelbuddy.data.database.Trip
import com.example.travelbuddy.data.database.TripWithTripActivitiesAndExpenses
import com.example.travelbuddy.ui.TravelBuddyRoute
import com.example.travelbuddy.ui.theme.Green20

@Composable
fun ActiveTripCard(
    trip: TripWithTripActivitiesAndExpenses,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        onClick = {
            navController.navigate(TravelBuddyRoute.TripDetails(trip.trip.id.toString()))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = trip.trip.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Box(
                    modifier = Modifier
                        .background(
                            color = Green20,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "In progress",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Text(
                text = "${trip.trip.startDate} - ${trip.trip.endDate}",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Scheduled events",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${trip.activities.size}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total expenses",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "€${trip.expenses.sumOf { expense -> expense.amount }}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TravelBuddyButton(
                label = "Add expense",
                onClick = {
                    navController.navigate(TravelBuddyRoute.NewExpense(trip.trip.id.toString()))
                },
                style = ButtonStyle.PRIMARY,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Euro,
                        contentDescription = "Add Expense",
                        tint = Color.White
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            TravelBuddyButton(
                label = "Add event",
                onClick = {
                    navController.navigate(TravelBuddyRoute.NewTripActivity(trip.trip.id.toString()))
                },
                style = ButtonStyle.PRIMARY_OUTLINED,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Event",
                        tint = Color(0xFF00A87E)
                    )
                }
            )
        }
    }
}