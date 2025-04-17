package com.example.travelbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.example.travelbuddy.ui.TravelBuddyNavGraph
import com.example.travelbuddy.ui.theme.TravelBuddyTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TravelBuddyTheme(dynamicColor = false) {
                val navController = rememberNavController()
                TravelBuddyNavGraph(navController)
            }
        }
    }
}
