package com.example.travelbuddy.ui.composables

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

sealed interface InputFieldType {
    @Serializable
    data object Text : InputFieldType
    @Serializable
    data object Password : InputFieldType
    @Serializable
    data object Date : InputFieldType
    @Serializable
    data object DateTime : InputFieldType
    @Serializable
    data object Select : InputFieldType
    @Serializable
    data object Position : InputFieldType
}

/**
 * Data class representing a select option with a label and value.
 * @param label The human-readable text shown in the dropdown
 * @param value The actual value used in code
 */
data class SelectOption(
    val label: String,
    val value: String
)

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
    dateFormat: String = "dd/MM/yyyy",
    dateTimeFormat: String = "dd/MM/yyyy HH:mm",
    options: List<SelectOption> = emptyList(), // Options for Select type with label-value pairs
) {
    val isPassword: Boolean = type == InputFieldType.Password
    val isDate: Boolean = type == InputFieldType.Date
    val isDateTime: Boolean = type == InputFieldType.DateTime
    val isSelect: Boolean = type == InputFieldType.Select
    var passwordVisible by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) } // For dropdown menu

    val context = LocalContext.current
    val dateFormatter = remember { DateTimeFormatter.ofPattern(dateFormat) }
    val dateTimeFormatter = remember { DateTimeFormatter.ofPattern(dateTimeFormat) }

    // Find the label to display for the current value in Select mode
    val displayLabel = if (isSelect) {
        options.find { it.value == value }?.label ?: placeholder ?: ""
    } else {
        placeholder ?: ""
    }

    when {
        isSelect -> {
            Box(
                modifier = modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = displayLabel,
                    onValueChange = { /* Non permettiamo input diretto */ },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true },
                    label = label?.let { { Text(text = it) } },
                    placeholder = placeholder?.let { { Text(text = placeholder) } },
                    isError = isError,
                    leadingIcon = leadingIcon,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select option"
                            )
                        }
                    },
                    readOnly = true
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(text = option.label) },
                            onClick = {
                                onValueChange(option.value)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
        isDateTime -> {
            val currentDateTime = LocalDateTime.now()

            OutlinedTextField(
                value = value,
                onValueChange = { /* Non permettiamo input diretto */ },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .then(modifier)
                    .clickable {
                        showDateTimePicker(
                            context,
                            currentDateTime,
                            dateTimeFormatter,
                            onValueChange
                        )
                    },
                label = label?.let { { Text(text = it) } },
                placeholder = placeholder?.let { { Text(text = placeholder) } },
                isError = isError,
                leadingIcon = leadingIcon,
                textStyle = MaterialTheme.typography.bodyMedium,
                trailingIcon = {
                    IconButton(onClick = {
                        showDateTimePicker(
                            context,
                            currentDateTime,
                            dateTimeFormatter,
                            onValueChange
                        )
                    }) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Select date and time"
                        )
                    }
                },
                readOnly = true
            )
        }
        isDate -> {
            val currentDate = LocalDate.now()

            OutlinedTextField(
                value = value,
                onValueChange = { /* Non permettiamo input diretto */ },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .then(modifier)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .then(modifier),
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

private fun showDateTimePicker(
    context: android.content.Context,
    initialDateTime: LocalDateTime,
    formatter: DateTimeFormatter,
    onValueSelected: (String) -> Unit
) {
    // First show the date picker
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            // After date is selected, show time picker
            val timePicker = TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    // When both date and time are selected, format and return the complete datetime
                    val selectedDateTime = LocalDateTime.of(
                        year,
                        month + 1,
                        dayOfMonth,
                        hourOfDay,
                        minute
                    )
                    onValueSelected(selectedDateTime.format(formatter))
                },
                initialDateTime.hour,
                initialDateTime.minute,
                true // 24-hour format
            )
            timePicker.show()
        },
        initialDateTime.year,
        initialDateTime.monthValue - 1,
        initialDateTime.dayOfMonth
    )
    datePicker.show()
}