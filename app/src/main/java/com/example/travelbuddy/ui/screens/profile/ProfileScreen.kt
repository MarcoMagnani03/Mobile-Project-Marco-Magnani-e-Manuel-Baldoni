package com.example.travelbuddy.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.travelbuddy.ui.composables.ButtonStyle
import com.example.travelbuddy.ui.composables.ProfileDetailItem
import com.example.travelbuddy.ui.composables.ProfileImageSection
import com.example.travelbuddy.ui.composables.TravelBuddyBottomBar
import com.example.travelbuddy.ui.composables.TravelBuddyButton
import com.example.travelbuddy.ui.composables.TravelBuddyTopBar
import com.example.travelbuddy.utils.ImageUtils.toImageBitmapOrNull


@Composable
fun ProfileScreen(
    state: ProfileState,
    actions: ProfileActions,
    navController: NavController
) {
    val scrollState = rememberScrollState()
    LaunchedEffect(Unit) {
        actions.loadUserData()
    }

    Scaffold(
        topBar = {
            TravelBuddyTopBar(
                navController,
                title = "Profile",
                subtitle = "Edit your information",
                canNavigateBack = true
            )
        },
        bottomBar = { TravelBuddyBottomBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ProfileImageSection(
                profileImageBitmap = state.profileBitmap.toImageBitmapOrNull(),
                isClickable = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = state.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = state.email,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            TravelBuddyButton(
                label = "Edit Profile",
                onClick = { actions.editProfile(navController) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(18.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Profile Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    ProfileDetailItem("Name", state.name)
                    ProfileDetailItem("Email", state.email)
                    ProfileDetailItem("Phone", state.phoneNumber)
                    ProfileDetailItem("Location", state.city)
                    ProfileDetailItem("Bio", state.bio)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            TravelBuddyButton(
                label = "Log Out",
                onClick = { actions.logout(navController) },
                style = ButtonStyle.ERROR,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Log out",
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }
    }
}


