package com.example.travelbuddy.ui.screens.code

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelbuddy.data.repositories.UserSessionRepository
import com.example.travelbuddy.ui.TravelBuddyRoute
import com.example.travelbuddy.ui.composables.InputField
import com.example.travelbuddy.ui.composables.TravelBuddyButton
@Composable
fun CodeScreen(
    state: CodeState,
    actions: CodeActions,
    navController: NavController,
    isNewPinSetup: Boolean,
    appPreferences: UserSessionRepository
) {
    val screenTitle = if (isNewPinSetup) "Create PIN" else "Enter PIN"
    val buttonText = if (isNewPinSetup) "Create Secure PIN" else "Confirm"
    val inputLabel = if (isNewPinSetup) "Create 6-digit code" else "Enter your PIN"

    val emailState = appPreferences.userEmail.collectAsState(initial = "")
    val userEmail = emailState.value

    Scaffold { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .padding(contentPadding)
                .padding(12.dp)
                .fillMaxSize()
        ) {
            Text(
                text = screenTitle,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.size(40.dp))

            if (!isNewPinSetup) {
                TravelBuddyButton(
                    label = "Use biometrics",
                    onClick = { navController.navigate(TravelBuddyRoute.Home) },
                )
                Spacer(modifier = Modifier.size(20.dp))
            }

            InputField(
                value = state.code,
                label = inputLabel,
                placeholder = "Enter 6-digit code",
                onValueChange = actions::setCode,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
            )

            Spacer(modifier = Modifier.size(30.dp))

            state.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }

            CustomKeypad(
                onNumberClick = { digit ->
                    if (state.code.length < 6) {
                        actions.setCode(state.code + digit.toString())
                    }
                },
                onConfirm = {
                    if (state.code.length == 6 && !state.isLoading) {
                        userEmail?.let { email ->
                            handlePinAction(
                                isNewPinSetup = isNewPinSetup,
                                userEmail = email,
                                state = state,
                                actions = actions,
                                navController = navController
                            )
                        }
                    }
                },
                onDelete = {
                    if (state.code.isNotEmpty()) {
                        actions.setCode(state.code.dropLast(1))
                    }
                }
            )

            Spacer(modifier = Modifier.size(20.dp))

            TravelBuddyButton(
                label = if (state.isLoading) "Processing..." else buttonText,
                onClick = {
                    if (state.code.length == 6 && !state.isLoading) {
                        userEmail?.let { email ->
                            handlePinAction(
                                isNewPinSetup = isNewPinSetup,
                                userEmail = email,
                                state = state,
                                actions = actions,
                                navController = navController
                            )
                        }
                    }
                },
                enabled = state.code.length == 6 && !state.isLoading
            )
        }
    }
}

private fun handlePinAction(
    isNewPinSetup: Boolean,
    userEmail: String,
    state: CodeState,
    actions: CodeActions,
    navController: NavController
) {
    val goToHome: () -> Unit = {
        navController.navigate(TravelBuddyRoute.Home) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    if (isNewPinSetup) {
        actions.savePin(userEmail, state.code, onComplete = goToHome)
    } else {
        actions.verifyPin(userEmail, state.code) { isValid ->
            if (isValid) {
                goToHome()
            } else {
                actions.showError("Wrong PIN")
            }
        }
    }
}
