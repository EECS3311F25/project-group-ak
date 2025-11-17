package org.example.project.viewmodel.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.example.project.data.repository.TripRepository
import org.example.project.model.dataClasses.Duration
import org.example.project.model.dataClasses.Event

/**
 * Groups user-entered date/time strings for the event duration.
 * Kept as strings to preserve raw input and validate/parse later.
 */
data class DurationFields(
    /** Event start date as a string (format: YYYY-MM-DD). */
    val startDate: String = "",
    /** Event end date as a string (format: YYYY-MM-DD). */
    val endDate: String = "",
    /** Event start time as a string (format: HH:MM). */
    val startTime: String = "",
    /** Event end time as a string (format: HH:MM). */
    val endTime: String = ""
)

/**
 * Represents the current UI state for the Add Event screen, including
 * input fields for event details, loading state, and error handling.
 *
 * @property title Event title entered by the user.
 * @property description Event description entered by the user.
 * @property location Event location entered by the user.
 * @property durationFields User-entered date/time strings grouped together.
 * @property duration Parsed duration once validated; null until valid.
 * @property isLoading Whether the event creation is in progress.
 * @property errorMessage Error message to be displayed, if any.
 * @property didCreateEvent Indicates if the event was successfully created.
 */
data class AddEventUiState(
    /** Event title entered by the user. */
    val title: String = "",
    /** Event description entered by the user. */
    val description: String = "",
    /** Optional image URL for the event header. */
    val imageUrl: String = "",
    /** Event location entered by the user. */
    val location: String = "",
    /** User-entered duration fields (date/time strings). */
    val durationFields: DurationFields = DurationFields(),
    /** Parsed duration, set on successful validation/submission. */
    val duration: Duration? = null,
    /** Trip duration window that events must stay within. */
    val tripDuration: Duration? = null,
    /** Already scheduled events for the trip to check overlaps. */
    val existingEvents: List<Event> = emptyList(),
    /** Whether the event creation is in progress. */
    val isLoading: Boolean = false,
    /** Error message to be displayed, if any. */
    val errorMessage: String? = null,
    /** Indicates if the event was successfully created/updated. */
    val didCreateEvent: Boolean = false,
    /** True when editing an existing event instead of creating a new one. */
    val isEditMode: Boolean = false
)

/**
 * ViewModel that manages the state and business logic for adding events.
 * It validates user input and interacts with [TripRepository] to persist new events.
 */
class AddEventViewModel(
    private val tripId: String,
    private val tripRepository: TripRepository,
    private val eventId: String? = null
) : ViewModel() {

    /** Holds the mutable state flow of the UI. */
    private val _state = MutableStateFlow(AddEventUiState())

    /** The exposed immutable state to observers (UI). */
    val state: StateFlow<AddEventUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val trip = tripRepository.getTripById(tripId)
            if (trip != null) {
                val editingEvent = eventId?.let { id -> trip.events.find { it.title == id } }
                val otherEvents = trip.events.filterNot { it.title == eventId }
                _state.value = _state.value.copy(
                    tripDuration = trip.duration,
                    existingEvents = otherEvents,
                    isEditMode = eventId != null
                )
                editingEvent?.let { event ->
                    _state.value = _state.value.copy(
                        title = event.title,
                        description = event.description,
                        location = event.location,
                        imageUrl = event.imageUrl.orEmpty(),
                        durationFields = DurationFields(
                            startDate = event.duration.startDate.toString(),
                            endDate = event.duration.endDate.toString(),
                            startTime = event.duration.startTime.toTimeString(),
                            endTime = event.duration.endTime.toTimeString()
                        )
                    )
                }
            }
        }
    }

    /** Updates the event title field. */
    fun updateTitle(value: String) { _state.value = _state.value.copy(title = value) }

    /** Updates the event description field. */
    fun updateDescription(value: String) { _state.value = _state.value.copy(description = value) }

    /** Updates the event image url field. */
    fun updateImageUrl(value: String) { _state.value = _state.value.copy(imageUrl = value) }

    /** Updates the event location field. */
    fun updateLocation(value: String) { _state.value = _state.value.copy(location = value) }

    /** Updates the event start date field. */
    fun updateStartDate(value: LocalDate) {
        clearError()
        val current = _state.value
        val safeStart = constrainToTripDates(value)
        val existingEnd = parseDateOrNull(current.durationFields.endDate)
        val safeEnd = existingEnd
            ?.let { constrainToTripDates(it) }
            ?.let { if (it < safeStart) safeStart else it }
            ?: safeStart

        _state.value = current.copy(
            durationFields = current.durationFields.copy(
                startDate = safeStart.toString(),
                endDate = safeEnd.toString()
            )
        )
    }

    /** Updates the event end date field. */
    fun updateEndDate(value: LocalDate) {
        clearError()
        val current = _state.value
        val safeEnd = constrainToTripDates(value)
        _state.value = current.copy(
            durationFields = current.durationFields.copy(
                endDate = safeEnd.toString()
            )
        )
    }

    /** Updates the event start time field. */
    fun updateStartTime(value: String) { _state.value = _state.value.copy(durationFields = _state.value.durationFields.copy(startTime = value)) }

    /** Updates the event end time field. */
    fun updateEndTime(value: String) { _state.value = _state.value.copy(durationFields = _state.value.durationFields.copy(endTime = value)) }

    /**
     * Validates user input, constructs an [Event] object, and triggers repository submission
     * while updating loading and error state accordingly.
     */
    fun submit() {
        val current = _state.value
        if (current.title.isBlank()) {
            _state.value = current.copy(errorMessage = "Title is required")
            return
        }
        if (current.durationFields.startDate.isBlank() || current.durationFields.endDate.isBlank()) {
            _state.value = current.copy(errorMessage = "Start and end dates are required")
            return
        }
        if (current.durationFields.startTime.isBlank() || current.durationFields.endTime.isBlank()) {
            _state.value = current.copy(errorMessage = "Start and end times are required")
            return
        }

        val parsedStartDate = parseDateOrNull(current.durationFields.startDate) ?: run {
            _state.value = current.copy(errorMessage = "Invalid start date format (YYYY-MM-DD)")
            return
        }
        val parsedEndDate = parseDateOrNull(current.durationFields.endDate) ?: run {
            _state.value = current.copy(errorMessage = "Invalid end date format (YYYY-MM-DD)")
            return
        }
        val normalizedStart = normalizeTime(current.durationFields.startTime) ?: run {
            _state.value = current.copy(errorMessage = "Invalid start time (0-23:00-59)")
            return
        }
        val parsedStartTime = runCatching { LocalTime.parse(normalizedStart) }.getOrElse {
            _state.value = current.copy(errorMessage = "Invalid start time (HH:MM)")
            return
        }
        val normalizedEnd = normalizeTime(current.durationFields.endTime) ?: run {
            _state.value = current.copy(errorMessage = "Invalid end time (0-23:00-59)")
            return
        }
        val parsedEndTime = runCatching { LocalTime.parse(normalizedEnd) }.getOrElse {
            _state.value = current.copy(errorMessage = "Invalid end time (HH:MM)")
            return
        }
        if (parsedEndDate < parsedStartDate) {
            _state.value = current.copy(errorMessage = "End date must be on or after start date")
            return
        }
        current.tripDuration?.let { tripDuration ->
            if (parsedStartDate < tripDuration.startDate || parsedEndDate > tripDuration.endDate) {
                _state.value = current.copy(
                    errorMessage = "Event dates must stay within ${tripDuration.startDate} - ${tripDuration.endDate}"
                )
                return
            }
        }

        val duration = Duration(
            startDate = parsedStartDate,
            startTime = parsedStartTime,
            endDate = parsedEndDate,
            endTime = parsedEndTime
        )

        current.existingEvents.firstOrNull { it.duration.conflictsWith(duration) }?.let { conflicting ->
            val conflictingRange = "${conflicting.duration.startDate} ${conflicting.duration.startTime} - " +
                "${conflicting.duration.endDate} ${conflicting.duration.endTime}"
            _state.value = current.copy(
                errorMessage = "Overlaps with existing event ${conflicting.title} ($conflictingRange)"
            )
            return
        }

        val normalizedImage = current.imageUrl.trim().ifBlank { null }

        val event = Event(
            title = current.title.trim(),
            duration = duration,
            description = current.description.trim(),
            location = current.location.trim(),
            imageUrl = normalizedImage
        )

        viewModelScope.launch {
            _state.value = current.copy(isLoading = true, errorMessage = null, didCreateEvent = false)
            if (current.isEditMode && eventId != null) {
                tripRepository.updateEvent(tripId, eventId, event).fold(
                    onSuccess = {
                        _state.value = current.copy(
                            duration = duration,
                            existingEvents = current.existingEvents + event,
                            isLoading = false,
                            errorMessage = null,
                            didCreateEvent = true
                        )
                    },
                    onFailure = { error ->
                        _state.value = current.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Failed to update event",
                            didCreateEvent = false
                        )
                    }
                )
            } else {
                tripRepository.addEvent(tripId, event).fold(
                    onSuccess = {
                        _state.value = current.copy(
                            title = "",
                            description = "",
                            location = "",
                            imageUrl = "",
                            durationFields = DurationFields(),
                            duration = duration,
                            existingEvents = current.existingEvents + event,
                            isLoading = false,
                            errorMessage = null,
                            didCreateEvent = true
                        )
                    },
                    onFailure = { error ->
                        _state.value = current.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Failed to add event",
                            didCreateEvent = false
                        )
                    }
                )
            }
        }
    }

    /**
     * Resets the [didCreateEvent] flag to false after successful submission.
     */
    fun clearCompletion() {
        if (_state.value.didCreateEvent) {
            _state.value = _state.value.copy(didCreateEvent = false)
        }
    }

    /**
     * Clears any existing error message from the UI state.
     */
    fun clearError() {
        if (_state.value.errorMessage != null) {
            _state.value = _state.value.copy(errorMessage = null)
        }
    }

    private fun parseDateOrNull(value: String): LocalDate? =
        value.takeIf { it.isNotBlank() }?.let { runCatching { LocalDate.parse(it) }.getOrNull() }

    private fun constrainToTripDates(date: LocalDate): LocalDate {
        val tripDuration = _state.value.tripDuration ?: return date
        return when {
            date < tripDuration.startDate -> tripDuration.startDate
            date > tripDuration.endDate -> tripDuration.endDate
            else -> date
        }
    }

    private fun normalizeTime(value: String): String? {
        if (!value.contains(":")) return null
        val parts = value.split(":")
        if (parts.size != 2) return null
        val hours = parts[0].toIntOrNull() ?: return null
        val minutes = parts[1].toIntOrNull() ?: return null
        if (hours !in 0..23 || minutes !in 0..59) return null
        val normalizedHours = hours.toString().padStart(2, '0')
        val normalizedMinutes = minutes.toString().padStart(2, '0')
        return "$normalizedHours:$normalizedMinutes"
    }

    private fun isValidTime(value: String): Boolean =
        normalizeTime(value) != null

    private fun LocalTime.toTimeString(): String =
        "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"

    fun isFieldValid(field: String): Boolean {
        val current = _state.value
        return when (field) {
            "title" -> current.title.isNotBlank()
            "location" -> current.location.isNotBlank()
            "duration" -> {
                val start = parseDateOrNull(current.durationFields.startDate) ?: return false
                val end = parseDateOrNull(current.durationFields.endDate) ?: return false
                if (end < start) return false
                current.tripDuration?.let { tripDuration ->
                    if (start < tripDuration.startDate || end > tripDuration.endDate) return false
                }
                true
            }
            "startTime" -> current.durationFields.startTime.isNotBlank() && isValidTime(current.durationFields.startTime)
            "endTime" -> current.durationFields.endTime.isNotBlank() && isValidTime(current.durationFields.endTime)
            else -> false
        }
    }

    fun getFieldError(field: String): String? {
        val current = _state.value
        return when (field) {
            "title" -> if (_state.value.title.isBlank()) "Title is required" else null
            "location" -> if (_state.value.location.isBlank()) "Location is required" else null
            "duration" -> {
                val start = parseDateOrNull(current.durationFields.startDate)
                val end = parseDateOrNull(current.durationFields.endDate)
                when {
                    start == null || end == null -> "Event duration is required"
                    end < start -> "End date must be on or after start date"
                    current.tripDuration != null &&
                        (start < current.tripDuration.startDate || end > current.tripDuration.endDate) ->
                        "Select dates within ${current.tripDuration.startDate} - ${current.tripDuration.endDate}"
                    else -> null
                }
            }
            "startTime" -> if (!isFieldValid("startTime")) "Enter a valid time (0-23:00-59)" else null
            "endTime" -> if (!isFieldValid("endTime")) "Enter a valid time (0-23:00-59)" else null
            else -> null
        }
    }
}
