package com.example.travelbuddy.ui.composables

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp

@Composable
fun CopyToClipboardButton(
    textToCopy: String,
    color: Color
) {
    val context = LocalContext.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    Button(
        onClick = {
            clipboardManager.setText(AnnotatedString(textToCopy))
            copied = true
            Toast.makeText(context, "Location copied", Toast.LENGTH_SHORT).show()
        },
        colors = ButtonColors(
            containerColor = Color.Transparent,
            contentColor = color,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = MaterialTheme.colorScheme.primary,
        ),
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier
            .padding(0.dp)
            .height(20.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = "Copy text",
            tint = color,
            modifier = Modifier.size(14.dp).padding(end = 4.dp)
        )
        Text("Copy location")
    }
}
