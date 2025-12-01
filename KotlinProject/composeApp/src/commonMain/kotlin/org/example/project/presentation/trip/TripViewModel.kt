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

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                println("=== TripViewModel: Loading trip with ID: $tripId ===")
                val trip = tripRepository.getTripById(tripId)
                if (trip != null) {
                    println("Loaded trip: ${trip.title} with ${trip.events.size} events")
                    _uiState.update { it.copy(trip = trip, isLoading = false) }
                } else {
                    println("ERROR: Trip not found with ID: $tripId")
                    _uiState.update { it.copy(error = "Trip not found", isLoading = false) }
                }
            } catch (e: Exception) {
                println("ERROR loading trip: ${e.message}")
                e.printStackTrace()
                _uiState.update { it.copy(error = "Failed to load trip: ${e.message}", isLoading = false) }
            }
        }
    }
    
    // Refresh trip data from repository
    fun refreshTrip() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                println("=== TripViewModel.refreshTrip ===")
                println("Loading trip with ID: $tripId")
                val trip = tripRepository.getTripById(tripId)
                if (trip != null) {
                    println("Loaded trip: ${trip.title}")
                    println("Trip has ${trip.events.size} events")
                    _uiState.update { it.copy(trip = trip) }
                } else {
                    println("ERROR: Trip not found with ID: $tripId")
                    _uiState.update { it.copy(error = "Trip not found") }
                }
            } catch (e: Exception) {
                println("ERROR refreshing trip: ${e.message}")
                e.printStackTrace()
                _uiState.update { it.copy(error = "Failed to refresh trip: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
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
