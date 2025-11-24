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
            
            // Step 1: Check if trip is in localCreatedTrips (newly created trip)
            val localTrip = tripRepository.getLocalCreatedTrip(currentTrip.id)
            val tripData = if (localTrip != null) {
                // Convert frontend Trip to backend Trip format
                org.example.project.data.api.TripConverter.toBackendTrip(localTrip)
            } else {
                null // Trip exists in backend, let backend look it up
            }
            
            val apiService = TripApiService(HttpClientFactory.create())
            val result = apiService.generateTripSummary(currentTrip.id, tripData)
            
            _isGeneratingSummary.value = false
            
            result.onSuccess { response ->
                println("✅ AI Summary generated successfully: ${response.summary.take(50)}...")
                _aiSummary.value = response.summary
                println("✅ _aiSummary.value is now: ${_aiSummary.value?.take(50)}...")
            }.onFailure { error ->
                println("❌ AI Summary failed: ${error.message}")
                _summaryError.value = error.message ?: "Failed to generate summary"
            }
        }
    }

    fun clearAISummary() {
        _aiSummary.value = null
        _summaryError.value = null
    }

    init {
        viewModelScope.launch {
            tripRepository.trips.collect { trips ->
                _trip.value = trips.find { it.id == tripId }
            }
        }

        viewModelScope.launch {
            if (_trip.value == null) {
                _trip.value = tripRepository.getTripById(tripId)
            }
        }
    }

    // ------- Trip operations -------
    fun addEvent(event: Event) {
        viewModelScope.launch {
            tripRepository.addEvent(tripId, event)
        }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            tripRepository.deleteEvent(tripId, eventId)
        }
    }

    fun updateEvent(eventId: String, updated: Event) {
        viewModelScope.launch {
            tripRepository.updateEvent(tripId, eventId, updated)
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
