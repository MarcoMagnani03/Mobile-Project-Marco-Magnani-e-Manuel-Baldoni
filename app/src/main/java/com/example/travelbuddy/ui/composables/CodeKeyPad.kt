package com.example.travelbuddy.ui.composables


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomKeypad(
    onNumberClick: (Int) -> Unit,
    onDelete: () -> Unit,
    onBiometrics: () -> Unit,
    showBiometricButton: Boolean = true,
    modifier: Modifier = Modifier
) {
    val keyRows = listOf(
        listOf(Key.Number(1), Key.Number(2), Key.Number(3)),
        listOf(Key.Number(4), Key.Number(5), Key.Number(6)),
        listOf(Key.Number(7), Key.Number(8), Key.Number(9)),
        listOf(
            Key.Delete,
            Key.Number(0),
            if (showBiometricButton) Key.Biometrics else null
        )
    )

    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        keyRows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                row.forEach { key ->
                    if (key != null) {
                        KeyButton(key, onNumberClick, onDelete, onBiometrics)
                    } else {
                        Spacer(modifier = Modifier.size(100.dp))
                    }
                }
            }
        }
    }
}
@Composable
private fun KeyButton(
    key: Key,
    onNumberClick: (Int) -> Unit,
    onDelete: () -> Unit,
    onBiometrics: () -> Unit
) {
    val onClick = when (key) {
        is Key.Number -> { { onNumberClick(key.number) } }
        is Key.Delete -> onDelete
        is Key.Biometrics -> onBiometrics
    }

    val backgroundColor = when (key) {
        is Key.Delete -> MaterialTheme.colorScheme.error
        is Key.Biometrics -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.outline
    }

    val contentColor = when (key) {
        is Key.Delete -> MaterialTheme.colorScheme.onError
        is Key.Biometrics -> MaterialTheme.colorScheme.primary
        else -> Color.Black
    }

    Button(
        onClick = onClick,
        modifier = Modifier
            .width(110.dp)
            .height(80.dp),
        shape = RoundedCornerShape(36.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        )
    ) {
        when (key) {
            is Key.Number -> Text(
                text = key.number.toString(),
                fontSize = 24.sp  // Aumentato la dimensione del font
            )
            is Key.Delete -> Icon(
                imageVector = Icons.AutoMirrored.Outlined.Backspace,
                contentDescription = "Delete",
                modifier = Modifier.size(32.dp)  // Aumentato la dimensione dell'icona
            )
            is Key.Biometrics -> Icon(
                imageVector = Icons.Outlined.Fingerprint,
                contentDescription = "Biometrics",
                modifier = Modifier.size(36.dp)  // Aumentato significativamente
            )
        }
    }
}



// Sealed class to define the different key types
sealed class Key {
    data class Number(val number: Int) : Key()
    data object Delete : Key()
    data object Biometrics : Key()
}