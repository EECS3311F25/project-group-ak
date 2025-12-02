package org.example.project.presentation.calendar.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.example.project.presentation.uishared.MapWindow
import org.example.project.presentation.uishared.MapMarker
import kotlin.math.*

// Expect function for platform-specific bottom sheet rendering
@Composable
expect fun RenderBottomSheetOverlay(
    startTitle: String,
    startAddress: String,
    endTitle: String,
    endAddress: String,
    distance: Double,
    drivingDuration: Double,
    walkingDuration: Double?,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    onCleanup: () -> Unit
)

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
    var routeDistance by remember { mutableStateOf<Double?>(null) }
    var routeDrivingDuration by remember { mutableStateOf<Double?>(null) }
    var routeWalkingDuration by remember { mutableStateOf<Double?>(null) }
    var isBottomSheetExpanded by remember { mutableStateOf(false) }
    
    // Calculate center point between the two locations
    val centerLat = (startLocation.latitude + endLocation.latitude) / 2
    val centerLng = (startLocation.longitude + endLocation.longitude) / 2
    
    // Calculate zoom level to fit both markers
    val latDiff = abs(startLocation.latitude - endLocation.latitude)
    val lngDiff = abs(startLocation.longitude - endLocation.longitude)
    val maxDiff = max(latDiff, lngDiff)

    // Rough zoom calculation (smaller zoom for larger spread)
    var zoom = when {
        maxDiff > 30 -> 2.5
        maxDiff > 5.0 -> 7.5
        maxDiff > 1.0 -> 9.5
        maxDiff > 0.2 -> 11.0
        else -> 13.0
    }

    // If the true distance is long, force zoom out further
    val distanceKm = haversineDistanceKm(startLocation.latitude, startLocation.longitude, endLocation.latitude, endLocation.longitude)
    if (distanceKm > 500) zoom = min(7.0, zoom)
    if (distanceKm > 1500) zoom = min(5.0, zoom)
    if (distanceKm > 3500) zoom = min(3.5, zoom)
    
    Box(modifier = modifier.fillMaxSize()) {
        // Map - full screen
        MapWindow(
            latitude = centerLat,
            longitude = centerLng,
            zoom = zoom,
            markers = listOf(startLocation, endLocation),
            routeEndpoints = Pair(startLocation, endLocation),
            onRouteCalculated = { distance, drivingDuration, walkingDuration ->
                routeDistance = distance
                routeDrivingDuration = drivingDuration
                routeWalkingDuration = walkingDuration?.takeIf { it <= 120.0 }
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Back button - DOM-based overlay for web, Compose for JVM
        RenderBackButtonOverlay(
            onBack = { component.onEvent(NavigationViewEvent.Close) },
            onCleanup = { }
        )
        
        // Collapsible bottom sheet - overlay layer (rendered after map, so appears on top)
        if (routeDistance != null && routeDrivingDuration != null) {
            RenderBottomSheetOverlay(
                startTitle = startLocation.title,
                startAddress = startLocation.address,
                endTitle = endLocation.title,
                endAddress = endLocation.address,
                distance = routeDistance!!,
                drivingDuration = routeDrivingDuration!!,
                walkingDuration = routeWalkingDuration,
                isExpanded = isBottomSheetExpanded,
                onExpandToggle = { isBottomSheetExpanded = !isBottomSheetExpanded },
                onCleanup = { }
            )
        }
    }
}

private fun haversineDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371.0 // Earth radius km
    val dLat = degToRad(lat2 - lat1)
    val dLon = degToRad(lon2 - lon1)
    val a = sin(dLat / 2).pow(2.0) + cos(degToRad(lat1)) * cos(degToRad(lat2)) * sin(dLon / 2).pow(2.0)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return R * c
}

private fun degToRad(value: Double): Double = value * PI / 180.0
