package com.example.travelbuddy.ui.composables

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.location.Geocoder
import java.util.Locale
import android.content.Context
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.platform.LocalContext
import com.example.travelbuddy.data.database.TripActivity
import com.google.maps.android.compose.MapUiSettings

data class MapLocation(
    val address: String,
    var latLng: LatLng? = null,
    val tripActivity: TripActivity? = null
)

/**
 * Composable principale che mostra una mappa con marker colorati
 * @param locations Lista di indirizzi da mostrare sulla mappa
 * @param onMarkerClick Callback per quando un marker viene cliccato
 */
@Composable
fun MapWithColoredMarkers(
    locations: List<MapLocation>,
    onMarkerClick: (MapLocation) -> Unit = {},
    selectedLocation: MapLocation? = null
) {
    val context = LocalContext.current

    var geocodedLocations by remember { mutableStateOf<List<MapLocation>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(locations) {
        val updatedLocations = withContext(Dispatchers.IO) {
            geocodeAddresses(locations, context)
        }
        geocodedLocations = updatedLocations

        updatedLocations.firstOrNull()?.latLng?.let { firstLocation ->
            cameraPositionState.position = CameraPosition.fromLatLngZoom(firstLocation, 12f)
        }

        isLoading = false
    }

    LaunchedEffect(selectedLocation) {
        if(selectedLocation != null){
            val updatedLocations = withContext(Dispatchers.IO) {
                geocodeAddresses(
                    listOf(
                        selectedLocation
                    ) as List<MapLocation>, context)
            }
            geocodedLocations = updatedLocations

            updatedLocations.firstOrNull()?.latLng?.let { firstLocation ->
                cameraPositionState.position = CameraPosition.fromLatLngZoom(firstLocation, 12f)
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
    ) {
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .height(300.dp),
            cameraPositionState = cameraPositionState,
        ) {
            geocodedLocations.forEach { location ->
                location.latLng?.let { position ->
                    Marker(
                        state = MarkerState(position = position),
                        snippet = "Lat: ${position.latitude}, Lng: ${position.longitude}",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                        title = location.tripActivity?.name,
                        onClick = {
                            onMarkerClick(location)
                            false
                        }
                    )
                }
            }
        }
    }
}

suspend fun geocodeAddresses(
    locations: List<MapLocation>,
    context: Context
): List<MapLocation> = withContext(Dispatchers.IO) {
    val geocoder = Geocoder(context, Locale.getDefault())

    locations.map { location ->
        if (location.latLng != null) {
            return@map location
        }

        try {
            val address = geocoder.getFromLocationName(location.address, 1)?.firstOrNull()

            if (address != null) {
                location.copy(
                    latLng = LatLng(address.latitude, address.longitude)
                )
            } else {
                location
            }
        } catch (e: Exception) {
            e.printStackTrace()
            location
        }
    }
}