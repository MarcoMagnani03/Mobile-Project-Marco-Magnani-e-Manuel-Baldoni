package com.example.travelbuddy.data.network

import com.example.travelbuddy.data.models.EventsResponse
import com.example.travelbuddy.utils.parseDate
import com.example.travelbuddy.utils.transformDate
import com.google.android.gms.maps.model.LatLng
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Locale

class PredictHQApiService(private val client: HttpClient) {
    private val baseUrl = "https://api.predicthq.com/v1/events"

    suspend fun getEvents(
        location: LatLng,
        startDate: String? = "",
        endDate: String? = ""
    ): EventsResponse {
        try {
            println("--------- PARAMS: ${location.latitude}")
            println("--------- PARAMS: ${location.longitude}")
            println("--------- PARAMS: ${transformDate(startDate.toString())}")
            // First get the raw response to inspect
            val response = client.get(baseUrl) {
                parameter("within", "100km@${location.latitude},${location.longitude}")
                parameter("limit", 20)
                parameter("start.gte", transformDate(startDate.toString()))
                parameter("end.lte", transformDate(endDate.toString()))
            }

            // Log the raw response
            val rawResponse = response.bodyAsText()
            println("--------- RAW RESPONSE FROM PREDICTHQ: $rawResponse")

            try {
                // Now try to parse it
                val body: EventsResponse = response.body()
                println("--------- PARSED RESPONSE: $body")
                return body
            } catch (e: Exception) {
                println("--------- ERROR PARSING JSON: ${e.message}")
                println("--------- RESPONSE CONTENT TYPE: ${response.headers["Content-Type"]}")
                e.printStackTrace()

                // Try to create a minimal valid response if parsing fails
                return EventsResponse(0, emptyList())
            }
        } catch (e: Exception) {
            println("--------- ERROR MAKING API CALL: ${e.message}")
            e.printStackTrace()
            throw Exception("API call failed: ${e.javaClass.simpleName}: ${e.message}")
        }
    }
}