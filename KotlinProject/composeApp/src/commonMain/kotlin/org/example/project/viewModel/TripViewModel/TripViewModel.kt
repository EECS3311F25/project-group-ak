package org.example.project.viewModel.TripViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.project.data.repository.TripRepository
import org.example.project.model.Trip
import org.example.project.model.Event

class TripViewModel(
    private val tripId: String,
    private val tripRepository: TripRepository
) : ViewModel() {
    private val _trip = MutableStateFlow<Trip?>(null)
    val trip: StateFlow<Trip?> = _trip
    val isLoading: StateFlow<Boolean> = tripRepository.isLoading
    val error: StateFlow<String?> = tripRepository.error

    init {
        viewModelScope.launch {
            _trip.value = tripRepository.getTripById(tripId)
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
