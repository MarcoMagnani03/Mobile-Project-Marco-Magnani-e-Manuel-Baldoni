package com.example.travelbuddy.data.models

import java.time.LocalDateTime

data class Event(
    val id: String,
    val title: String,
    val category: String,
    val description: String?,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime?,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val price: Double? = null,
    val currency: String? = "EUR",
    val attendeeCount: Int? = 0,
    val imageUrl: String? = null
)