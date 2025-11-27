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
    onNavigateToNavigation: ((startMarker: MapMarker, endMarker: MapMarker) -> Unit)?,
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
                onNavigateToNavigation,
                onStartMarkerSelected = { marker ->
                    selectedStartMarker = marker
                    console.log("Start marker selected: ${marker.title}")
                },
                onEndMarkerSelected = { endMarker ->
                    selectedStartMarker?.let { startMarker ->
                        console.log("End marker selected: ${endMarker.title}, navigating from ${startMarker.title}")
                        // Call the navigation callback to navigate to NavigationView
                        onNavigateToNavigation?.invoke(startMarker, endMarker)
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
    onNavigateToNavigation: ((startMarker: MapMarker, endMarker: MapMarker) -> Unit)?,
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
            
            // Create a wrapper for handling destination selection
            val handleDestinationWrapper: (String, Double, Double, String) -> Unit = { title, lat, lng, address ->
                console.log("Destination wrapper called: $title")
                
                // Get the start marker from window storage
                val startMarkerData = js("window['startMarker_' + mapId]")
                
                if (startMarkerData != null) {
                    // Extract start marker data
                    val startTitle = js("startMarkerData.title") as String
                    val startLat = js("startMarkerData.latitude") as Double
                    val startLng = js("startMarkerData.longitude") as Double
                    val startAddress = js("startMarkerData.address") as String
                    
                    // Create MapMarker objects
                    val startMarker = MapMarker(
                        title = startTitle,
                        latitude = startLat,
                        longitude = startLng,
                        address = startAddress
                    )
                    
                    val endMarker = MapMarker(
                        title = title,
                        latitude = lat,
                        longitude = lng,
                        address = address
                    )
                    
                    console.log("Calling onNavigateToNavigation with MapMarker objects")
                    
                    // Call the navigation callback
                    onNavigateToNavigation?.invoke(startMarker, endMarker)
                    
                    // Reset navigation mode
                    js("window['navigationMode_' + mapId] = false")
                    js("window['startMarker_' + mapId] = null")
                } else {
                    console.error("No start marker found!")
                }
            }
            
            // Store the wrapper in window
            js("window['handleDestinationSelected_' + mapId] = handleDestinationWrapper")
            
            // Set up global handler for "Navigate from here" button
            js("""
                var mapIdStr = mapId;
                window['handleNavigateFromHere_' + mapIdStr] = function(title, lat, lng, address) {
                    console.log('Navigate from here clicked:', title);
                    console.log('Current navigation mode:', window['navigationMode_' + mapIdStr]);
                    
                    // Store the start marker
                    window['startMarker_' + mapIdStr] = { title: title, latitude: lat, longitude: lng, address: address };
                    
                    // Enable navigation mode
                    window['navigationMode_' + mapIdStr] = true;
                    console.log('Navigation mode enabled, startMarker:', window['startMarker_' + mapIdStr]);
                    
                    // Close all popups first
                    var map = window['mapInstance_' + mapIdStr];
                    var popups = document.getElementsByClassName('mapboxgl-popup');
                    if (popups.length) {
                        for (var i = 0; i < popups.length; i++) {
                            popups[i].remove();
                        }
                    }
                    
                    // Update ALL markers - both start and destinations
                    var markersChanged = 0;
                    window['mapMarkers_' + mapIdStr].forEach(function(markerObj) {
                        // Remove old marker
                        markerObj.marker.remove();
                        
                        // Create new popup
                        var newPopup = new mapboxgl.Popup({ offset: 25 })
                            .setHTML(markerObj.popupHTML);
                        
                        // Determine color: blue for start marker, yellow for others
                        var markerColor = (markerObj.title === title) ? '#3887be' : '#FFD700';
                        
                        // Create new marker with appropriate color
                        var newMarker = new mapboxgl.Marker({ color: markerColor })
                            .setLngLat([markerObj.longitude, markerObj.latitude])
                            .setPopup(newPopup)
                            .addTo(markerObj.map);
                        
                        // Update marker reference
                        markerObj.marker = newMarker;
                        markerObj.popup = newPopup;
                        
                        // Attach popup event handler with IIFE to capture popup
                        (function(capturedPopup, capturedMarkerObj, isStart) {
                            capturedPopup.on('open', function() {
                                setTimeout(function() {
                                    var popupContent = capturedPopup.getElement();
                                    var btn = popupContent ? popupContent.querySelector('button') : null;
                                    if (btn) {
                                        var btnText = btn.querySelector('.nav-btn-text');
                                        if (isStart) {
                                            btnText.textContent = 'Cancel Navigation';
                                        } else {
                                            btnText.textContent = 'Select as destination';
                                        }
                                        btn.onclick = function(e) {
                                            e.preventDefault();
                                            e.stopPropagation();
                                            if (isStart) {
                                                window['handleCancelNavigation_' + mapIdStr]();
                                            } else {
                                                window['handleDestinationSelected_' + mapIdStr](
                                                    capturedMarkerObj.title,
                                                    capturedMarkerObj.latitude,
                                                    capturedMarkerObj.longitude,
                                                    capturedMarkerObj.address
                                                );
                                            }
                                        };
                                    }
                                }, 100);
                            });
                        })(newPopup, markerObj, markerObj.title === title);
                        
                        markersChanged++;
                    });
                    console.log('Updated ' + markersChanged + ' markers for navigation mode');
                };
                
                window['handleCancelNavigation_' + mapIdStr] = function() {
                    console.log('Cancel navigation clicked');
                    
                    // Reset navigation mode
                    window['navigationMode_' + mapIdStr] = false;
                    window['startMarker_' + mapIdStr] = null;
                    
                    // Close all popups
                    var popups = document.getElementsByClassName('mapboxgl-popup');
                    if (popups.length) {
                        for (var i = 0; i < popups.length; i++) {
                            popups[i].remove();
                        }
                    }
                    
                    // Change all markers back to blue
                    window['mapMarkers_' + mapIdStr].forEach(function(markerObj) {
                        markerObj.marker.remove();
                        var newPopup = new mapboxgl.Popup({ offset: 25 })
                            .setHTML(markerObj.popupHTML);
                        var newMarker = new mapboxgl.Marker({ color: '#3887be' })
                            .setLngLat([markerObj.longitude, markerObj.latitude])
                            .setPopup(newPopup)
                            .addTo(markerObj.map);
                        markerObj.marker = newMarker;
                        markerObj.popup = newPopup;
                        
                        // Reattach popup event handler with IIFE to capture popup
                        (function(capturedPopup, capturedMarkerObj) {
                            capturedPopup.on('open', function() {
                                setTimeout(function() {
                                    var popupContent = capturedPopup.getElement();
                                    var btn = popupContent ? popupContent.querySelector('button') : null;
                                    if (btn) {
                                        var btnText = btn.querySelector('.nav-btn-text');
                                        btnText.textContent = 'Navigate from here';
                                        btn.onclick = function(e) {
                                            e.preventDefault();
                                            e.stopPropagation();
                                            window['handleNavigateFromHere_' + mapIdStr](
                                                capturedMarkerObj.title,
                                                capturedMarkerObj.latitude,
                                                capturedMarkerObj.longitude,
                                                capturedMarkerObj.address
                                            );
                                        };
                                    }
                                }, 100);
                            });
                        })(newPopup, markerObj);
                    });
                    
                    console.log('Navigation cancelled, all markers reset to blue');
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
                val eventNumber = marker.eventNumber ?: 0
                val hasEventNumber = eventNumber > 0
                
                js("""
                    var mapIdStr = mapId;
                    var eventNumberHtml = hasEventNumber ? 
                        '<div style="display: inline-block; background-color: #3887be; color: white; font-weight: bold; ' +
                        'width: 24px; height: 24px; border-radius: 50%; text-align: center; line-height: 24px; ' +
                        'font-size: 14px; margin-right: 8px; vertical-align: middle;">' + eventNumber + '</div>' : 
                        '';
                    var timeHtml = hasTime ? 
                        '<p style="margin: 0 0 8px 0; font-size: 13px; color: #444;">' + markerStartTime + ' - ' + markerEndTime + '</p>' : 
                        '';
                    
                    // Create unique button ID
                    var buttonId = 'nav-btn-' + markerTitle.replace(/\s+/g, '-').replace(/[^a-zA-Z0-9-]/g, '');
                    console.log('Creating button with ID:', buttonId, 'for marker:', markerTitle);
                    
                    // Store the popup HTML for later recreation
                    var popupHTML = '<div style="padding: 8px;">' +
                        '<div style="display: flex; align-items: center; margin-bottom: 8px;">' +
                            eventNumberHtml +
                            '<h3 style="margin: 0; font-size: 16px; font-weight: bold; flex: 1;">' + markerTitle + '</h3>' +
                        '</div>' +
                        timeHtml +
                        (markerDesc ? '<p style="margin: 0 0 8px 0; font-size: 14px;">' + markerDesc + '</p>' : '') +
                        '<p style="margin: 0 0 12px 0; font-size: 12px; color: #666;">' + markerAddress + '</p>' +
                        '<button id="' + buttonId + '" ' +
                            'style="background-color: #3887be; color: white; border: none; ' +
                            'padding: 8px 16px; border-radius: 4px; cursor: pointer; font-size: 14px; width: 100%;">' +
                            '<span class="nav-btn-text">Navigate from here</span>' +
                        '</button>' +
                    '</div>';
                    
                    var popup = new mapboxgl.Popup({ offset: 25 })
                        .setHTML(popupHTML);
                    
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
                        popupHTML: popupHTML,
                        map: map
                    });
                    
                    // Add click handler for the button after popup opens
                    // Use IIFE to capture the correct popup and marker data
                    (function(capturedPopup, capturedTitle, capturedLat, capturedLng, capturedAddress) {
                        capturedPopup.on('open', function() {
                            console.log('[initMap] Popup opened for:', capturedTitle);
                            
                            // Function to setup button with retry mechanism
                            function setupButton(attempt) {
                                var popupContent = capturedPopup.getElement();
                                console.log('[initMap] Attempt', attempt, '- Popup element for', capturedTitle, ':', popupContent);
                                
                                if (!popupContent) {
                                    if (attempt < 10) {
                                        console.log('[initMap] Retrying in 50ms...');
                                        setTimeout(function() { setupButton(attempt + 1); }, 50);
                                    } else {
                                        console.error('[initMap] Failed to get popup content after 10 attempts for:', capturedTitle);
                                    }
                                    return;
                                }
                                
                                var btn = popupContent.querySelector('button');
                                console.log('[initMap] Button element found:', btn);
                                
                                if (btn) {
                                    var btnText = btn.querySelector('.nav-btn-text');
                                    
                                    // Function to update button text based on current state
                                    function updateButtonText() {
                                        if (window['navigationMode_' + mapIdStr]) {
                                            var startMarkerTitle = window['startMarker_' + mapIdStr] ? window['startMarker_' + mapIdStr].title : null;
                                            if (capturedTitle === startMarkerTitle) {
                                                btnText.textContent = 'Cancel Navigation';
                                                console.log('[initMap] Button text: Cancel Navigation');
                                            } else {
                                                btnText.textContent = 'Select as destination';
                                                console.log('[initMap] Button text: Select as destination');
                                            }
                                        } else {
                                            btnText.textContent = 'Navigate from here';
                                            console.log('[initMap] Button text: Navigate from here');
                                        }
                                    }
                                    
                                    // Set initial button text
                                    updateButtonText();
                                    
                                    // Remove any existing onclick handlers
                                    btn.onclick = null;
                                    
                                    // Attach new click handler that checks state at click time
                                    btn.onclick = function(e) {
                                        e.preventDefault();
                                        e.stopPropagation();
                                        
                                        console.log('[initMap] Button clicked for:', capturedTitle);
                                        console.log('[initMap] Current navigation mode:', window['navigationMode_' + mapIdStr]);
                                        
                                        // Check current state at click time
                                        if (window['navigationMode_' + mapIdStr]) {
                                            var startMarkerTitle = window['startMarker_' + mapIdStr] ? window['startMarker_' + mapIdStr].title : null;
                                            console.log('[initMap] Start marker:', startMarkerTitle);
                                            
                                            if (capturedTitle === startMarkerTitle) {
                                                // Cancel navigation
                                                console.log('[initMap] Calling handleCancelNavigation');
                                                window['handleCancelNavigation_' + mapIdStr]();
                                            } else {
                                                // Select as destination
                                                console.log('[initMap] Calling handleDestinationSelected');
                                                window['handleDestinationSelected_' + mapIdStr](capturedTitle, capturedLat, capturedLng, capturedAddress);
                                            }
                                        } else {
                                            // Start navigation mode
                                            console.log('[initMap] Calling handleNavigateFromHere');
                                            window['handleNavigateFromHere_' + mapIdStr](capturedTitle, capturedLat, capturedLng, capturedAddress);
                                        }
                                    };
                                    
                                    console.log('[initMap] Button onclick handler attached for:', capturedTitle);
                                } else {
                                    console.error('[initMap] Button not found for marker:', capturedTitle);
                                }
                            }
                            
                            // Start setup with initial delay
                            setTimeout(function() { setupButton(1); }, 50);
                        });
                    })(popup, markerTitle, markerLat, markerLng, markerAddress);
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
            // Preserve navigation state across updates
            // Don't reset navigationMode or startMarker here
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
                val eventNumber = marker.eventNumber ?: 0
                val hasEventNumber = eventNumber > 0

                js("""
                    var mapIdStr = mapId;
                    var eventNumberHtml = hasEventNumber ? 
                        '<div style="display: inline-block; background-color: #3887be; color: white; font-weight: bold; ' +
                        'width: 24px; height: 24px; border-radius: 50%; text-align: center; line-height: 24px; ' +
                        'font-size: 14px; margin-right: 8px; vertical-align: middle;">' + eventNumber + '</div>' : 
                        '';
                    var timeHtml = hasTime ? 
                        '<p style="margin: 0 0 8px 0; font-size: 13px; color: #444;">' + markerStartTime + ' - ' + markerEndTime + '</p>' : 
                        '';

                    // Create unique button ID
                    var buttonId = 'nav-btn-' + markerTitle.replace(/\s+/g, '-').replace(/[^a-zA-Z0-9-]/g, '');
                    console.log('[updateMapView] Creating button with ID:', buttonId, 'for marker:', markerTitle);

                    // Store the popup HTML for later recreation
                    var popupHTML = '<div style="padding: 8px;">' +
                        '<div style="display: flex; align-items: center; margin-bottom: 8px;">' +
                            eventNumberHtml +
                            '<h3 style="margin: 0; font-size: 16px; font-weight: bold; flex: 1;">' + markerTitle + '</h3>' +
                        '</div>' +
                        timeHtml +
                        (markerDesc ? '<p style="margin: 0 0 8px 0; font-size: 14px;">' + markerDesc + '</p>' : '') +
                        '<p style="margin: 0 0 12px 0; font-size: 12px; color: #666;">' + markerAddress + '</p>' +
                        '<button id="' + buttonId + '" ' +
                            'style="background-color: #3887be; color: white; border: none; ' +
                            'padding: 8px 16px; border-radius: 4px; cursor: pointer; font-size: 14px; width: 100%;">' +
                            '<span class="nav-btn-text">Navigate from here</span>' +
                        '</button>' +
                    '</div>';

                    var popup = new mapboxgl.Popup({ offset: 25 })
                        .setHTML(popupHTML);
                    
                    // Determine marker color based on navigation state
                    var markerColor = '#3887be'; // Default blue
                    var isStartMarker = false;
                    
                    if (window['navigationMode_' + mapIdStr] && window['startMarker_' + mapIdStr]) {
                        var startMarkerTitle = window['startMarker_' + mapIdStr].title;
                        if (markerTitle === startMarkerTitle) {
                            // This is the start marker, keep it blue
                            markerColor = '#3887be';
                            isStartMarker = true;
                        } else {
                            // Other markers should be yellow in navigation mode
                            markerColor = '#FFD700';
                        }
                    }
                    
                    var newMarker = new mapboxgl.Marker({ color: markerColor })
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
                        popupHTML: popupHTML,
                        map: mapInstance
                    });
                    
                    // Store a reference to update button text dynamically
                    // Use IIFE to capture the correct popup and marker data
                    (function(capturedPopup, capturedTitle, capturedLat, capturedLng, capturedAddress) {
                        capturedPopup.on('open', function() {
                            console.log('[updateMapView] Popup opened for:', capturedTitle);
                            
                            // Function to setup button with retry mechanism
                            function setupButton(attempt) {
                                var popupContent = capturedPopup.getElement();
                                console.log('[updateMapView] Attempt', attempt, '- Popup element for', capturedTitle, ':', popupContent);
                                
                                if (!popupContent) {
                                    if (attempt < 10) {
                                        console.log('[updateMapView] Retrying in 50ms...');
                                        setTimeout(function() { setupButton(attempt + 1); }, 50);
                                    } else {
                                        console.error('[updateMapView] Failed to get popup content after 10 attempts for:', capturedTitle);
                                    }
                                    return;
                                }
                                
                                var btn = popupContent.querySelector('button');
                                console.log('[updateMapView] Button element found:', btn);
                                
                                if (btn) {
                                    var btnText = btn.querySelector('.nav-btn-text');
                                    
                                    // Function to update button text based on current state
                                    function updateButtonText() {
                                        if (window['navigationMode_' + mapIdStr]) {
                                            var startMarkerTitle = window['startMarker_' + mapIdStr] ? window['startMarker_' + mapIdStr].title : null;
                                            if (capturedTitle === startMarkerTitle) {
                                                btnText.textContent = 'Cancel Navigation';
                                                console.log('[updateMapView] Button text: Cancel Navigation');
                                            } else {
                                                btnText.textContent = 'Select as destination';
                                                console.log('[updateMapView] Button text: Select as destination');
                                            }
                                        } else {
                                            btnText.textContent = 'Navigate from here';
                                            console.log('[updateMapView] Button text: Navigate from here');
                                        }
                                    }
                                    
                                    // Set initial button text
                                    updateButtonText();
                                    
                                    // Remove any existing onclick handlers
                                    btn.onclick = null;
                                    
                                    // Attach new click handler that checks state at click time
                                    btn.onclick = function(e) {
                                        e.preventDefault();
                                        e.stopPropagation();
                                        
                                        console.log('[updateMapView] Button clicked for:', capturedTitle);
                                        console.log('[updateMapView] Current navigation mode:', window['navigationMode_' + mapIdStr]);
                                        
                                        // Check current state at click time
                                        if (window['navigationMode_' + mapIdStr]) {
                                            var startMarkerTitle = window['startMarker_' + mapIdStr] ? window['startMarker_' + mapIdStr].title : null;
                                            console.log('[updateMapView] Start marker:', startMarkerTitle);
                                            
                                            if (capturedTitle === startMarkerTitle) {
                                                // Cancel navigation
                                                console.log('[updateMapView] Calling handleCancelNavigation');
                                                window['handleCancelNavigation_' + mapIdStr]();
                                            } else {
                                                // Select as destination
                                                console.log('[updateMapView] Calling handleDestinationSelected');
                                                window['handleDestinationSelected_' + mapIdStr](capturedTitle, capturedLat, capturedLng, capturedAddress);
                                            }
                                        } else {
                                            // Start navigation mode
                                            console.log('[updateMapView] Calling handleNavigateFromHere');
                                            window['handleNavigateFromHere_' + mapIdStr](capturedTitle, capturedLat, capturedLng, capturedAddress);
                                        }
                                    };
                                    
                                    console.log('[updateMapView] Button onclick handler attached for:', capturedTitle);
                                } else {
                                    console.error('[updateMapView] Button not found for marker:', capturedTitle);
                                }
                            }
                            
                            // Start setup with initial delay
                            setTimeout(function() { setupButton(1); }, 50);
                        });
                    })(popup, markerTitle, markerLat, markerLng, markerAddress);
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
