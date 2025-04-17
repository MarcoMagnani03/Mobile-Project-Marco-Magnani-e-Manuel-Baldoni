package com.example.travelbuddy.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.travelbuddy.R
import coil.compose.AsyncImage
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.outlined.Image

@Composable
fun ProfileImageSection(
    profileImageUri: String,
    onClick: () -> Unit
) {
    val imageModifier = Modifier
        .size(140.dp)
        .clip(CircleShape)
        .clickable(onClick = onClick)
        .border(
            width = 2.dp,
            color = if (profileImageUri.isEmpty())
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.primary,
            shape = CircleShape
        )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        if (profileImageUri.isNotEmpty()) {
            AsyncImage(
                model = profileImageUri,
                contentDescription = "Immagine profilo",
                modifier = imageModifier,
                contentScale = ContentScale.Crop,
            )
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = imageModifier
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    Icons.Outlined.Image,
                    contentDescription = "Aggiungi foto",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Icona camera sovrapposta
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset((-8).dp, (-8).dp)
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Cambia foto",
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    )
                    .padding(8.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}