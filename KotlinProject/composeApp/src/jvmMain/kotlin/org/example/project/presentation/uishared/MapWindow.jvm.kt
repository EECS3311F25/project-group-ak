package org.example.project.presentation.uishared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * JVM implementation - uses web view or shows placeholder
 * 
 * For production, you could use:
 * - JavaFX WebView to embed Mapbox GL JS
 * - Native Mapbox SDK for desktop
 * - Alternative mapping libraries
 */
@Composable
actual fun MapWindow(
    latitude: Double,
    longitude: Double,
    zoom: Double,
    markers: List<MapMarker>,
    routeEndpoints: Pair<MapMarker, MapMarker>?,
    onRouteCalculated: ((distance: Double, drivingDuration: Double, walkingDuration: Double) -> Unit)?,
    modifier: Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Map Window\nLat: $latitude, Lng: $longitude\nMarkers: ${markers.size}\nRoute: ${routeEndpoints != null}")
    }
}
