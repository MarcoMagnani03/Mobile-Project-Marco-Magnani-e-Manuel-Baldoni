package com.example.travelbuddy.ui.screens.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import com.example.travelbuddy.ui.composables.InputField
import com.example.travelbuddy.ui.composables.TravelBuddyTopBar


@Composable
fun ChangePinScreen(
    state: SettingState,
    actions: SettingActions,
    navController: NavController
) {
    var currentPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TravelBuddyTopBar(
                navController = navController,
                title = "Set Access PIN",
                subtitle = "Manage your security PIN",
                canNavigateBack = true
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InputField(
                value = currentPin,
                onValueChange = { currentPin = it },
                label = "Current PIN",
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
            )

            InputField(
                value = newPin,
                onValueChange = { newPin = it },
                label = "New PIN",
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
            )

            InputField(
                value = confirmPin,
                onValueChange = { confirmPin = it },
                label = "Confirm New PIN",
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
            )

            state.errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            state.successMessage?.let {
                LaunchedEffect(it) {
                    navController.popBackStack()
                    actions.clearMessages()
                }
            }

            Button(
                onClick = {
                    if (newPin != confirmPin) {
                        actions.clearMessages()
                        actions.onSetPin("", "")
                    } else {
                        actions.onSetPin(currentPin, newPin)
                    }
                },
                enabled = newPin.isNotBlank() && confirmPin.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Set PIN")
            }
        }
    }
}
