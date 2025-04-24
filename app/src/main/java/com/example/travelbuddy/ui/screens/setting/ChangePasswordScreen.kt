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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.travelbuddy.ui.composables.InputField
import com.example.travelbuddy.ui.composables.InputFieldType
import com.example.travelbuddy.ui.composables.TravelBuddyTopBar

@Composable
fun ChangePasswordScreen(
    state: SettingState,
    actions: SettingActions,
    navController: NavController
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordMismatchError by remember { mutableStateOf<String?>(null) }


    Scaffold(
        topBar = {
            TravelBuddyTopBar(
                navController = navController,
                title = "Change Password",
                subtitle = "Update your account password",
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
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = "Current Password",
                modifier = Modifier.fillMaxWidth(),
                type = InputFieldType.Password,
                leadingIcon = { Icon(Icons.Outlined.Lock, "Password") }
            )

            InputField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = "New Password",
                modifier = Modifier.fillMaxWidth(),
                type = InputFieldType.Password,
                leadingIcon = { Icon(Icons.Outlined.Lock, "Password") }
            )

            InputField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm New Password",
                modifier = Modifier.fillMaxWidth(),
                type = InputFieldType.Password,
                leadingIcon = { Icon(Icons.Outlined.Lock, "Password") }
            )

            passwordMismatchError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

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
                    if (newPassword != confirmPassword) {
                        passwordMismatchError = "Confirmation password does not match"
                        actions.clearMessages()
                    } else {
                        passwordMismatchError = null
                        actions.onChangePassword(currentPassword, newPassword)
                    }
                },
                enabled = newPassword.isNotBlank() && confirmPassword.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Change Password")
            }
        }
    }
}
