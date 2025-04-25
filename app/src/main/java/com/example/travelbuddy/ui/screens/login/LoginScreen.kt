package com.example.travelbuddy.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelbuddy.ui.TravelBuddyRoute
import com.example.travelbuddy.ui.composables.InputField
import com.example.travelbuddy.ui.composables.InputFieldType
import com.example.travelbuddy.ui.composables.TravelBuddyButton
import androidx.compose.foundation.clickable
import androidx.compose.runtime.LaunchedEffect
import com.example.travelbuddy.ui.composables.ButtonStyle
import com.example.travelbuddy.ui.composables.GoogleSignInButton

@Composable
fun LoginScreen(
    state: LoginState,
    actions: LoginActions,
    navController: NavController
) {
    LaunchedEffect(state.navigateToCode) {
        if (state.navigateToCode) {
            navController.navigate(TravelBuddyRoute.Code) {
                popUpTo(TravelBuddyRoute.Login) { inclusive = true }
            }
            actions.resetNavigation()
        }
    }
    Scaffold { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(contentPadding)
                .padding(12.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Travel Buddy",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.size(100.dp))
            InputField(
                value = state.email,
                label = "Email",
                onValueChange = actions::setEmail,
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Email,
                        contentDescription = "email icon"
                    )
                },
            )
            Spacer(modifier = Modifier.size(30.dp))
            InputField(
                value = state.password,
                label = "Password",
                onValueChange = actions::setPassword,
                type = InputFieldType.Password,
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Lock,
                        contentDescription = "password icon"
                    )
                },
            )
            Spacer(modifier = Modifier.size(30.dp))
            TravelBuddyButton(
                label = "Sign In",
                onClick = { actions.loginWithEmail()  },
                enabled = state.canSubmit,
                height = 50,
                isLoading = state.isLoading,
            )
            Spacer(modifier = Modifier.size(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            ) {
                androidx.compose.material3.HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                )
                Text(
                    text = "Or continue with",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                androidx.compose.material3.HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            GoogleSignInButton { account ->
                println(account)
                account?.let {
                    actions.handleGoogleSignIn(it)
                }
            }

            Spacer(modifier = Modifier.size(32.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = "If you don't have an Account")
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = "Sign Up",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                            navController.navigate(TravelBuddyRoute.SignUp)
                    }
                )
            }
        }
    }
}
