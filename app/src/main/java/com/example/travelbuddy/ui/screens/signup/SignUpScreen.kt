package com.example.travelbuddy.ui.screens.signup

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

@Composable
fun SignUpScreen(
    state: SignUpState,
    actions: SignUpActions,
    navController: NavController
) {
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
                label = "Sign Up",
                onClick = { navController.navigate(TravelBuddyRoute.Home) },
                enabled = state.canSubmit,
                height = 50
            )
            Spacer(modifier = Modifier.size(16.dp))



            Spacer(modifier = Modifier.size(32.dp))
        }
    }
}
