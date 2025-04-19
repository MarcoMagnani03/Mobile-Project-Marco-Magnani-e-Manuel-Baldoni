package com.example.travelbuddy.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.travelbuddy.ui.composables.ButtonStyle
import com.example.travelbuddy.ui.composables.ProfileImageSection
import com.example.travelbuddy.ui.composables.TravelBuddyButton
import com.example.travelbuddy.ui.composables.TravelBuddyTopBar
import com.example.travelbuddy.utils.ImageUtils.toImageBitmapOrNull


@Composable
fun ProfileScreen(
    state: ProfileState,
    actions: ProfileActions,
    navController: NavController
) {
    LaunchedEffect(Unit) {
        actions.loadUserData()
        println(state.profileImage)
        println(state.profileImage.toImageBitmapOrNull())
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TravelBuddyTopBar(navController,"Profile", "Edit your information", canNavigateBack = true)
        ProfileImageSection(
            profileImageBitmap = state.profileImage.toImageBitmapOrNull(),
            onClick = { }
        )


        Spacer(modifier = Modifier.height(16.dp))

        // Name
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
            onClick = { /* Naviga alla schermata di modifica profilo */ },
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
            modifier = Modifier.fillMaxWidth()
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
                ProfileDetailItem("Phone", state.phone)
                ProfileDetailItem("Location", state.city)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        TravelBuddyButton(
            label = "Log Out",
            onClick = { /* Gestisci logout */ },
            style = ButtonStyle.ERROR,
            leadingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Edit",
                    modifier = Modifier.size(18.dp)
                )
            }
        )
    }
}

@Composable
fun ProfileDetailItem(label: String, value: String?) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = if (!value.isNullOrBlank()) value else "undefined",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}