package org.example.project.presentation.calendar.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.presentation.map.MapView
import org.example.project.presentation.map.MapMarker
import kotlin.math.*

/**
 * Navigation view to display route between two locations
 * 
 * @param component The NavigationViewComponent for handling events
 * @param startLocation Starting point marker
 * @param endLocation Destination point marker
 */
@Composable
fun NavigationView(
    component: NavigationViewComponent,
    startLocation: MapMarker,
    endLocation: MapMarker,
    modifier: Modifier = Modifier
) {
    // Calculate center point between the two locations
    val centerLat = (startLocation.latitude + endLocation.latitude) / 2
    val centerLng = (startLocation.longitude + endLocation.longitude) / 2
    
    // Calculate zoom level to fit both markers
    val latDiff = abs(startLocation.latitude - endLocation.latitude)
    val lngDiff = abs(startLocation.longitude - endLocation.longitude)
    val maxDiff = max(latDiff, lngDiff)
    
    // Rough zoom calculation (you can adjust these values)
    val zoom = when {
        maxDiff > 0.5 -> 8.0
        maxDiff > 0.1 -> 10.0
        maxDiff > 0.05 -> 11.0
        maxDiff > 0.01 -> 13.0
        else -> 14.0
    }
    
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with close button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Navigation",
                style = MaterialTheme.typography.headlineMedium,
            )
            IconButton(onClick = { component.onEvent(NavigationViewEvent.Close) }) {
                Text("✕", style = MaterialTheme.typography.headlineSmall)
            }
        }
        
        // Map showing both locations
        MapView(
            latitude = centerLat,
            longitude = centerLng,
            zoom = zoom,
            markers = listOf(startLocation, endLocation),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}
