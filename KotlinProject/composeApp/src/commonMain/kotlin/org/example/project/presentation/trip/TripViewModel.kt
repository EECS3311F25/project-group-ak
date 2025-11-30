package org.example.project.presentation.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.data.repository.TripRepository
import org.example.project.model.dataClasses.Trip
import org.example.project.model.dataClasses.Event

// imports for AI Summary Work
import org.example.project.data.api.HttpClientFactory
import org.example.project.data.api.TripApiService
import org.example.project.data.api.TripConverter


class TripViewModel(
    private val tripId: String,
    private val tripRepository: TripRepository
) : ViewModel() {
    private val _trip = MutableStateFlow<Trip?>(null)
    val trip: StateFlow<Trip?> = _trip.asStateFlow()
    val isLoading: StateFlow<Boolean> = tripRepository.isLoading
    val error: StateFlow<String?> = tripRepository.error

    // AI-Summary Variable
    private val _aiSummary = MutableStateFlow<String?>(null)
    val aiSummary : StateFlow<String?> = _aiSummary.asStateFlow()

    private val _isGeneratingSummary  = MutableStateFlow(false)
    val isGeneratingSummary: StateFlow<Boolean> = _isGeneratingSummary.asStateFlow()

    private val _summaryError = MutableStateFlow<String?>(null)
    val summaryError: StateFlow<String?> = _summaryError.asStateFlow()


    fun generateAISummary() {
        viewModelScope.launch {
            val currentTrip = _trip.value ?: return@launch
            
            _isGeneratingSummary.value = true
            _summaryError.value = null
            _aiSummary.value = null // Clear old summary when regenerating
            
            println("Generating AI summary for trip ${currentTrip.id} with ${currentTrip.events.size} events")
            
            val apiService = TripApiService(HttpClientFactory.create())
            // Trip exists in backend, let backend look it up by ID (tripData = null)
            val result = apiService.generateTripSummary(currentTrip.id, tripData = null)
            
            result.fold(
                onSuccess = { response ->
                    println("AI summary generated successfully")
                    _aiSummary.value = response.summary
                },
                onFailure = { error ->
                    println("AI summary generation failed: ${error.message}")
                    error.printStackTrace()
                    _summaryError.value = error.message ?: "Failed to generate summary"
                }
            )
            
            _isGeneratingSummary.value = false
        }
    }

    fun clearAISummary() {
        _aiSummary.value = null
        _summaryError.value = null
    }

    init {
        // Load the trip with all its events
        viewModelScope.launch {
            _trip.value = tripRepository.getTripById(tripId)
        }
    }

    // ------- Trip operations -------
    fun addEvent(event: Event) {
        viewModelScope.launch {
            tripRepository.addEvent(tripId, event)
            // Refresh the specific trip to get updated events
            _trip.value = tripRepository.getTripById(tripId)
        }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            tripRepository.deleteEvent(tripId, eventId)
            // Refresh the specific trip to get updated events
            _trip.value = tripRepository.getTripById(tripId)
        }
    }

    fun updateEvent(eventId: String, updated: Event) {
        viewModelScope.launch {
            tripRepository.updateEvent(tripId, eventId, updated)
            // Refresh the specific trip to get updated events
            _trip.value = tripRepository.getTripById(tripId)
        }
    }

    fun addMember(userId: String) {
        viewModelScope.launch {
            tripRepository.addMember(tripId, userId)
        }
    }

    fun removeMember(userId: String) {
        viewModelScope.launch {
            tripRepository.removeMember(tripId, userId)
        }
    }

    // Image and duration updates can be added when repository supports them

    fun updateTripTitle(title: String) {
        viewModelScope.launch {
            tripRepository.updateTripTitle(tripId, title)
        }
    }

    fun updateTripDescription(description: String) {
        viewModelScope.launch {
            tripRepository.updateTripDescription(tripId, description)
        }
    }
}
