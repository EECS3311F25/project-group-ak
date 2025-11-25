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
    walkingDuration: Double,
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
    
    // Rough zoom calculation (you can adjust these values)
    val zoom = when {
        maxDiff > 0.5 -> 8.0
        maxDiff > 0.1 -> 10.0
        maxDiff > 0.05 -> 11.0
        maxDiff > 0.01 -> 13.0
        else -> 14.0
    }
    
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
                routeWalkingDuration = walkingDuration
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Back button - DOM-based overlay for web, Compose for JVM
        RenderBackButtonOverlay(
            onBack = { component.onEvent(NavigationViewEvent.Close) },
            onCleanup = { }
        )
        
        // Collapsible bottom sheet - overlay layer (rendered after map, so appears on top)
        if (routeDistance != null && routeDrivingDuration != null && routeWalkingDuration != null) {
            RenderBottomSheetOverlay(
                startTitle = startLocation.title,
                startAddress = startLocation.address,
                endTitle = endLocation.title,
                endAddress = endLocation.address,
                distance = routeDistance!!,
                drivingDuration = routeDrivingDuration!!,
                walkingDuration = routeWalkingDuration!!,
                isExpanded = isBottomSheetExpanded,
                onExpandToggle = { isBottomSheetExpanded = !isBottomSheetExpanded },
                onCleanup = { }
            )
        }
    }
}
