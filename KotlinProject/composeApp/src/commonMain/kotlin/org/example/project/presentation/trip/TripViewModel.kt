package org.example.project.presentation.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.repository.TripRepository
import org.example.project.model.dataClasses.Trip
import org.example.project.model.dataClasses.Event

// imports for AI Summary Work
import org.example.project.data.api.HttpClientFactory
import org.example.project.data.api.TripApiService
import org.example.project.data.api.TripConverter

data class TripUiState(
    val trip: Trip? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class TripViewModel(
    private val tripId: String,
    private val tripRepository: TripRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TripUiState(isLoading = true))
    val uiState: StateFlow<TripUiState> = _uiState.asStateFlow()

    // AI-Summary Variable
    private val _aiSummary = MutableStateFlow<String?>(null)
    val aiSummary : StateFlow<String?> = _aiSummary.asStateFlow()

    private val _isGeneratingSummary  = MutableStateFlow(false)
    val isGeneratingSummary: StateFlow<Boolean> = _isGeneratingSummary.asStateFlow()

    private val _summaryError = MutableStateFlow<String?>(null)
    val summaryError: StateFlow<String?> = _summaryError.asStateFlow()

    // FrontEnd - Triggers this function
    fun generateAISummary() {
        viewModelScope.launch {
            val currentTrip = _uiState.value.trip ?: return@launch
            
            _isGeneratingSummary.value = true
            _summaryError.value = null
            _aiSummary.value = null // Clear old summary when regenerating
            
            println("Generating AI summary for trip ${currentTrip.id} with ${currentTrip.events.size} events")
            
            val apiService = TripApiService(HttpClientFactory.create())

            // 2nd step : Trip exists in backend, let backend look it up by ID (tripData = null)
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
        refreshTrip()
    }
    
    fun refreshTrip() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val trip = tripRepository.getTripById(tripId)
                _uiState.update { it.copy(trip = trip, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to load trip: ${e.message}", isLoading = false) }
            }
        }
    }

    // ------- Trip operations -------
    fun addEvent(event: Event) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                tripRepository.addEvent(tripId, event)
                val trip = tripRepository.getTripById(tripId)
                if (trip != null) {
                    _uiState.update { it.copy(trip = trip) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to add event: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                tripRepository.deleteEvent(tripId, eventId)
                val trip = tripRepository.getTripById(tripId)
                if (trip != null) {
                    _uiState.update { it.copy(trip = trip) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to delete event: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateEvent(eventId: String, updated: Event) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                tripRepository.updateEvent(tripId, eventId, updated)
                val trip = tripRepository.getTripById(tripId)
                if (trip != null) {
                    _uiState.update { it.copy(trip = trip) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to update event: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun addMember(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                tripRepository.addMember(tripId, userId)
                val trip = tripRepository.getTripById(tripId)
                if (trip != null) {
                    _uiState.update { it.copy(trip = trip) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to add member: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun removeMember(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                tripRepository.removeMember(tripId, userId)
                val trip = tripRepository.getTripById(tripId)
                if (trip != null) {
                    _uiState.update { it.copy(trip = trip) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to remove member: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // Image and duration updates can be added when repository supports them

    fun updateTripTitle(title: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                tripRepository.updateTripTitle(tripId, title)
                val trip = tripRepository.getTripById(tripId)
                if (trip != null) {
                    _uiState.update { it.copy(trip = trip) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to update title: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateTripDescription(description: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                tripRepository.updateTripDescription(tripId, description)
                val trip = tripRepository.getTripById(tripId)
                if (trip != null) {
                    _uiState.update { it.copy(trip = trip) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to update description: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}
