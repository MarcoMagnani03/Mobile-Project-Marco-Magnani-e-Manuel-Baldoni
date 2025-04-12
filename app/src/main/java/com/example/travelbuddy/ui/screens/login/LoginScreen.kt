package com.example.travelbuddy.ui.screens.login

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelbuddy.ui.TravelBuddyRoute
import com.example.travelbuddy.ui.composables.InputField
import com.example.travelbuddy.ui.composables.InputFieldType

@Composable
fun LoginScreen(
    state: LoginState,
    actions: LoginActions,
    navController: NavController
) {
    Scaffold() { contentPadding ->
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
                onValueChange = actions::setEmail,
                placeholder = "Email",
                leadingIcon = { Icon(
                    Icons.Outlined.Email,
                    contentDescription = "email icon"
                ) },
            )
            Spacer(modifier = Modifier.size(30.dp))
            InputField(
                value = state.password,
                onValueChange = actions::setPassword,
                placeholder = "Password",
                type = InputFieldType.Password,
                leadingIcon = { Icon(
                    Icons.Outlined.Lock,
                    contentDescription = "password icon"
                ) },
            )
            Spacer(modifier = Modifier.size(30.dp))
            Button(
                onClick = { navController.navigate(TravelBuddyRoute.Home) },
                enabled = state.canSubmit,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    "Login"
                )
            }
        }
    }
}