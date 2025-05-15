package com.example.travelbuddy.data.models

import kotlinx.serialization.Serializable

@Serializable
data class EventsResponse(
    val count: Int = 0,
    val results: List<EventDto>? = null
)

@Serializable
data class EventDto(
    val id: String,
    val title: String,
    val description: String? = null,
    val category: String,
    val start: String,
    val end: String? = null,
    val location: List<Double>? = null,
    val entities: List<Entity>? = null,
    val geo: Geo? = null,
)

@Serializable
data class Entity(
    val type: String,
    val name: String? = null,
    val value: String? = null,
    val currency: String? = null
)

@Serializable
data class Geo(
    val address: GeoAddress,
)

@Serializable
data class GeoAddress(
    val country_code: String? = "",
    val formatted_address: String? = "",
)