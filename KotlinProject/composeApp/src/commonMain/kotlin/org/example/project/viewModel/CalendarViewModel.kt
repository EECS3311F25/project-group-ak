package org.example.project.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.example.project.model.Event
import org.example.project.model.Trip
import org.example.project.data.repository.TripRepository

data class CalendarUiState(
    val currentTrip: Trip,
    val selectedDate: LocalDate? = null,
    val events: List<Event> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)


class CalendarViewModel(
    private val trip: Trip,
    private val tripRepository: TripRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        CalendarUiState(
            currentTrip = trip,
            selectedDate = null,
            events = trip.events,
            isLoading = false,
            error = null
        )
    )

    val uiState: StateFlow<CalendarUiState> = _uiState

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
                    _uiState.update { s -> s.copy(currentTrip = updated, events = updated.events) }
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
                    _uiState.update { s -> s.copy(currentTrip = updated, events = updated.events) }
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
                    _uiState.update { s -> s.copy(currentTrip = updated, events = updated.events) }
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
                // TODO: Trip model needs an id field for this to work
                // For now, just update with current trip data
                tripRepository.refresh()
                _uiState.update { it.copy(events = it.currentTrip.events) }
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
}