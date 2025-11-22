package org.example.project.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.data.repository.TripRepository
import org.example.project.model.dataClasses.Event

data class MapUiState(
    val centerLatitude: Double = 43.6532, // Default: Toronto
    val centerLongitude: Double = -79.3832,
    val zoom: Double = 12.0,
    val markers: List<MapMarker> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class MapViewModel(
    private val tripId: String,
    private val tripRepository: TripRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()
    
    init {
        loadTripLocations()
    }
    
    /**
     * Load trip and convert events to map markers
     */
    private fun loadTripLocations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val trip = tripRepository.getTripById(tripId)
                
                if (trip != null) {
                    // Convert events to markers (assuming events have location data)
                    val markers = trip.events.mapNotNull { event ->
                        // Parse location string to coordinates
                        // For now, using placeholder coordinates
                        // You'll need to implement geocoding or store lat/lng in Event model
                        parseLocationToMarker(event)
                    }
                    
                    // Calculate center point from markers
                    val (centerLat, centerLng) = if (markers.isNotEmpty()) {
                        calculateCenter(markers)
                    } else {
                        43.6532 to -79.3832 // Default: Toronto
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        centerLatitude = centerLat,
                        centerLongitude = centerLng,
                        markers = markers,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Trip not found",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load trip: ${e.message}",
                    isLoading = false
                )
            }
        }
    }
    
    /**
     * Parse event location to map marker
     * TODO: Implement proper geocoding or add lat/lng to Event model
     */
    private fun parseLocationToMarker(event: Event): MapMarker? {
        // For now, return null - you need to implement location parsing
        // Options:
        // 1. Add latitude/longitude fields to Event data class
        // 2. Use geocoding API to convert location string to coordinates
        // 3. Manually map known locations
        
        return when (event.location?.lowercase()) {
            "toronto, on", "toronto" -> MapMarker(
                latitude = 43.6532,
                longitude = -79.3832,
                title = event.title,
                description = event.description
            )
            "niagara falls, on", "niagara falls" -> MapMarker(
                latitude = 43.0896,
                longitude = -79.0849,
                title = event.title,
                description = event.description
            )
            "kingston, on", "kingston" -> MapMarker(
                latitude = 44.2312,
                longitude = -76.4860,
                title = event.title,
                description = event.description
            )
            "ottawa, on", "ottawa" -> MapMarker(
                latitude = 45.4215,
                longitude = -75.6972,
                title = event.title,
                description = event.description
            )
            else -> null
        }
    }
    
    /**
     * Calculate center point from markers
     */
    private fun calculateCenter(markers: List<MapMarker>): Pair<Double, Double> {
        if (markers.isEmpty()) return 0.0 to 0.0
        
        val avgLat = markers.map { it.latitude }.average()
        val avgLng = markers.map { it.longitude }.average()
        
        return avgLat to avgLng
    }
    
    /**
     * Update map center
     */
    fun setCenter(latitude: Double, longitude: Double, zoom: Double = _uiState.value.zoom) {
        _uiState.value = _uiState.value.copy(
            centerLatitude = latitude,
            centerLongitude = longitude,
            zoom = zoom
        )
    }
}
