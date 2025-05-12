package com.example.travelbuddy.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class TravelBuddyDropDownMenuItem (
    val text: @Composable () -> Unit,
    val onClick: () -> Unit,
    val leadingIcon: @Composable (() -> Unit)? = null,
    val trailingIcon: @Composable (() -> Unit)? = null,
    val color: Color? = null
)

@Composable
fun TravelBuddyDropDownMenu(
    menuItems: List<TravelBuddyDropDownMenuItem> = emptyList()
){
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopEnd)
            .padding(4.dp),
    ) {
        IconButton(
            onClick = { expanded = true },
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            menuItems.map { item ->
                DropdownMenuItem(
                    onClick = {
                        item.onClick()
                        expanded = false
                    },
                    text = item.text,
                    leadingIcon = item.leadingIcon,
                    trailingIcon = item.trailingIcon,
                    colors = MenuItemColors(
                        textColor = item.color ?: MaterialTheme.colorScheme.onSurface,
                        leadingIconColor = item.color ?: MaterialTheme.colorScheme.onSurface,
                        trailingIconColor = item.color ?: MaterialTheme.colorScheme.onSurface,
                        disabledTextColor = Color.LightGray,
                        disabledLeadingIconColor = Color.LightGray,
                        disabledTrailingIconColor = Color.LightGray,
                    )
                )
            }
        }
    }
}