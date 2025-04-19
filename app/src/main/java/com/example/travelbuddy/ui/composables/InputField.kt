package com.example.travelbuddy.ui.composables

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

sealed interface InputFieldType {
    @Serializable
    data object Text : InputFieldType
    @Serializable
    data object Password : InputFieldType
    @Serializable
    data object Date : InputFieldType
}

@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = "",
    isError: Boolean = false,
    placeholder: String? = "",
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    type: InputFieldType = InputFieldType.Text,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    dateFormat: String? = "dd/MM/yyyy"
) {
    val isPassword: Boolean = type == InputFieldType.Password
    val isDate: Boolean = type == InputFieldType.Date
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val dateFormatter = remember { DateTimeFormatter.ofPattern(dateFormat) }

    when {
        isDate -> {
            val currentDate = LocalDate.now()

            OutlinedTextField(
                value = value,
                onValueChange = { /* Non permettiamo input diretto */ },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = modifier
                    .fillMaxWidth()
                    .clickable {
                        // Mostra il DatePicker quando il campo viene cliccato
                        val datePicker = DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                                onValueChange(selectedDate.format(dateFormatter))
                            },
                            currentDate.year,
                            currentDate.monthValue - 1,
                            currentDate.dayOfMonth
                        )
                        datePicker.show()
                    },
                label = label?.let { { Text(text = it) } },
                placeholder = placeholder?.let { { Text(text = placeholder) } },
                isError = isError,
                leadingIcon = leadingIcon,
                textStyle = MaterialTheme.typography.bodyMedium,
                trailingIcon = {
                    IconButton(onClick = {
                        val datePicker = DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                                onValueChange(selectedDate.format(dateFormatter))
                            },
                            currentDate.year,
                            currentDate.monthValue - 1,
                            currentDate.dayOfMonth
                        )
                        datePicker.show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Select date"
                        )
                    }
                },
                readOnly = true // Campo in sola lettura perché usiamo il DatePicker
            )
        }
        else -> {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = modifier
                    .fillMaxWidth(),
                label = label?.let { { Text(text = it) } },
                placeholder = placeholder?.let { { Text(text = placeholder) } },
                isError = isError,
                leadingIcon = leadingIcon,
                textStyle = MaterialTheme.typography.bodyMedium,
                visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
                trailingIcon = {
                    when {
                        isPassword -> {
                            val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            val description = if (passwordVisible) "Hide password" else "Show password"

                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = icon, contentDescription = description)
                            }
                        }
                        trailingIcon != null -> trailingIcon()
                    }
                },
                keyboardOptions = keyboardOptions
            )
        }
    }
}