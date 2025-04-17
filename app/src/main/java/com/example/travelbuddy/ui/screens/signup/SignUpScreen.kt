package com.example.travelbuddy.ui.screens.signup

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelbuddy.ui.composables.InputField
import com.example.travelbuddy.ui.composables.InputFieldType
import com.example.travelbuddy.ui.composables.TravelBuddyButton
import com.example.travelbuddy.utils.PermissionStatus
import com.example.travelbuddy.utils.rememberMultiplePermissions
import com.example.travelbuddy.ui.composables.ProfileImageSection

@Composable
fun SignUpScreen(
    state: SignUpState,
    actions: SignUpActions,
    navController: NavController
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val permissionHandler = rememberMultiplePermissions(
        permissions = listOf(
            Manifest.permission.CAMERA,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
        )
    ) { statuses ->
        if (statuses.all { it.value.isGranted }) {
            actions.showImagePicker(true)
        } else {
            if (statuses.any { it.value == PermissionStatus.PermanentlyDenied }) {
                actions.showPermissionRationale(true)
            }
        }
    }

    Scaffold { contentPadding ->

        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            ProfileImageSection(
                profileImageUri = state.profileImageUri,
                onClick = {
                    if (permissionHandler.statuses.all { it.value.isGranted }) {
                        actions.showImagePicker(true)
                    } else {
//                        viewModel.checkAndRequestPermissions(context, permissionHandler)
                    }
                }
            )

            if (state.permissionRationaleVisible) {
                LaunchedEffect(snackbarHostState) {
                    val res = snackbarHostState.showSnackbar(
                        "Location permission is required.",
                        "Go to Settings",
                        duration = SnackbarDuration.Long
                    )
                    if (res == SnackbarResult.ActionPerformed) {
                        context.startActivity(
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                        )
                    }
                    actions.showPermissionRationale(false)
                }
            }

            if (state.showImagePicker) {
                ImageSourceDialog(
                    onCameraSelected = { /* ... */ },
                    onGallerySelected = { /* ... */ },
                    onDismiss = { actions.showImagePicker(false) }
                )
            }
        }
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



            Spacer(modifier = Modifier.size(16.dp))

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


@Composable
private fun ImageSourceDialog(
    onCameraSelected: () -> Unit,
    onGallerySelected: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleziona origine") },
        text = {
            Column {
                ListItem(
                    headlineContent = { Text("Scatta una foto") },
                    leadingContent = { Icon(Icons.Filled.CameraAlt, "Camera") },
                    modifier = Modifier.clickable(onClick = onCameraSelected)
                )
                ListItem(
                    headlineContent = { Text("Scegli dalla galleria") },
                    leadingContent = { Icon(Icons.Filled.PhotoLibrary, "Photo gallery") },
                    modifier = Modifier.clickable(onClick = onGallerySelected)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Annulla") }
        }
    )
}