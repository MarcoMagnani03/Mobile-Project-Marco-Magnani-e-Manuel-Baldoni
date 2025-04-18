package com.example.travelbuddy.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ImageSourceDialog(
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
                    headlineContent = { Text("Take a photo") },
                    leadingContent = { Icon(Icons.Filled.CameraAlt, "Camera") },
                    modifier = Modifier.clickable(onClick = onCameraSelected)
                )
                ListItem(
                    headlineContent = { Text("Choose from gallery") },
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