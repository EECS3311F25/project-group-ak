package org.example.project.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import org.example.project.data.repository.TripRepository
import org.example.project.model.dataClasses.Event
import org.example.project.model.dataClasses.Trip
import org.example.project.presentation.uishared.MapMarker
import org.example.project.presentation.uishared.toMapMarker

data class MapUiState(
    val trip: Trip? = null,
    val centerLatitude: Double = 43.6532, // Default: Toronto
    val centerLongitude: Double = -79.3832,
    val zoom: Double = 12.0,
    val markers: List<MapMarker> = emptyList(),
    val days: List<String> = emptyList(), // List of day labels (e.g., "Day 1", "Day 2")
    val selectedDayIndex: Int = 0, // Default to Day 1 (0-based index)
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
     * Select a specific day to filter events
     */
    fun selectDay(dayIndex: Int) {
        val trip = _uiState.value.trip ?: return
        
        // Calculate the actual date for this day index
        val dayDate = trip.duration.startDate.plus(dayIndex, DateTimeUnit.DAY)
        
        // Filter events that occur on this date
        val filteredEvents = trip.events.filter { event ->
            event.duration.isWithin(dayDate)
        }
        
        // Sort events by start time
        val sortedEvents = filteredEvents.sortedBy { it.duration.startTime }
        
        // Convert filtered events to markers with event numbers
        val markers = sortedEvents.mapIndexedNotNull { index, event ->
            event.toMapMarker()?.copy(eventNumber = index + 1)
        }
        
        // Recalculate center if we have markers
        val (centerLat, centerLng) = if (markers.isNotEmpty()) {
            calculateCenter(markers)
        } else {
            _uiState.value.centerLatitude to _uiState.value.centerLongitude
        }
        val zoom = if (markers.isNotEmpty()) estimateZoom(markers) else _uiState.value.zoom
        
        _uiState.value = _uiState.value.copy(
            selectedDayIndex = dayIndex,
            markers = markers,
            centerLatitude = centerLat,
            centerLongitude = centerLng,
            zoom = zoom
        )
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
                    // Calculate number of days in trip
                    val days = calculateDays(trip)
                    
                    // Store trip and days first
                    _uiState.value = _uiState.value.copy(
                        trip = trip,
                        days = days,
                        isLoading = false
                    )
                    
                    // Default to showing Day 1 (index 0)
                    if (days.isNotEmpty()) {
                        selectDay(0)
                    } else {
                        // No days, show all events
                        val markers = trip.events.mapNotNull { event ->
                            event.toMapMarker()
                        }
                        
                        val (centerLat, centerLng) = if (markers.isNotEmpty()) {
                            calculateCenter(markers)
                        } else {
                            43.6532 to -79.3832 // Default: Toronto
                        }
                        val zoom = if (markers.isNotEmpty()) estimateZoom(markers) else 12.0
                        
                        _uiState.value = _uiState.value.copy(
                            centerLatitude = centerLat,
                            centerLongitude = centerLng,
                            markers = markers,
                            zoom = zoom,
                            selectedDayIndex = 0
                        )
                    }
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
     * Calculate center point from markers
     */
    private fun calculateCenter(markers: List<MapMarker>): Pair<Double, Double> {
        if (markers.isEmpty()) return 0.0 to 0.0
        
        val avgLat = markers.map { it.latitude }.average()
        val avgLng = markers.map { it.longitude }.average()
        
        return avgLat to avgLng
    }

    /**
     * Estimate a reasonable zoom level based on marker spread to avoid zooming in on empty areas.
     */
    private fun estimateZoom(markers: List<MapMarker>): Double {
        if (markers.size <= 1) return 12.0

        val minLat = markers.minOf { it.latitude }
        val maxLat = markers.maxOf { it.latitude }
        val minLng = markers.minOf { it.longitude }
        val maxLng = markers.maxOf { it.longitude }

        val latDiff = maxLat - minLat
        val lngDiff = maxLng - minLng
        val maxDiff = maxOf(latDiff, lngDiff)

        return when {
            maxDiff > 30 -> 2.5
            maxDiff > 20 -> 3.5
            maxDiff > 10 -> 5.0
            maxDiff > 5  -> 7.0
            maxDiff > 1  -> 9.0
            maxDiff > 0.2 -> 11.0
            else -> 12.0
        }
    }
    
    /**
     * Calculate list of day labels for the trip
     */
    private fun calculateDays(trip: Trip): List<String> {
        val days = mutableListOf<String>()
        var currentDate = trip.duration.startDate
        val endDate = trip.duration.endDate
        var dayNumber = 1
        
        while (currentDate <= endDate) {
            days.add("Day $dayNumber")
            currentDate = currentDate.plus(1, DateTimeUnit.DAY)
            dayNumber++
        }
        
        return days
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
