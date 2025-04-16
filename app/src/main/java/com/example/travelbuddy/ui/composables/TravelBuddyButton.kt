package com.example.travelbuddy.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class ButtonStyle {
    PRIMARY,
    PRIMARY_OUTLINED
}

@Composable
fun TravelBuddyButton(
    label: String,
    onClick: () -> Unit,
    height: Int? = 50,
    enabled: Boolean? = true,
    style: ButtonStyle = ButtonStyle.PRIMARY,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isLoading: Boolean = false,
) {
    when (style) {
        ButtonStyle.PRIMARY -> {
            Button(
                onClick = { if (!isLoading) onClick() },
                enabled = (enabled ?: true) && !isLoading,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height((height ?: 50).dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    ButtonContent(
                        label = label,
                        textColor = MaterialTheme.colorScheme.onPrimary,
                        leadingIcon = leadingIcon,
                        trailingIcon = trailingIcon
                    )
                }
            }
        }

        ButtonStyle.PRIMARY_OUTLINED -> {
            OutlinedButton(
                onClick = { if (!isLoading) onClick() },
                enabled = (enabled ?: true) && !isLoading,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                    disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height((height ?: 50).dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    ButtonContent(
                        label = label,
                        textColor = MaterialTheme.colorScheme.primary,
                        leadingIcon = leadingIcon,
                        trailingIcon = trailingIcon
                    )
                }
            }
        }
    }
}

@Composable
private fun ButtonContent(
    label: String,
    textColor: Color,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Leading icon se presente
        leadingIcon?.let {
            it()
            Spacer(modifier = Modifier.width(8.dp))
        }

        // Label del bottone
        Text(
            text = label,
            color = textColor
        )

        // Trailing icon se presente
        trailingIcon?.let {
            Spacer(modifier = Modifier.width(8.dp))
            it()
        }
    }
}