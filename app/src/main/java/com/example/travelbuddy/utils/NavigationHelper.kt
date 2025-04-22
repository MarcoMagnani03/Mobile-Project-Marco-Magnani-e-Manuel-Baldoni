package com.example.travelbuddy.utils

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.travelbuddy.ui.TravelBuddyRoute


fun NavController.getBackStackEntryOrNull(
    route: TravelBuddyRoute
): NavBackStackEntry? {
    return try {
        getBackStackEntry(route)
    } catch (e: IllegalArgumentException) {
        null
    }
}