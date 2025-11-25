package org.example.project.presentation.uishared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.browser.document
import kotlinx.browser.window
import org.example.project.config.Config
import org.w3c.dom.HTMLDivElement

/**
 * Web implementation using Mapbox GL JS
 * 
 * This implementation uses JavaScript interop to integrate Mapbox GL JS
 * into a Compose for Web application.
 */
@Composable
actual fun MapWindow(
    latitude: Double,
    longitude: Double,
    zoom: Double,
    markers: List<MapMarker>,
    modifier: Modifier
) {
    var isMapReady by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val mapId = remember { "map-${(0..999999).random()}" }
    
    LaunchedEffect(Unit) {
        console.log("MapView: Starting initialization...")
        console.log("MapView: mapId = $mapId")
        
        // Load Mapbox library first
        loadMapboxLibrary()
        
        // Wait for library to load using kotlinx.coroutines delay
        var attempts = 0
        while (attempts < 50 && !isMapboxLoaded()) {
            kotlinx.coroutines.delay(100)
            attempts++
        }
        
        if (!isMapboxLoaded()) {
            errorMessage = "Failed to load Mapbox library"
            console.error("MapView: Failed to load Mapbox GL JS library after $attempts attempts")
            return@LaunchedEffect
        }
        
        console.log("MapView: Mapbox library loaded successfully after $attempts attempts")
        
        // Find the ComposeTarget container
        val composeTarget = document.getElementById("ComposeTarget") as? HTMLDivElement
        if (composeTarget == null) {
            errorMessage = "Cannot find ComposeTarget element"
            console.error("MapView: Cannot find ComposeTarget element")
            return@LaunchedEffect
        }
        
        // Get ComposeTarget's position
        val rect = composeTarget.getBoundingClientRect()
        
        // Create the map container div
        val container = document.createElement("div") as HTMLDivElement
        container.id = mapId
        container.style.apply {
            position = "fixed"
            top = "${rect.top + 70}px"  // ComposeTarget top + space for title
            left = "${rect.left}px"
            width = "${rect.width}px"
            height = "${rect.height - 70}px"  // Remaining height after title
            zIndex = "100"
        }
        
        document.body?.appendChild(container)
        console.log("MapView: Container div created and appended to body")
        
        // Wait a bit for DOM to settle
        kotlinx.coroutines.delay(300)
        
        // Initialize the map
        try {
            initializeMapboxMap(mapId, latitude, longitude, zoom, markers)
            isMapReady = true
            console.log("MapView: Map initialized successfully")
        } catch (e: Throwable) {
            errorMessage = "Map initialization error: ${e.message}"
            console.error("MapView: Initialization error", e)
        }
    }
    
    DisposableEffect(mapId) {
        onDispose {
            console.log("MapView: Cleaning up map $mapId")
            val mapInstance = js("window['mapInstance_' + mapId]")
            if (mapInstance != null) {
                js("mapInstance.remove()")
            }
            document.getElementById(mapId)?.remove()
            js("delete window['mapInstance_' + mapId]")
        }
    }
    
    // Show status overlay
    if (!isMapReady || errorMessage != null) {
        Box(
            modifier = modifier.fillMaxSize().background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            when {
                errorMessage != null -> Text("Error: $errorMessage", color = Color.Red)
                else -> Text("Loading map...", color = Color.Black)
            }
        }
    }
}

/**
 * Initialize Mapbox GL JS map
 */
private fun initializeMapboxMap(
    mapId: String,
    latitude: Double,
    longitude: Double,
    zoom: Double,
    markers: List<MapMarker>
) {
    // Access token from Config (loaded from environment)
    val accessToken = Config.MAPBOX_ACCESS_TOKEN
    
    if (accessToken.isEmpty()) {
        console.error("MAPBOX_ACCESS_TOKEN is not set! Please configure your .env file.")
        return
    }
    
    // Load Mapbox GL JS library if not already loaded
    if (!isMapboxLoaded()) {
        loadMapboxLibrary()
    }
    
    // Wait for library to load, then initialize map
    window.setTimeout({
        if (js("typeof mapboxgl !== 'undefined'") as Boolean) {
            // Set access token
            js("mapboxgl.accessToken = accessToken")
            
            // Create map
            val map = js("""
                new mapboxgl.Map({
                    container: mapId,
                    style: 'mapbox://styles/mapbox/streets-v12',
                    center: [longitude, latitude],
                    zoom: zoom
                })
            """)
            
            // Add navigation controls
            js("map.addControl(new mapboxgl.NavigationControl())")
            
            // Store map instance for later updates
            js("window['mapInstance_' + mapId] = map")
            
            // Add markers
            markers.forEach { marker ->
                val markerLng = marker.longitude
                val markerLat = marker.latitude
                val markerTitle = marker.title
                val markerDesc = marker.description ?: ""
                
                js("""
                    new mapboxgl.Marker()
                        .setLngLat([markerLng, markerLat])
                        .setPopup(new mapboxgl.Popup().setHTML(
                            '<h3>' + markerTitle + '</h3>' + 
                            (markerDesc ? '<p>' + markerDesc + '</p>' : '')
                        ))
                        .addTo(map)
                """)
            }
        }
    }, 100)
}

/**
 * Update map view (center, zoom, markers)
 */
private fun updateMapView(
    mapId: String,
    latitude: Double,
    longitude: Double,
    zoom: Double,
    markers: List<MapMarker>
) {
    val mapInstance = js("window['mapInstance_' + mapId]")
    if (mapInstance != null) {
        js("""
            mapInstance.flyTo({
                center: [longitude, latitude],
                zoom: zoom,
                essential: true
            })
        """)
    }
}

/**
 * Check if Mapbox GL JS is loaded
 */
private fun isMapboxLoaded(): Boolean {
    return js("typeof mapboxgl !== 'undefined'") as Boolean
}

/**
 * Dynamically load Mapbox GL JS library
 */
private fun loadMapboxLibrary() {
    // Check if already loaded
    if (isMapboxLoaded()) {
        console.log("MapView: Mapbox library already loaded")
        return
    }
    
    // Check if already being loaded
    val existingScript = document.querySelector("script[src*='mapbox-gl.js']")
    if (existingScript != null) {
        console.log("MapView: Mapbox library script already in DOM")
        return
    }
    
    console.log("MapView: Loading Mapbox GL JS library...")
    
    // Load CSS
    val cssLink = document.createElement("link") as org.w3c.dom.HTMLLinkElement
    cssLink.rel = "stylesheet"
    cssLink.href = "https://api.mapbox.com/mapbox-gl-js/v3.0.1/mapbox-gl.css"
    document.head?.appendChild(cssLink)
    console.log("MapView: CSS link added")
    
    // Load JS - NOT async so it blocks and loads in order
    val script = document.createElement("script") as org.w3c.dom.HTMLScriptElement
    script.src = "https://api.mapbox.com/mapbox-gl-js/v3.0.1/mapbox-gl.js"
    script.asDynamic().onload = {
        console.log("MapView: Mapbox GL JS script loaded successfully")
    }
    script.asDynamic().onerror = { error: dynamic ->
        console.error("MapView: Failed to load Mapbox GL JS script", error)
    }
    document.head?.appendChild(script)
    console.log("MapView: Script tag added to head")
}
