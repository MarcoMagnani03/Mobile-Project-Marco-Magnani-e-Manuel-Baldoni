package com.example.travelbuddy.data.repositories

import com.example.travelbuddy.data.models.Event
import com.example.travelbuddy.data.network.PredictHQApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EventsRepository(
    private val apiService: PredictHQApiService
) {
    suspend fun getEvents(location: String, startDate: String?, endDate: String?): List<Event> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getEvents(
                location = location,
                startDate = startDate,
                endDate = endDate
            )

            // Map API response to Event model
            return@withContext response.results!!.map { eventDto ->
                Event(
                    id = eventDto.id,
                    title = eventDto.title,
                    category = eventDto.category,
                    description = eventDto.description,
                    startDateTime = LocalDateTime.parse(eventDto.start, DateTimeFormatter.ISO_DATE_TIME),
                    endDateTime = eventDto.end?.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME) },
                    address = eventDto.geo?.address?.formatted_address.toString(),
                    latitude = eventDto.location?.get(0) ?: 0.0,
                    longitude = eventDto.location?.get(1) ?: 0.0,
                    price = eventDto.entities?.find { it.type == "price" }?.value?.toDoubleOrNull(),
                    currency = eventDto.entities?.find { it.type == "price" }?.currency ?: "EUR",
                    attendeeCount = eventDto.entities?.find { it.type == "attendance" }?.value?.toIntOrNull()
                )
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch events: ${e.message}")
        }
    }
}