package com.example.travelbuddy.ui.screens.tripPhotos

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.travelbuddy.data.database.Photo
import com.example.travelbuddy.ui.TravelBuddyRoute
import com.example.travelbuddy.ui.composables.ImageSourceDialog
import com.example.travelbuddy.ui.composables.TravelBuddyBottomBar
import com.example.travelbuddy.ui.composables.TravelBuddyButton
import com.example.travelbuddy.ui.composables.TravelBuddyTopBar
import com.example.travelbuddy.ui.screens.camera.CameraCaptureScreen
import com.example.travelbuddy.ui.screens.camera.ImagePreviewScreen
import com.example.travelbuddy.utils.ImageUtils
import com.example.travelbuddy.utils.PermissionStatus
import com.example.travelbuddy.utils.rememberMultiplePermissions
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripPhotosScreen(
    state: TripPhotosState,
    actions: TripPhotosActions,
    navController: NavController
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedPhoto by remember { mutableStateOf<Photo?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPhotoDetailDialog by remember { mutableStateOf(false) }
    var showBulkDeleteDialog by remember { mutableStateOf(false) }

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
            actions.addPhoto(bytes)
        }
        actions.showImagePicker(false)
    }

    // Delete confirmation dialog
    if (showDeleteDialog && selectedPhoto != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Photo") },
            text = { Text("Are you sure you want to delete this photo?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedPhoto?.let { actions.deletePhoto(it) }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Bulk delete confirmation dialog
    if (showBulkDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showBulkDeleteDialog = false },
            title = { Text("Delete Photos") },
            text = { Text("Are you sure you want to delete ${state.selectedPhotos.size} selected photos?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        actions.deleteSelectedPhotos()
                        showBulkDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBulkDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Photo detail dialog
    if (showPhotoDetailDialog && selectedPhoto != null) {
        AlertDialog(
            onDismissRequest = { showPhotoDetailDialog = false },
            title = { Text("Photo Details") },
            text = {
                Column {
                    selectedPhoto?.file?.let { photoBytes ->
                        Image(
                            bitmap = ImageUtils.byteArrayToOrientedBitmap(photoBytes).asImageBitmap(),
                            contentDescription = "Photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Date: ${selectedPhoto?.creationDate}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPhotoDetailDialog = false
                        showDeleteDialog = true
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPhotoDetailDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Image source dialog
    if (state.showImagePicker) {
        ImageSourceDialog(
            onCameraSelected = {
                actions.showImagePicker(false)
                actions.showCameraScreen(true)
            },
            onGallerySelected = {
                galleryLauncher.launch("image/*")
            },
            onDismiss = { actions.showImagePicker(false) }
        )
    }

    Scaffold(
        topBar = {
            if (state.isSelectionMode) {
                // Selection mode top bar
                TopAppBar(
                    title = { Text("${state.selectedPhotos.size} selected", style = MaterialTheme.typography.headlineLarge) },
                    navigationIcon = {
                        IconButton(onClick = { actions.clearSelection() }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear selection")
                        }
                    },
                    actions = {
                        IconButton(onClick = { actions.selectAllPhotos() }) {
                            Icon(Icons.Default.SelectAll, contentDescription = "Select all")
                        }
                        if (state.selectedPhotos.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    // Handle saving in UI layer
                                    val selectedPhotos = state.photos.filter { photo ->
                                        photo.id?.let { state.selectedPhotos.contains(it) } == true
                                    }
                                    val imageBytesList = selectedPhotos.mapNotNull { it.file }
                                    val successCount = ImageUtils.saveImagesToGallery(context, imageBytesList)

                                    // Clear selection after saving
                                    actions.clearSelection()

                                    // Show result
                                    if (successCount > 0) {
                                        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                            snackbarHostState.showSnackbar(
                                                message = "$successCount photos saved to gallery",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                },
                                enabled = !state.isSavingToGallery
                            ) {
                                Icon(Icons.Default.Download, contentDescription = "Save to gallery")
                            }
                            IconButton(onClick = { showBulkDeleteDialog = true }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete selected")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            } else {
                TravelBuddyTopBar(
                    navController = navController,
                    title = "Trip Photos",
                    canNavigateBack = true,
                )
            }
        },
        bottomBar = {
            if (!state.isSelectionMode) {
                TravelBuddyBottomBar(navController)
            }
        },
        floatingActionButton = {
            if (!state.isSelectionMode) {
                FloatingActionButton(
                    onClick = {
                        if (cameraPermissionHandler.statuses.all { it.value.isGranted }) {
                            actions.showImagePicker(true)
                        } else {
                            cameraPermissionHandler.launchPermissionRequest()
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add photo",
                        tint = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (state.photos.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "No photos yet",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Tap the + button to add photos",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item(span = { GridItemSpan(currentLineSpan = 3) }) {
                        TravelBuddyButton(
                            label = "Select photos",
                            onClick = { actions.toggleSelectionMode() }
                        )
                    }

                    items(state.photos) { photo ->
                        PhotoItem(
                            photo = photo,
                            isSelected = photo.id?.let { state.selectedPhotos.contains(it) } ?: false,
                            isSelectionMode = state.isSelectionMode,
                            onPhotoClick = {
                                if (state.isSelectionMode) {
                                    photo.id?.let { actions.togglePhotoSelection(it) }
                                } else {
                                    selectedPhoto = photo
                                    showPhotoDetailDialog = true
                                }
                            },
                            onDeleteClick = {
                                selectedPhoto = photo
                                showDeleteDialog = true
                            },
                            onLongClick = {
                                if (!state.isSelectionMode) {
                                    actions.toggleSelectionMode()
                                    photo.id?.let { actions.togglePhotoSelection(it) }
                                }
                            }
                        )
                    }
                }
            }

            state.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }
        }
    }

    // Handle save to gallery completion
    LaunchedEffect(state.isSavingToGallery) {
        if (!state.isSavingToGallery && state.selectedPhotos.isEmpty() && !state.isSelectionMode) {
            snackbarHostState.showSnackbar(
                message = "Photos saved to gallery",
                duration = SnackbarDuration.Short
            )
        }
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
                val bytes = ImageUtils.uriToByteArray(context, state.previewImageUri.toUri())
                actions.addPhoto(bytes)
                ImageUtils.saveImageToGallery(context, bytes)
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
            actions.addPhoto(newImageBytes)
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
            text = { Text("Camera and gallery permission is required to add photos.") },
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

@Composable
fun PhotoItem(
    photo: Photo,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onPhotoClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onPhotoClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            photo.file?.let { photoBytes ->
                Image(
                    bitmap = ImageUtils.byteArrayToOrientedBitmap(photoBytes).asImageBitmap(),
                    contentDescription = "Photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } ?: Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text("No image")
            }

            // Selection indicator
            if (isSelectionMode) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(24.dp)
                        .background(
                            color = if (isSelected)
                                MaterialTheme.colorScheme.primary
                            else
                                Color.White.copy(alpha = 0.7f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            } else {
                // Delete button in the top-right corner (only when not in selection mode)
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(32.dp)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete photo",
                        tint = Color.White
                    )
                }
            }

            // Date indicator at the bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(4.dp)
            ) {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = try {
                    val fullDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val date = fullDateFormat.parse(photo.creationDate)
                    date?.let { dateFormat.format(it) } ?: photo.creationDate
                } catch (e: Exception) {
                    photo.creationDate
                }

                Text(
                    text = formattedDate,
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}