package org.example.project.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.data.repository.TripRepository
import org.example.project.model.dataClasses.Event
import org.example.project.model.dataClasses.Trip
import org.example.project.presentation.uishared.MapWindow
import org.example.project.presentation.uishared.MapMarker

data class MapUiState(
    val trip: Trip? = null,
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
     * Refresh trip data (called from UI)
     */
    fun refreshTrip() {
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
                        trip = trip,
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
        // If event has Location object with coordinates, use it directly
        return event.location?.let { location ->
            MapMarker(
                latitude = location.latitude,
                longitude = location.longitude,
                title = event.title,
                description = event.description,
                address = location.address ?: location.title ?: ""
            )
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
