package com.example.travelbuddy.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelbuddy.ui.TravelBuddyRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelBuddyTopBar(
    navController: NavController,
    title: String,
    subtitle: String? = null,
    canNavigateBack: Boolean = false,
    enableNotification: Boolean = true
) {
    TopAppBar(
        modifier = Modifier.padding(12.dp, 0.dp),
        title = {
            Column(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalAlignment = if (canNavigateBack) Alignment.CenterHorizontally else Alignment.Start
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = if (canNavigateBack) TextAlign.Center else TextAlign.Start,
                    color = if (canNavigateBack) Color.Unspecified else MaterialTheme.colorScheme.primary
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = if (canNavigateBack) TextAlign.Center else TextAlign.Start
                    )
                }
            }
        },
        navigationIcon = {
            if (canNavigateBack && navController.previousBackStackEntry != null) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back"
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = {
                if (enableNotification && navController.currentDestination?.route != TravelBuddyRoute.Notification::class.qualifiedName){
                    navController.navigate(TravelBuddyRoute.Notification){
                        launchSingleTop = true
                    }
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = if(enableNotification) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.Transparent
                    }
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}