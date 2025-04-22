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
import androidx.compose.ui.graphics.ImageBitmap

@Composable
fun ProfileImageSection(
    profileImageBitmap: ImageBitmap? = null,
    profileImageUri: String? = null,
    isClickable: Boolean = true,
    onClick: () -> Unit = {}
) {
    val imageModifier = Modifier
        .size(140.dp)
        .clip(CircleShape)
        .then(if (isClickable) Modifier.clickable(onClick = onClick) else Modifier)
        .border(
            width = 2.dp,
            color = if (profileImageBitmap != null || !profileImageUri.isNullOrEmpty())
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surfaceVariant,
            shape = CircleShape
        )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        when {
            profileImageBitmap != null -> {
                Image(
                    bitmap = profileImageBitmap,
                    contentDescription = "Immagine profilo (bitmap)",
                    modifier = imageModifier,
                    contentScale = ContentScale.Crop,
                )
            }

            !profileImageUri.isNullOrEmpty() -> {
                AsyncImage(
                    model = profileImageUri,
                    contentDescription = "Immagine profilo (uri)",
                    modifier = imageModifier,
                    contentScale = ContentScale.Crop,
                )
            }

            else -> {
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
                        contentDescription = "Add photo",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset((-8).dp, (-8).dp)
        ) {
            if(isClickable){
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
}
