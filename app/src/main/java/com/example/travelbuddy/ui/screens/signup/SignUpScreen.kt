package com.example.travelbuddy.ui.screens.signup

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
import com.example.travelbuddy.utils.PermissionStatus
import com.example.travelbuddy.utils.rememberMultiplePermissions

@Composable
fun SignUpScreen(
    state: SignUpState,
    actions: SignUpActions,
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
            actions.showImagePicker(true)
        } else {
            if (statuses.any { it.value == PermissionStatus.PermanentlyDenied }) {
                actions.showPermissionRationale(true)
            } else {
                actions.showCameraPermissionDeniedAlert(true)
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
        actions.showImagePicker(false)
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
            Text(
                text = "Travel Buddy",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.size(24.dp))

            ProfileImageSection(
                profileImageUri = state.profileImageUri,
                onClick = {
                    if (cameraPermissionHandler.statuses.all { it.value.isGranted }) {
                        actions.showImagePicker(true)
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
                        actions.showImagePicker(false)
                        navController.navigate(TravelBuddyRoute.CameraCapture)
                    },
                    onGallerySelected = { galleryLauncher.launch("image/*") },
                    onDismiss = { actions.showImagePicker(false) }
                )
            }

            Spacer(modifier = Modifier.size(25.dp))

            InputField(
                value = state.firstName,
                label = "Nome",
                onValueChange = actions::setFirstName,
                leadingIcon = { Icon(Icons.Outlined.Person, null) }
            )

            Spacer(modifier = Modifier.size(16.dp))

            InputField(
                value = state.lastName,
                label = "Cognome",
                onValueChange = actions::setLastName,
                leadingIcon = { Icon(Icons.Outlined.PersonOutline, null) }
            )

            Spacer(modifier = Modifier.size(16.dp))

            InputField(
                value = state.city,
                label = "Città",
                onValueChange = actions::setCity,
                leadingIcon = { Icon(Icons.Outlined.LocationCity, null) }
            )

            Spacer(modifier = Modifier.size(16.dp))

            InputField(
                value = state.phoneNumber,
                label = "Numero di telefono",
                onValueChange = actions::setPhoneNumber,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                leadingIcon = { Icon(Icons.Outlined.Phone, null) }
            )

            Spacer(modifier = Modifier.size(16.dp))

            InputField(
                value = state.bio,
                label = "Bio",
                onValueChange = actions::setBio,
                leadingIcon = { Icon(Icons.Outlined.Info, null) }
            )

            Spacer(modifier = Modifier.size(16.dp))

            InputField(
                value = state.email,
                label = "Email",
                onValueChange = actions::setEmail,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                leadingIcon = { Icon(Icons.Outlined.Email, null) }
            )

            Spacer(modifier = Modifier.size(16.dp))

            InputField(
                value = state.password,
                label = "Password",
                onValueChange = actions::setPassword,
                type = InputFieldType.Password,
                leadingIcon = { Icon(Icons.Outlined.Lock, null) }
            )

            Spacer(modifier = Modifier.size(24.dp))

            TravelBuddyButton(
                label = "Sign up",
                onClick = actions::signUp,
                enabled = state.canSubmit,
                height = 50,
                isLoading = state.isLoading
            )

            Spacer(modifier = Modifier.size(32.dp))
        }

        // CAMERA SCREEN
        if (state.showCameraScreen) {
            CameraCaptureScreen(
                context = context,
                onImageCaptured = { uri, _ ->
                    actions.setPreviewImageUri(uri.toString())
                    actions.showCameraScreen(false)
                    actions.showImagePreview(true)
                },
                onError = { actions.showCameraScreen(false) },
                onClose = { actions.showCameraScreen(false) }
            )
        }

        // IMAGE PREVIEW
        if (state.showImagePreview && state.previewImageUri != null) {
            ImagePreviewScreen(
                imageUri = state.previewImageUri.toUri(),
                onConfirm = {
                    val bytes = ImageUtils.uriToByteArray(context, Uri.parse(state.previewImageUri))
                    actions.setPicture(bytes)
                    actions.showImagePreview(false)
                },
                onRetake = {
                    actions.showImagePreview(false)
                    actions.showCameraScreen(true)
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
                actions.showPermissionRationale(false)
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
                        actions.showCameraPermissionDeniedAlert(false)
                    }) {
                        Text("Grant")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { actions.showCameraPermissionDeniedAlert(false) }) {
                        Text("Dismiss")
                    }
                },
                onDismissRequest = { actions.showCameraPermissionDeniedAlert(false) }
            )
        }
    }
}