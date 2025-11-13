package org.example.project.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.example.project.model.Event
import org.example.project.model.Trip
import org.example.project.data.repository.TripRepository

class CalendarViewModel(
    private val trip: Trip,
    private val tripRepository: TripRepository
) : ViewModel() {
    private val _currentTrip = MutableStateFlow(trip)
    val currentTrip: StateFlow<Trip> = _currentTrip

    private val _events = MutableStateFlow<List<Event>>(trip.events)
    val events: StateFlow<List<Event>> = _events

    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        loadEventsForDate(date)
    }

    private fun loadEventsForDate(date: LocalDate) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                println("=== CalendarViewModel.loadEventsForDate ===")
                println("Loading events for date: $date")
                println("Total trip events: ${_currentTrip.value.events.size}")
                
                // Filter events for the selected date by checking overlap with that date.
                // This includes events that started the day before and end on this date,
                // events that start on this date and end the next day, and multi-day events.
                val filteredEvents = _currentTrip.value.events.filter { event ->
                    val overlaps = event.duration.overlapsDate(date)
                    println("  ${event.title}: overlapsDate($date) = $overlaps")
                    println("    Duration: ${event.duration.startDate} ${event.duration.startTime} -> ${event.duration.endDate} ${event.duration.endTime}")
                    overlaps
                }
                println("Filtered events count: ${filteredEvents.size}")
                _events.value = filteredEvents
            } catch (e: Exception) {
                println("ERROR loading events: ${e.message}")
                e.printStackTrace()
                _error.value = "Failed to load events: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Add or update an event in the trip
    fun addEvent(event: Event) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // Check for conflicts with existing events
                val conflicts = getConflictingEvents(event)
                if (conflicts.isNotEmpty()) {
                    _error.value = "Event conflicts with: ${conflicts.joinToString { it.title }}"
                    _isLoading.value = false
                    return@launch
                }
                
                val updatedTrip = _currentTrip.value.copy(
                    events = _currentTrip.value.events + event
                )
                val result = tripRepository.updateTrip(updatedTrip)
                
                result.onSuccess { 
                    _currentTrip.value = it
                    _events.value = it.events
                }
                result.onFailure {
                    _error.value = "Failed to add event: ${it.message}"
                }
            } catch (e: Exception) {
                _error.value = "Failed to add event: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Check if an event conflicts with existing events
    fun getConflictingEvents(event: Event): List<Event> {
        return _currentTrip.value.events.filter { existingEvent ->
            existingEvent.duration.conflictsWith(event.duration)
        }
    }

    // Update an existing event
    fun updateEvent(oldEvent: Event, newEvent: Event) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val updatedEvents = _currentTrip.value.events.map { event ->
                    if (event.title == oldEvent.title && event.duration == oldEvent.duration) newEvent else event
                }
                val updatedTrip = _currentTrip.value.copy(events = updatedEvents)
                val result = tripRepository.updateTrip(updatedTrip)
                
                result.onSuccess { 
                    _currentTrip.value = it
                    _events.value = it.events
                }
                result.onFailure {
                    _error.value = "Failed to update event: ${it.message}"
                }
            } catch (e: Exception) {
                _error.value = "Failed to update event: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Delete an event
    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val updatedEvents = _currentTrip.value.events.filter { 
                    it.title != event.title || it.duration != event.duration
                }
                val updatedTrip = _currentTrip.value.copy(events = updatedEvents)
                val result = tripRepository.updateTrip(updatedTrip)
                
                result.onSuccess { 
                    _currentTrip.value = it
                    _events.value = it.events
                }
                result.onFailure {
                    _error.value = "Failed to delete event: ${it.message}"
                }
            } catch (e: Exception) {
                _error.value = "Failed to delete event: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Refresh trip data from repository
    fun refreshTrip() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // TODO: Trip model needs an id field for this to work
                // For now, just update with current trip data
                tripRepository.refresh()
                _events.value = _currentTrip.value.events
            } catch (e: Exception) {
                _error.value = "Failed to refresh trip: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}