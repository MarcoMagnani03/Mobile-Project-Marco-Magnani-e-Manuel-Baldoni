package com.example.travelbuddy.ui.screens.code

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.example.travelbuddy.data.repositories.UserSessionRepository
import com.example.travelbuddy.ui.TravelBuddyRoute
import com.example.travelbuddy.ui.composables.ButtonStyle
import com.example.travelbuddy.ui.composables.CustomKeypad
import com.example.travelbuddy.ui.composables.InputField
import com.example.travelbuddy.ui.composables.TravelBuddyButton
import com.example.travelbuddy.utils.BiometricAuthenticator
import com.example.travelbuddy.utils.BiometricResult

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
    val activity = (LocalActivity.current as? FragmentActivity) ?: throw IllegalStateException("FragmentActivity not found")
    val biometricAuthenticator = BiometricAuthenticator(activity)

    val goToHome: () -> Unit = {
        navController.navigate(TravelBuddyRoute.Home) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    // Function to handle biometric authentication
    fun authenticateWithBiometrics() {
        if (isNewPinSetup) return // Don't allow biometrics for new PIN setup

        biometricAuthenticator.authenticate(
            title = "Authenticate with Biometrics",
            subtitle = "Use your fingerprint or face to login",
            onResult = { result ->
                when (result) {
                    is BiometricResult.Success -> {
                        userEmail?.let { email ->
                            actions.verifyBiometricAuth() { isValid ->
                                if (isValid) {
                                    goToHome()
                                } else {
                                    println(email)
                                    actions.showError("Biometric authentication failed")
                                }
                            }
                        }
                    }
                    is BiometricResult.Error -> {
                        actions.showError(result.errorMessage)
                    }
                    BiometricResult.Failed -> {
                        actions.showError("Authentication failed")
                    }
                    BiometricResult.Cancelled -> {
                        // User cancelled the operation, no action needed
                    }
                }
            }
        )
    }

    // If it's not a new PIN setup and biometric is available, prompt immediately
    LaunchedEffect(Unit) {
        if (!isNewPinSetup && biometricAuthenticator.isBiometricAvailable()) {
            authenticateWithBiometrics()
        }
    }

    val scrollState = rememberScrollState()

    Scaffold { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .padding(12.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = screenTitle,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.size(40.dp))

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
                onBiometrics = {
                    if (!isNewPinSetup && biometricAuthenticator.isBiometricAvailable()) {
                        authenticateWithBiometrics()
                    } else if (!biometricAuthenticator.isBiometricAvailable()) {
                        actions.showError("Biometric authentication not available")
                    }
                },
                onDelete = {
                    if (state.code.isNotEmpty()) {
                        actions.setCode(state.code.dropLast(1))
                    }
                },
                showBiometricButton = !isNewPinSetup && biometricAuthenticator.isBiometricAvailable()
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

            if(!isNewPinSetup){
                Spacer(modifier = Modifier.height(16.dp))

                TravelBuddyButton(
                    label = "Back to login",
                    onClick = { actions.logout(navController) },
                    style = ButtonStyle.ERROR,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Back to login",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }
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