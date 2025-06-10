package com.example.travelbuddy.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun transformDate(dateString: String): String {
    val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val date = LocalDate.parse(dateString, inputFormatter)
    return date.format(outputFormatter)
}

fun parseDate(dateString: String): Date {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return try {
        format.parse(dateString) ?: Date()
    } catch (e: Exception) {
        Date()
    }
}

fun parseDateTime(dateString: String): Date {
    val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return try {
        format.parse(dateString) ?: Date()
    } catch (e: Exception) {
        Date()
    }
}

fun formatTimeRange(startDate: String, endDate: String): String {
    val start = parseDate(startDate)
    val end = parseDate(endDate)
    val timeFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    return "${timeFormat.format(start)} - ${timeFormat.format(end)}"
}

fun formatDateToMonthDay(dateString: String): String {
    val date = parseDate(dateString)
    val format = SimpleDateFormat("MMM dd", Locale.getDefault())
    return format.format(date)
}

fun parseDateToMillis(dateString: String): Long {
    val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return format.parse(dateString)?.time ?: 0L
}

fun extractDay(dateString: String): String {
    val date = parseDate(dateString)
    val dayFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    return dayFormat.format(date)
}

fun formatDate(dateString: String): String {
    val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    return try {
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}