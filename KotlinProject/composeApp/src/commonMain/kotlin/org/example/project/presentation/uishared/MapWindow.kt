package org.example.project.presentation.uishared

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Common interface for map display across platforms
 * 
 * @param latitude Center latitude
 * @param longitude Center longitude
 * @param zoom Zoom level (0-22, where 0 is world view)
 * @param markers List of map markers to display
 * @param modifier Compose modifier
 */
@Composable
expect fun MapWindow(
    latitude: Double,
    longitude: Double,
    zoom: Double = 12.0,
    markers: List<MapMarker> = emptyList(),
    modifier: Modifier = Modifier
)

/**
 * Represents a marker on the map
 */
data class MapMarker(
    val latitude: Double,
    val longitude: Double,
    val title: String,
    val description: String? = null
)
