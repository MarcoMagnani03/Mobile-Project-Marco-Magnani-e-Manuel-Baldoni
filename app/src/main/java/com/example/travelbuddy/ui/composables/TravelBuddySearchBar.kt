package com.example.travelbuddy.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun TravelBuddySearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Search...",
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector = Icons.Default.Search
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = "Search Icon"
            )
        },
        placeholder = { Text(placeholder) },
        singleLine = true,
        shape = MaterialTheme.shapes.large,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}
