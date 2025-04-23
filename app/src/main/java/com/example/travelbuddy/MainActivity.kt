package com.example.travelbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.example.travelbuddy.ui.TravelBuddyNavGraph
import com.example.travelbuddy.ui.screens.setting.SettingViewModel
import com.example.travelbuddy.ui.screens.setting.ThemeOption
import com.example.travelbuddy.ui.theme.TravelBuddyTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingViewModel: SettingViewModel = koinViewModel()
            val settingState by settingViewModel.state.collectAsState()

            val darkTheme = when (settingState.selectedTheme) {
                ThemeOption.SYSTEM -> isSystemInDarkTheme()
                ThemeOption.DARK -> true
                ThemeOption.LIGHT -> false
            }

            TravelBuddyTheme(
                darkTheme = darkTheme,
                dynamicColor = false
            ) {
                val navController = rememberNavController()
                TravelBuddyNavGraph(navController)
            }
        }
    }
}
