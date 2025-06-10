package com.example.travelbuddy.data.repositories

import android.content.Context
import android.location.Geocoder
import com.example.travelbuddy.data.models.Event
import com.example.travelbuddy.data.network.PredictHQApiService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class EventsRepository(
    private val apiService: PredictHQApiService,
    private val context: Context
) {
    suspend fun getEvents(location: String, startDate: String?, endDate: String?): List<Event> = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val address = geocoder.getFromLocationName(location, 1)?.firstOrNull()

            if (address == null){
                throw Error("No address found")
            }

            val response = apiService.getEvents(
                location = LatLng(address.latitude, address.longitude),
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
            throw Exception("No events found")
        }
    }
}