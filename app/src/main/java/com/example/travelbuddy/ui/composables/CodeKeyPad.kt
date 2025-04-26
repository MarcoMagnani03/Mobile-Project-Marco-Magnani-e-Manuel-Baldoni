package com.example.travelbuddy.ui.composables


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
    val keys = buildList {
        addAll((1..9).map { Key.Number(it) })
        add(Key.Delete)
        add(Key.Number(0))
        if (showBiometricButton) {
            add(Key.Biometrics)
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(keys) { key ->
            when (key) {
                is Key.Number -> {
                    Button(
                        onClick = { onNumberClick(key.number) },
                        modifier = Modifier
                            .size(72.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.outline,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(text = key.number.toString(),
                            fontSize = 20.sp)
                    }
                }

                is Key.Delete -> {
                    Button(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(72.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Outlined.Backspace, contentDescription = "Delete")
                    }
                }

                is Key.Biometrics -> {
                    Button(
                        onClick = onBiometrics,
                        modifier = Modifier
                            .size(72.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimary,
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Fingerprint,
                            contentDescription = "Use biometrics",
                            modifier = Modifier
                                .size(36.dp)
                        )
                    }
                }
            }
        }
    }
}

// Sealed class to define the different key types
sealed class Key {
    data class Number(val number: Int) : Key()
    data object Delete : Key()
    data object Biometrics : Key()
}