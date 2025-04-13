package com.example.travelbuddy.ui.screens.code

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
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
import com.example.travelbuddy.ui.composables.TravelBuddyButton

@Composable
fun CodeScreen(
    state: CodeState,
    actions: CodeActions,
    navController: NavController
) {
    Scaffold() { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .padding(contentPadding)
                .padding(12.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Secure Login",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.size(40.dp))
            TravelBuddyButton(
                label = "Use biometrics",
                onClick = { navController.navigate(TravelBuddyRoute.Home) },
            )
            Spacer(modifier = Modifier.size(20.dp))
            InputField(
                value = state.code,
                label = "Code",
                placeholder = "Enter 6-digit code",
                onValueChange = actions::setCode
            )
            Spacer(modifier = Modifier.size(30.dp))
            CustomKeypad(
                onNumberClick = {},
                onConfirm = {},
                onDelete = {}
            )
        }
    }
}