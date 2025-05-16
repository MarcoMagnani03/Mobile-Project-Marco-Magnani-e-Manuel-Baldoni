package com.example.travelbuddy.ui.screens.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelbuddy.ui.TravelBuddyRoute
import com.example.travelbuddy.ui.composables.CardWithRow
import com.example.travelbuddy.ui.composables.TravelBuddyBottomBar
import com.example.travelbuddy.ui.composables.TravelBuddyTopBar
import com.example.travelbuddy.ui.screens.setting.SettingViewModel

enum class ThemeOption {
    SYSTEM, LIGHT, DARK
}

@Composable
fun SettingScreen(
    state: SettingState,
    actions: SettingActions,
    navController: NavController
) {
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            TravelBuddyTopBar(
                navController = navController,
                title = "Setting",
                null,
                canNavigateBack = true
            )
        },
        bottomBar = { TravelBuddyBottomBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
        ) {

            Text("Privacy", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(15.dp))
            CardWithRow("Change Password") {
                navController.navigate(TravelBuddyRoute.ChangePassword){
                    launchSingleTop = true
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            CardWithRow("Set Access PIN") {
                navController.navigate(TravelBuddyRoute.ChangePin){
                    launchSingleTop = true
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Theme", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))

            ThemeOption.entries.forEach { option ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (state.selectedTheme == option),
                            onClick = { actions.onThemeSelected(option) }
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (state.selectedTheme == option),
                        onClick = { actions.onThemeSelected(option) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = option.name.lowercase().replaceFirstChar { it.uppercase() }
                    )
                }
            }
        }
    }
}
