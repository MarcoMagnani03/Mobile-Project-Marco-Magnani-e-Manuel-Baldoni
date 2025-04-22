package com.example.travelbuddy.ui.screens.profile

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.travelbuddy.ui.TravelBuddyRoute
import com.example.travelbuddy.ui.composables.*
import com.example.travelbuddy.ui.screens.camera.CameraCaptureScreen
import com.example.travelbuddy.ui.screens.camera.ImagePreviewScreen
import com.example.travelbuddy.utils.ImageUtils
import com.example.travelbuddy.utils.ImageUtils.toImageBitmapOrNull
import com.example.travelbuddy.utils.PermissionStatus
import com.example.travelbuddy.utils.rememberMultiplePermissions

@Composable
fun EditProfileScreen(
    state: ProfileState,
    actions: ProfileActions,
    navController: NavController
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val cameraPermissionHandler = rememberMultiplePermissions(
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
            actions.toggleUIControl(ProfileControl.ImagePicker, true)
        } else {
            if (statuses.any { it.value == PermissionStatus.PermanentlyDenied }) {
                actions.toggleUIControl(ProfileControl.CameraRationale, true)
            } else {
                actions.toggleUIControl(ProfileControl.CameraDenied, true)
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bytes = ImageUtils.uriToByteArray(context, it)
            actions.setPicture(bytes)
            actions.setPictureUri(it.toString())
        }
        actions.toggleUIControl(ProfileControl.ImagePicker, false)
    }

    val scrollState = rememberScrollState()

    Scaffold { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TravelBuddyTopBar(
                navController,
                "Edit Profile",
                "Edit profile informations",
                canNavigateBack = true
            )

            ProfileImageSection(
                profileImageBitmap = state.picture
                    ?.let { ImageUtils.byteArrayToOrientedBitmap(it).toImageBitmapOrNull() }
                    ?: state.profileBitmap?.toImageBitmapOrNull(),
                onClick = {
                    if (cameraPermissionHandler.statuses.all { it.value.isGranted }) {
                        actions.toggleUIControl(ProfileControl.ImagePicker, true)
                    } else {
                        cameraPermissionHandler.launchPermissionRequest()
                    }
                }
            )


            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Select a profile image",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge
            )

            if (state.showImagePicker) {
                ImageSourceDialog(
                    onCameraSelected = {
                        actions.toggleUIControl(ProfileControl.ImagePicker, false)
                        navController.navigate(TravelBuddyRoute.CameraCapture)
                    },
                    onGallerySelected = { galleryLauncher.launch("image/*") },
                    onDismiss = { actions.toggleUIControl(ProfileControl.ImagePicker, false) }
                )
            }

            Spacer(modifier = Modifier.size(25.dp))

            InputField(
                value = state.firstName,
                label = "First Name",
                onValueChange = actions.updateFieldFor(ProfileField.FirstName),
                leadingIcon = { Icon(Icons.Outlined.Person, "FirstName") }
            )

            Spacer(modifier = Modifier.size(16.dp))

            InputField(
                value = state.lastName,
                label = "Surname",
                onValueChange = actions.updateFieldFor(ProfileField.LastName),
                leadingIcon = { Icon(Icons.Outlined.PersonOutline, "LastName") }
            )

            Spacer(modifier = Modifier.size(16.dp))

            InputField(
                value = state.city,
                label = "City",
                onValueChange = actions.updateFieldFor(ProfileField.City),
                leadingIcon = { Icon(Icons.Outlined.LocationCity, "Location") }
            )

            Spacer(modifier = Modifier.size(16.dp))

            InputField(
                value = state.phoneNumber,
                label = "Phone number",
                onValueChange = actions.updateFieldFor(ProfileField.Phone),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                leadingIcon = { Icon(Icons.Outlined.Phone, "Phone") }
            )

            Spacer(modifier = Modifier.size(16.dp))

            InputField(
                value = state.bio,
                label = "Bio",
                onValueChange = actions.updateFieldFor(ProfileField.Bio),
                leadingIcon = { Icon(Icons.Outlined.Info, "Bio") }
            )

            Spacer(modifier = Modifier.size(16.dp))

            InputField(
                value = state.email,
                label = "Email",
                onValueChange = actions.updateFieldFor(ProfileField.Email),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                leadingIcon = { Icon(Icons.Outlined.Email, "Email") }
            )

            Spacer(modifier = Modifier.size(24.dp))

            TravelBuddyButton(
                label = "Edit user",
                onClick = {actions.submitProfileChanges(navController)},
                enabled = state.canSubmit,
                height = 50,
                isLoading = state.isLoading
            )

            state.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Spacer(modifier = Modifier.size(32.dp))
        }

        // CAMERA SCREEN
        if (state.showCameraScreen) {
            CameraCaptureScreen(
                context = context,
                onImageCaptured = { uri, _ ->
                    actions.setPreviewImageUri(uri.toString())
                    actions.toggleUIControl(ProfileControl.CameraScreen, false)
                    actions.toggleUIControl(ProfileControl.ImagePreview, true)
                },
                onError = { actions.toggleUIControl(ProfileControl.CameraScreen, false) },
                onClose = { actions.toggleUIControl(ProfileControl.CameraScreen, false) }
            )
        }

        // IMAGE PREVIEW
        if (state.showImagePreview && state.previewImageUri != null) {
            ImagePreviewScreen(
                imageUri = state.previewImageUri.toUri(),
                onConfirm = {
                    val bytes = ImageUtils.uriToByteArray(context, state.previewImageUri.toUri())
                    actions.setPicture(bytes)
                    actions.toggleUIControl(ProfileControl.ImagePreview, false)
                },
                onRetake = {
                    actions.toggleUIControl(ProfileControl.ImagePreview, false)
                    actions.toggleUIControl(ProfileControl.CameraScreen, true)
                }
            )
        }

        val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
        val newImageBytes = savedStateHandle?.get<ByteArray>("imageBytes")
        val confirmedImageUri = savedStateHandle?.get<String>("confirmedImageUri")

        LaunchedEffect(newImageBytes, confirmedImageUri) {
            if (newImageBytes != null && confirmedImageUri != null) {
                actions.setPicture(newImageBytes)
                actions.setPictureUri(confirmedImageUri)

                ImageUtils.saveImageToGallery(context, newImageBytes)

                savedStateHandle.remove<ByteArray>("imageBytes")
                savedStateHandle.remove<String>("confirmedImageUri")
                savedStateHandle.remove<String>("previewImageUri")
            }
        }

        if (state.permissionRationaleVisible) {
            LaunchedEffect(snackbarHostState) {
                val res = snackbarHostState.showSnackbar(
                    "Camera permission is required.",
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
                actions.toggleUIControl(ProfileControl.CameraRationale, false)
            }
        }

        // CAMERA DENIED ALERT
        if (state.permissionCameraDeniedVisible) {
            AlertDialog(
                title = { Text("Camera permission denied") },
                text = { Text("Camera and gallery permission is required.") },
                confirmButton = {
                    TextButton(onClick = {
                        cameraPermissionHandler.launchPermissionRequest()
                        actions.toggleUIControl(ProfileControl.CameraDenied, false)
                    }) {
                        Text("Grant")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { actions.toggleUIControl(ProfileControl.CameraDenied, false)}) {
                        Text("Dismiss")
                    }
                },
                onDismissRequest = { actions.toggleUIControl(ProfileControl.CameraDenied, false)}
            )
        }
    }
}