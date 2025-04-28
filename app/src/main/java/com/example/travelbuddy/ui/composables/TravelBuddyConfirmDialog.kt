package com.example.travelbuddy.ui.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun TravelBuddyConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmButtonText: String = "Confirm",
    dismissButtonText: String = "Cancel"
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
            }) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
            }) {
                Text(dismissButtonText)
            }
        }
    )
}
