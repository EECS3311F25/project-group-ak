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
    routeEndpoints: Pair<MapMarker, MapMarker>?,
    onRouteCalculated: ((distance: Double, drivingDuration: Double, walkingDuration: Double) -> Unit)?,
    modifier: Modifier
) {
    var isMapReady by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var routeInfo by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var selectedStartMarker by remember { mutableStateOf<MapMarker?>(null) }
    val mapId = remember { "map-${(0..999999).random()}" }
    
    // NavBar height to subtract from map height (64dp converted to pixels, approximately 88px)
    val navBarHeightPx = 88
    
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
            top = "${rect.top}px"
            left = "${rect.left}px"
            width = "${rect.width}px"
            // Subtract NavBar height from the total height
            height = "${rect.height - navBarHeightPx}px"
            zIndex = "1"
        }
        
        document.body?.appendChild(container)
        console.log("MapView: Container div created with height adjusted for NavBar")
        
        // Wait a bit for DOM to settle
        kotlinx.coroutines.delay(300)
        
        // Initialize the map
        try {
            initializeMapboxMap(
                mapId, 
                latitude, 
                longitude, 
                zoom, 
                markers, 
                routeEndpoints, 
                onRouteCalculated,
                onStartMarkerSelected = { marker ->
                    selectedStartMarker = marker
                    console.log("Start marker selected: ${marker.title}")
                },
                onEndMarkerSelected = { endMarker ->
                    selectedStartMarker?.let { startMarker ->
                        console.log("End marker selected: ${endMarker.title}, navigating from ${startMarker.title}")
                        // TODO: Navigate to NavigationView with these two markers
                        // For now, just show alert - you'll replace this with actual navigation
                        js("""
                            alert('Route from: ' + startMarker.title + '\nTo: ' + endMarker.title);
                        """)
                    }
                }
            )
            isMapReady = true
            console.log("MapView: Map initialized successfully")
        } catch (e: Throwable) {
            errorMessage = "Map initialization error: ${e.message}"
            console.error("MapView: Initialization error", e)
        }
    }
    
    // Update map when markers, center, or zoom change
    LaunchedEffect(markers, latitude, longitude, zoom) {
        if (isMapReady) {
            console.log("MapView: Updating map with ${markers.size} markers")
            updateMapView(mapId, latitude, longitude, zoom, markers, routeEndpoints)
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
    markers: List<MapMarker>,
    routeEndpoints: Pair<MapMarker, MapMarker>?,
    onRouteCalculated: ((distance: Double, drivingDuration: Double, walkingDuration: Double) -> Unit)?,
    onStartMarkerSelected: (MapMarker) -> Unit,
    onEndMarkerSelected: (MapMarker) -> Unit
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
            
            // Store marker instances and navigation state using dynamic property access
            js("window['mapMarkers_' + mapId] = []")
            js("window['navigationMode_' + mapId] = false")
            js("window['startMarker_' + mapId] = null")
            
            // Create Kotlin callback wrappers that can be called from JavaScript
            js("window['kotlinStartCallback_' + mapId] = onStartMarkerSelected")
            js("window['kotlinEndCallback_' + mapId] = onEndMarkerSelected")
            
            // Set up global handler for "Navigate from here" button
            js("""
                var mapIdStr = mapId;
                window['handleNavigateFromHere_' + mapIdStr] = function(title, lat, lng, address) {
                    console.log('Navigate from here clicked:', title);
                    
                    // Store the start marker
                    window['startMarker_' + mapIdStr] = { title: title, latitude: lat, longitude: lng, address: address };
                    
                    // Enable navigation mode
                    window['navigationMode_' + mapIdStr] = true;
                    
                    // Change all other markers to yellow
                    window['mapMarkers_' + mapIdStr].forEach(function(markerObj) {
                        if (markerObj.title !== title) {
                            markerObj.marker.remove();
                            var newMarker = new mapboxgl.Marker({ color: '#FFD700' })
                                .setLngLat([markerObj.longitude, markerObj.latitude])
                                .setPopup(markerObj.popup)
                                .addTo(markerObj.map);
                            markerObj.marker = newMarker;
                        }
                    });
                    
                    // Close the current popup
                    var map = window['mapInstance_' + mapIdStr];
                    var popups = document.getElementsByClassName('mapboxgl-popup');
                    if (popups.length) {
                        for (var i = 0; i < popups.length; i++) {
                            popups[i].remove();
                        }
                    }
                };
            """)
            
            // Set up global handler for destination marker selection
            js("""
                var mapIdStr = mapId;
                window['handleDestinationSelected_' + mapIdStr] = function(title, lat, lng, address) {
                    console.log('Destination selected:', title);
                    
                    if (window['startMarker_' + mapIdStr]) {
                        var startMarker = window['startMarker_' + mapIdStr];
                        var endMarker = { title: title, latitude: lat, longitude: lng, address: address };
                        
                        // Call Kotlin callbacks
                        window['kotlinStartCallback_' + mapIdStr](startMarker);
                        window['kotlinEndCallback_' + mapIdStr](endMarker);
                        
                        // Reset navigation mode
                        window['navigationMode_' + mapIdStr] = false;
                        window['startMarker_' + mapIdStr] = null;
                    }
                };
            """)
            
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
            
            // Wait for map to load before adding markers and route
            js("""
                map.on('load', function() {
                    console.log('MapView: Map loaded event fired');
                })
            """)
            
            // Add markers
            markers.forEach { marker ->
                val markerLng = marker.longitude
                val markerLat = marker.latitude
                val markerTitle = marker.title
                val markerDesc = marker.description ?: ""
                val markerAddress = marker.address
                val markerStartTime = marker.startTime?.let { "${it.hour.toString().padStart(2, '0')}:${it.minute.toString().padStart(2, '0')}" } ?: ""
                val markerEndTime = marker.endTime?.let { "${it.hour.toString().padStart(2, '0')}:${it.minute.toString().padStart(2, '0')}" } ?: ""
                val hasTime = markerStartTime.isNotEmpty() && markerEndTime.isNotEmpty()
                
                js("""
                    var mapIdStr = mapId;
                    var timeHtml = hasTime ? 
                        '<p style="margin: 0 0 8px 0; font-size: 13px; color: #444;">' + markerStartTime + ' - ' + markerEndTime + '</p>' : 
                        '';
                    
                    var popup = new mapboxgl.Popup({ offset: 25 })
                        .setHTML(
                            '<div style="padding: 8px;">' +
                                '<h3 style="margin: 0 0 8px 0; font-size: 16px; font-weight: bold;">' + markerTitle + '</h3>' + 
                                timeHtml +
                                (markerDesc ? '<p style="margin: 0 0 8px 0; font-size: 14px;">' + markerDesc + '</p>' : '') +
                                '<p style="margin: 0 0 12px 0; font-size: 12px; color: #666;">' + markerAddress + '</p>' +
                                '<button id="nav-btn-' + markerTitle.replace(/\s+/g, '-') + '" ' +
                                    'style="background-color: #3887be; color: white; border: none; ' +
                                    'padding: 8px 16px; border-radius: 4px; cursor: pointer; font-size: 14px; width: 100%;">' +
                                    'Navigate from here' +
                                '</button>' +
                            '</div>'
                        );
                    
                    var newMarker = new mapboxgl.Marker({ color: '#3887be' })
                        .setLngLat([markerLng, markerLat])
                        .setPopup(popup)
                        .addTo(map);
                    
                    // Store marker reference
                    window['mapMarkers_' + mapIdStr].push({
                        title: markerTitle,
                        latitude: markerLat,
                        longitude: markerLng,
                        address: markerAddress,
                        marker: newMarker,
                        popup: popup,
                        map: map
                    });
                    
                    // Add click handler for the button after popup opens
                    popup.on('open', function() {
                        var btn = document.getElementById('nav-btn-' + markerTitle.replace(/\s+/g, '-'));
                        if (btn) {
                            btn.onclick = function() {
                                if (window['navigationMode_' + mapIdStr]) {
                                    // In navigation mode, this is the destination
                                    window['handleDestinationSelected_' + mapIdStr](markerTitle, markerLat, markerLng, markerAddress);
                                } else {
                                    // Start navigation mode
                                    window['handleNavigateFromHere_' + mapIdStr](markerTitle, markerLat, markerLng, markerAddress);
                                }
                            };
                        }
                    });
                """)
            }
            
            // If routeEndpoints is provided, fetch and display route between those two markers
            if (routeEndpoints != null) {
                val start = routeEndpoints.first
                val end = routeEndpoints.second
                fetchAndDisplayRoutes(mapId, start, end, accessToken, onRouteCalculated)
            }
        }
    }, 100)
}

/**
 * Fetch both driving and walking routes from Mapbox Directions API
 */
private fun fetchAndDisplayRoutes(
    mapId: String,
    start: MapMarker,
    end: MapMarker,
    accessToken: String,
    onRouteCalculated: ((distance: Double, drivingDuration: Double, walkingDuration: Double) -> Unit)?
) {
    val startCoords = "${start.longitude},${start.latitude}"
    val endCoords = "${end.longitude},${end.latitude}"
    
    // Fetch driving route
    val drivingUrl = "https://api.mapbox.com/directions/v5/mapbox/driving/$startCoords;$endCoords" +
            "?geometries=geojson&access_token=$accessToken"
    
    // Fetch walking route
    val walkingUrl = "https://api.mapbox.com/directions/v5/mapbox/walking/$startCoords;$endCoords" +
            "?geometries=geojson&access_token=$accessToken"
    
    console.log("MapView: Fetching driving route from: $drivingUrl")
    console.log("MapView: Fetching walking route from: $walkingUrl")
    
    // Create a wrapper function to add route to map
    js("""
        window.addRouteToMap = function(map, geojson) {
            if (map.getLayer('route')) {
                map.removeLayer('route');
            }
            if (map.getSource('route')) {
                map.removeSource('route');
            }
            
            map.addSource('route', {
                'type': 'geojson',
                'data': {
                    'type': 'Feature',
                    'properties': {},
                    'geometry': geojson
                }
            });
            
            map.addLayer({
                'id': 'route',
                'type': 'line',
                'source': 'route',
                'layout': {
                    'line-join': 'round',
                    'line-cap': 'round'
                },
                'paint': {
                    'line-color': '#3887be',
                    'line-width': 5,
                    'line-opacity': 0.75
                }
            });
            
            console.log('MapView: Route layer added to map');
        };
        
        window.routeResults = {
            driving: null,
            walking: null
        };
    """)
    
    // Fetch both routes using JavaScript fetch API
    js("""
        Promise.all([
            fetch(drivingUrl).then(function(response) { return response.json(); }),
            fetch(walkingUrl).then(function(response) { return response.json(); })
        ])
        .then(function(results) {
            var drivingData = results[0];
            var walkingData = results[1];
            
            console.log('MapView: Driving route data received', drivingData);
            console.log('MapView: Walking route data received', walkingData);
            
            var map = window['mapInstance_' + mapId];
            
            // Process driving route
            if (drivingData.routes && drivingData.routes.length > 0) {
                var drivingRoute = drivingData.routes[0];
                var geojson = drivingRoute.geometry;
                var distance = drivingRoute.distance;
                var drivingDuration = drivingRoute.duration;
                
                console.log('MapView: Driving - Distance: ' + distance + 'm, Duration: ' + drivingDuration + 's');
                
                window.routeResults.driving = {
                    distance: distance,
                    duration: drivingDuration
                };
                
                // Display driving route on map
                if (map.isStyleLoaded()) {
                    window.addRouteToMap(map, geojson);
                } else {
                    map.on('load', function() {
                        window.addRouteToMap(map, geojson);
                    });
                }
            }
            
            // Process walking route
            if (walkingData.routes && walkingData.routes.length > 0) {
                var walkingRoute = walkingData.routes[0];
                var walkingDuration = walkingRoute.duration;
                
                console.log('MapView: Walking - Duration: ' + walkingDuration + 's');
                
                window.routeResults.walking = {
                    duration: walkingDuration
                };
            }
            
            // Return both driving and walking route info to callback
            if (onRouteCalculated && window.routeResults.driving && window.routeResults.walking) {
                onRouteCalculated(
                    window.routeResults.driving.distance / 1000, 
                    window.routeResults.driving.duration / 60,
                    window.routeResults.walking.duration / 60
                );
            }
        })
        .catch(function(error) {
            console.error('MapView: Error fetching routes:', error);
        });
    """)
}

/**
 * Legacy function for backward compatibility - now uses fetchAndDisplayRoutes
 */
private fun fetchAndDisplayRoute(
    mapId: String,
    start: MapMarker,
    end: MapMarker,
    accessToken: String,
    onRouteCalculated: ((distance: Double, drivingDuration: Double, walkingDuration: Double) -> Unit)?
) {
    fetchAndDisplayRoutes(mapId, start, end, accessToken, onRouteCalculated)
}

/**
 * Update map view (center, zoom, markers)
 */
private fun updateMapView(
    mapId: String,
    latitude: Double,
    longitude: Double,
    zoom: Double,
    markers: List<MapMarker>,
    routeEndpoints: Pair<MapMarker, MapMarker>?
) {
    val mapInstance = js("window['mapInstance_' + mapId]")
    if (mapInstance != null) {
        // Update map center and zoom
        js("""
            mapInstance.flyTo({
                center: [longitude, latitude],
                zoom: zoom,
                essential: true
            })
        """)
        
        // Clear existing markers
        js("""
            var mapIdStr = mapId;
            if (window['mapMarkers_' + mapIdStr]) {
                window['mapMarkers_' + mapIdStr].forEach(function(markerObj) {
                    markerObj.marker.remove();
                });
                window['mapMarkers_' + mapIdStr] = [];
            }
        """)
        
        // Add new markers
        markers.forEach { marker ->
            val markerLng = marker.longitude
            val markerLat = marker.latitude
            val markerTitle = marker.title
            val markerDesc = marker.description ?: ""
            val markerAddress = marker.address
            val markerStartTime = marker.startTime?.let { "${it.hour.toString().padStart(2, '0')}:${it.minute.toString().padStart(2, '0')}" } ?: ""
            val markerEndTime = marker.endTime?.let { "${it.hour.toString().padStart(2, '0')}:${it.minute.toString().padStart(2, '0')}" } ?: ""
            val hasTime = markerStartTime.isNotEmpty() && markerEndTime.isNotEmpty()
            
            js("""
                var mapIdStr = mapId;
                var timeHtml = hasTime ? 
                    '<p style="margin: 0 0 8px 0; font-size: 13px; color: #444;">' + markerStartTime + ' - ' + markerEndTime + '</p>' : 
                    '';
                
                var popup = new mapboxgl.Popup({ offset: 25 })
                    .setHTML(
                        '<div style="padding: 8px;">' +
                            '<h3 style="margin: 0 0 8px 0; font-size: 16px; font-weight: bold;">' + markerTitle + '</h3>' + 
                            timeHtml +
                            (markerDesc ? '<p style="margin: 0 0 8px 0; font-size: 14px;">' + markerDesc + '</p>' : '') +
                            '<p style="margin: 0 0 12px 0; font-size: 12px; color: #666;">' + markerAddress + '</p>' +
                            '<button id="nav-btn-' + markerTitle.replace(/\s+/g, '-') + '" ' +
                                'style="background-color: #3887be; color: white; border: none; ' +
                                'padding: 8px 16px; border-radius: 4px; cursor: pointer; font-size: 14px; width: 100%;">' +
                                'Navigate from here' +
                            '</button>' +
                        '</div>'
                    );
                
                var newMarker = new mapboxgl.Marker({ color: '#3887be' })
                    .setLngLat([markerLng, markerLat])
                    .setPopup(popup)
                    .addTo(mapInstance);
                
                // Store marker reference
                if (!window['mapMarkers_' + mapIdStr]) {
                    window['mapMarkers_' + mapIdStr] = [];
                }
                
                window['mapMarkers_' + mapIdStr].push({
                    title: markerTitle,
                    latitude: markerLat,
                    longitude: markerLng,
                    address: markerAddress,
                    marker: newMarker,
                    popup: popup,
                    map: mapInstance
                });
                
                // Add click handler for the button after popup opens
                popup.on('open', function() {
                    var btn = document.getElementById('nav-btn-' + markerTitle.replace(/\s+/g, '-'));
                    if (btn) {
                        btn.onclick = function() {
                            if (window['navigationMode_' + mapIdStr]) {
                                // In navigation mode, this is the destination
                                window['handleDestinationSelected_' + mapIdStr](markerTitle, markerLat, markerLng, markerAddress);
                            } else {
                                // Start navigation mode
                                window['handleNavigateFromHere_' + mapIdStr](markerTitle, markerLat, markerLng, markerAddress);
                            }
                        };
                    }
                });
            """)
        }
        
        console.log("MapView: Map updated with ${markers.size} markers")
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
