package com.example.travelbuddy.ui.screens.signup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelbuddy.ui.TravelBuddyRoute
import com.example.travelbuddy.ui.composables.InputField
import com.example.travelbuddy.ui.composables.InputFieldType
import com.example.travelbuddy.ui.composables.TravelBuddyButton

@Composable
fun SignUpScreen(
    state: SignUpState,
    actions: SignUpActions,
    navController: NavController
) {
    Scaffold { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .padding(contentPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // TODO: Manca bottone per tornare indietro

            Text(
                text = "Travel Buddy",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.size(24.dp))

            InputField(
                value = state.firstName,
                label = "Nome",
                onValueChange = actions::setFirstName,
                leadingIcon = {
                    Icon(Icons.Outlined.Person, contentDescription = "Nome")
                }
            )
            Spacer(modifier = Modifier.size(16.dp))

            InputField(
                value = state.lastName,
                label = "Cognome",
                onValueChange = actions::setLastName,
                leadingIcon = {
                    Icon(Icons.Outlined.PersonOutline, contentDescription = "Cognome")
                }
            )
            Spacer(modifier = Modifier.size(16.dp))

            InputField(
                value = state.city,
                label = "Città",
                onValueChange = actions::setCity,
                leadingIcon = {
                    Icon(Icons.Outlined.LocationCity, contentDescription = "Città")
                }
            )
            Spacer(modifier = Modifier.size(16.dp))

            InputField(
                value = state.phoneNumber,
                label = "Numero di telefono",
                onValueChange = actions::setPhoneNumber,
                leadingIcon = {
                    Icon(Icons.Outlined.Phone, contentDescription = "Telefono")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(modifier = Modifier.size(16.dp))

            InputField(
                value = state.bio,
                label = "Bio",
                onValueChange = actions::setBio,
                leadingIcon = {
                    Icon(Icons.Outlined.Info, contentDescription = "Bio")
                }
            )
            Spacer(modifier = Modifier.size(16.dp))

            InputField(
                value = state.email,
                label = "Email",
                onValueChange = actions::setEmail,
                leadingIcon = {
                    Icon(Icons.Outlined.Email, contentDescription = "Email")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.size(16.dp))

            InputField(
                value = state.password,
                label = "Password",
                onValueChange = actions::setPassword,
                type = InputFieldType.Password,
                leadingIcon = {
                    Icon(Icons.Outlined.Lock, contentDescription = "Password")
                }
            )

            Spacer(modifier = Modifier.size(24.dp))

            TravelBuddyButton(
                label = "Sign up",
                onClick = { actions.signUp() },
                enabled = state.canSubmit,
                height = 50,
                isLoading = state.isLoading
            )

            Spacer(modifier = Modifier.size(32.dp))
        }
    }
}
