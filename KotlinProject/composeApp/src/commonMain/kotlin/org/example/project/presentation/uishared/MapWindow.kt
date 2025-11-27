package org.example.project.presentation.uishared

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalTime

/**
 * Common interface for map display across platforms
 * 
 * @param latitude Center latitude
 * @param longitude Center longitude
 * @param zoom Zoom level (0-22, where 0 is world view)
 * @param markers List of map markers to display
 * @param routeEndpoints Optional pair of markers to show route between (if null, no route is shown)
 * @param onRouteCalculated Callback with distance (km), driving duration (minutes), and walking duration (minutes) when route is calculated
 * @param modifier Compose modifier
 */
@Composable
expect fun MapWindow(
    latitude: Double,
    longitude: Double,
    zoom: Double = 12.0,
    markers: List<MapMarker> = emptyList(),
    routeEndpoints: Pair<MapMarker, MapMarker>? = null,
    onRouteCalculated: ((distance: Double, drivingDuration: Double, walkingDuration: Double) -> Unit)? = null,
    modifier: Modifier = Modifier
)

/**
 * Represents a marker on the map
 */
data class MapMarker(
    val latitude: Double,
    val longitude: Double,
    val title: String,
    val description: String? = null,
    val address: String,
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null
)
