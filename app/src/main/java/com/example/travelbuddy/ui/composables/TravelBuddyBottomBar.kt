package com.example.travelbuddy.ui.composables

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.travelbuddy.ui.TravelBuddyRoute

sealed class BottomNavItem(
    val route: TravelBuddyRoute,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(
        route = TravelBuddyRoute.Home,
        title = "Home",
        icon = Icons.Default.Home
    )

    object NewTrip : BottomNavItem(
        route = TravelBuddyRoute.Home,
        title = "New Trip",
        icon = Icons.Default.AddCircle
    )

    object Friends : BottomNavItem(
        route = TravelBuddyRoute.Home,
        title = "Friends",
        icon = Icons.Default.People
    )

    object Profile : BottomNavItem(
        route = TravelBuddyRoute.Home,
        title = "Profile",
        icon = Icons.Default.Person
    )
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.NewTrip,
    BottomNavItem.Friends,
    BottomNavItem.Profile
)

@Composable
fun TravelBuddyBottomBar(navController: NavController) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White
    ) {
        bottomNavItems.forEach { item ->
            AddBottomNavItem(
                navItem = item,
                navController = navController
            )
        }
    }
}

@Composable
fun RowScope.AddBottomNavItem(
    navItem: BottomNavItem,
    navController: NavController
) {
    val selected = navController.currentDestination == navItem.route

    NavigationBarItem(
        selected = selected,
        onClick = {
            navController.navigate(navItem.route) {
                // Evita di creare più istanze della stessa destinazione
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        },
        icon = {
            Icon(
                imageVector = navItem.icon,
                contentDescription = navItem.title,
                modifier = Modifier.size(24.dp),
                tint = if (selected) Color.White else Color.White.copy(alpha = 0.7f)
            )
        },
        label = { Text(text = navItem.title) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color.White,
            unselectedIconColor = Color.White.copy(alpha = 0.7f),
            selectedTextColor = Color.White,
            unselectedTextColor = Color.White.copy(alpha = 0.7f),
            indicatorColor = MaterialTheme.colorScheme.primary
        )
    )
}