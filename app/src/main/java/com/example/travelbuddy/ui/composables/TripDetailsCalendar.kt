package com.example.travelbuddy.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.BitmapFactory
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upcoming
import androidx.compose.material3.Icon
import androidx.compose.ui.text.font.FontStyle
import com.example.travelbuddy.data.database.TripActivity
import com.example.travelbuddy.data.database.TripActivityType
import com.example.travelbuddy.utils.extractDay
import com.example.travelbuddy.utils.formatDateToMonthDay
import com.example.travelbuddy.utils.formatTimeRange
import com.example.travelbuddy.utils.parseDate

@Composable
fun TripDetailsCalendar(
    activities: List<TripActivity>,
    activityTypes: Map<Int, TripActivityType> = emptyMap(),
    onViewAllClick: () -> Unit
) {
    val groupedActivities = activities
        .sortedBy { parseDate(it.startDate) }
        .groupBy { extractDay(it.startDate) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Upcoming activities",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (activities.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "No upcoming activities",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                groupedActivities.forEach { (day, dayActivities) ->
                    dayActivities.forEach { activity ->
                        ActivityItem(
                            activity = activity,
                            activityType = activity.tripActivityTypeId?.let {
                                activityTypes[it]
                            }
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onViewAllClick) {
                    Text(
                        text = "View All Activities",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityItem(
    activity: TripActivity,
    activityType: TripActivityType?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .width(24.dp)
                .height(80.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(4.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text(
                text = formatDateToMonthDay(activity.startDate),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = activity.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = formatTimeRange(activity.startDate, activity.endDate),
                color = Color.Gray,
                fontSize = 14.sp
            )
        }

        Box(
            modifier = Modifier
                .size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            if (activityType?.icon != null) {
                val bitmap = rememberBytearrayAsBitmap(activityType.icon)
                bitmap?.let {
                    Image(
                        bitmap = it,
                        contentDescription = activityType.label,
                        modifier = Modifier.size(28.dp),
                        colorFilter = ColorFilter.tint(Color(0xFF00A67E))
                    )
                } ?: FallbackIcon(activityType.label)
            } else {
                FallbackIcon(activityType?.label ?: "Activity")
            }
        }
    }
}

@Composable
fun rememberBytearrayAsBitmap(byteArray: ByteArray?): ImageBitmap? {
    return remember(byteArray) {
        byteArray?.let {
            try {
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                bitmap?.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        }
    }
}

@Composable
fun FallbackIcon(typeLabel: String) {
    Icon(
        imageVector = Icons.Default.Upcoming,
        contentDescription = typeLabel,
        tint = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.size(28.dp)
    )
}