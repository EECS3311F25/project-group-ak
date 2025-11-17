
package org.example.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.example.project.model.dataClasses.Event
import org.example.project.model.dataClasses.Trip
import org.example.project.data.repository.TripRepository

data class CalendarUiState(
    val currentTrip: Trip,
    val selectedDate: LocalDate? = null,
    val events: List<Event> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)



class CalendarViewModel(
    private val tripId: String,
    private val tripRepository: TripRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        CalendarUiState(
            currentTrip = Trip(
                id = tripId,
                title = "",
                description = "",
                users = emptyList(),
                events = emptyList(),
                duration = org.example.project.model.dataClasses.Duration(
                    startDate = kotlinx.datetime.LocalDate(2000, 1, 1),
                    startTime = kotlinx.datetime.LocalTime(0, 0),
                    endDate = kotlinx.datetime.LocalDate(2000, 1, 1),
                    endTime = kotlinx.datetime.LocalTime(0, 0)
                ),
                createdDate = kotlinx.datetime.LocalDate(2000, 1, 1)
            ),
            selectedDate = null,
            events = emptyList(),
            isLoading = true,
            error = null
        )
    )

    val uiState: StateFlow<CalendarUiState> = _uiState

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val trip = tripRepository.getTripById(tripId)
                if (trip != null) {
                    _uiState.update { it.copy(currentTrip = trip, events = trip.events, isLoading = false) }
                } else {
                    _uiState.update { it.copy(error = "Trip not found", isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to load trip: ${e.message}", isLoading = false) }
            }
        }
    }

    fun selectDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
        loadEventsForDate(date)
    }

    private fun loadEventsForDate(date: LocalDate) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                println("=== CalendarViewModel.loadEventsForDate ===")
                println("Loading events for date: $date")
                println("Total trip events: ${_uiState.value.currentTrip.events.size}")
                
                // Filter events for the selected date by checking overlap with that date.
                // This includes events that started the day before and end on this date,
                // events that start on this date and end the next day, and multi-day events.
                val filteredEvents = _uiState.value.currentTrip.events.filter { event ->
                    val overlaps = event.duration.overlapsDate(date)
                    println("  ${event.title}: overlapsDate($date) = $overlaps")
                    println("    Duration: ${event.duration.startDate} ${event.duration.startTime} -> ${event.duration.endDate} ${event.duration.endTime}")
                    overlaps
                }
                println("Filtered events count: ${filteredEvents.size}")
                _uiState.update { it.copy(events = filteredEvents) }
            } catch (e: Exception) {
                println("ERROR loading events: ${e.message}")
                e.printStackTrace()
                _uiState.update { it.copy(error = "Failed to load events: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // Add or update an event in the trip
    fun addEvent(event: Event) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Check for conflicts with existing events
                val conflicts = getConflictingEvents(event)
                if (conflicts.isNotEmpty()) {
                    _uiState.update { it.copy(error = "Event conflicts with: ${conflicts.joinToString { it.title }}", isLoading = false) }
                    return@launch
                }
                val updatedTrip = _uiState.value.currentTrip.copy(
                    events = _uiState.value.currentTrip.events + event
                )
                val result = tripRepository.updateTrip(updatedTrip)
                result.onSuccess { updated ->
                    // Only update currentTrip, let loadEventsForDate set filtered events
                    _uiState.update { s -> s.copy(currentTrip = updated) }
                    _uiState.value.selectedDate?.let { loadEventsForDate(it) }
                }
                result.onFailure { throwable ->
                    _uiState.update { s -> s.copy(error = "Failed to add event: ${throwable.message}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to add event: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
    
    // Check if an event conflicts with existing events
    fun getConflictingEvents(event: Event): List<Event> {
        return _uiState.value.currentTrip.events.filter { existingEvent ->
            existingEvent.duration.conflictsWith(event.duration)
        }
    }

    // Update an existing event
    fun updateEvent(oldEvent: Event, newEvent: Event) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val updatedEvents = _uiState.value.currentTrip.events.map { event ->
                    if (event.title == oldEvent.title && event.duration == oldEvent.duration) newEvent else event
                }
                val updatedTrip = _uiState.value.currentTrip.copy(events = updatedEvents)
                val result = tripRepository.updateTrip(updatedTrip)
                result.onSuccess { updated ->
                    // Only update currentTrip, let loadEventsForDate set filtered events
                    _uiState.update { s -> s.copy(currentTrip = updated) }
                    _uiState.value.selectedDate?.let { loadEventsForDate(it) }
                }
                result.onFailure { throwable ->
                    _uiState.update { s -> s.copy(error = "Failed to update event: ${throwable.message}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to update event: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // Delete an event
    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val updatedEvents = _uiState.value.currentTrip.events.filter {
                    it.title != event.title || it.duration != event.duration
                }
                val updatedTrip = _uiState.value.currentTrip.copy(events = updatedEvents)
                val result = tripRepository.updateTrip(updatedTrip)
                result.onSuccess { updated ->
                    // Only update currentTrip, let loadEventsForDate set filtered events
                    _uiState.update { s -> s.copy(currentTrip = updated) }
                    _uiState.value.selectedDate?.let { loadEventsForDate(it) }
                }
                result.onFailure { throwable ->
                    _uiState.update { s -> s.copy(error = "Failed to delete event: ${throwable.message}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to delete event: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // Refresh trip data from repository
    fun refreshTrip() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val trip = tripRepository.getTripById(tripId)
                if (trip != null) {
                    _uiState.update { it.copy(currentTrip = trip, events = trip.events) }
                } else {
                    _uiState.update { it.copy(error = "Trip not found") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to refresh trip: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    /**
     * Replace the current trip used by this ViewModel. This is useful when the
     * caller navigates to a different trip but re-uses the same ViewModel
     * instance. It resets selectedDate and events to match the new trip.
     */
    fun updateTrip(newTrip: Trip, selectStartDate: Boolean = true) {
        _uiState.update { current ->
            val newSelected = if (selectStartDate) newTrip.duration.startDate else current.selectedDate
            current.copy(
                currentTrip = newTrip,
                selectedDate = newSelected,
                events = newTrip.events,
                error = null
            )
        }
    }

    // Update only the time of an event (used for drag-to-change-time)
    fun updateEventTime(oldEvent: Event, newEvent: Event) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val updatedEvents = _uiState.value.currentTrip.events.map { event ->
                    if (event.title == oldEvent.title && event.duration == oldEvent.duration) newEvent else event
                }
                val updatedTrip = _uiState.value.currentTrip.copy(events = updatedEvents)
                val result = tripRepository.updateTrip(updatedTrip)
                result.onSuccess { updated ->
                    _uiState.update { s -> s.copy(currentTrip = updated) }
                    _uiState.value.selectedDate?.let { loadEventsForDate(it) }
                }
                result.onFailure { throwable ->
                    _uiState.update { s -> s.copy(error = "Failed to update event time: ${throwable.message}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to update event time: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}