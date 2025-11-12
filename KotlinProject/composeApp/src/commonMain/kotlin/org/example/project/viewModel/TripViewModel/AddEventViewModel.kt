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
import org.example.project.model.Duration
import org.example.project.model.Event

data class AddEventUiState(
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val didCreateEvent: Boolean = false
)

class AddEventViewModel(
    private val tripId: String,
    private val tripRepository: TripRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddEventUiState())
    val state: StateFlow<AddEventUiState> = _state.asStateFlow()

    fun updateTitle(value: String) { _state.value = _state.value.copy(title = value) }
    fun updateDescription(value: String) { _state.value = _state.value.copy(description = value) }
    fun updateLocation(value: String) { _state.value = _state.value.copy(location = value) }
    fun updateStartDate(value: String) { _state.value = _state.value.copy(startDate = value) }
    fun updateEndDate(value: String) { _state.value = _state.value.copy(endDate = value) }
    fun updateStartTime(value: String) { _state.value = _state.value.copy(startTime = value) }
    fun updateEndTime(value: String) { _state.value = _state.value.copy(endTime = value) }

    fun submit() {
        val current = _state.value
        if (current.title.isBlank()) {
            _state.value = current.copy(errorMessage = "Title is required")
            return
        }
        if (current.startDate.isBlank() || current.endDate.isBlank()) {
            _state.value = current.copy(errorMessage = "Start and end dates are required")
            return
        }
        if (current.startTime.isBlank() || current.endTime.isBlank()) {
            _state.value = current.copy(errorMessage = "Start and end times are required")
            return
        }

        val parsedStartDate = runCatching { LocalDate.parse(current.startDate) }.getOrElse {
            _state.value = current.copy(errorMessage = "Invalid start date format (YYYY-MM-DD)")
            return
        }
        val parsedEndDate = runCatching { LocalDate.parse(current.endDate) }.getOrElse {
            _state.value = current.copy(errorMessage = "Invalid end date format (YYYY-MM-DD)")
            return
        }
        val parsedStartTime = runCatching { LocalTime.parse(current.startTime) }.getOrElse {
            _state.value = current.copy(errorMessage = "Invalid start time (HH:MM)")
            return
        }
        val parsedEndTime = runCatching { LocalTime.parse(current.endTime) }.getOrElse {
            _state.value = current.copy(errorMessage = "Invalid end time (HH:MM)")
            return
        }

        val duration = Duration(
            startDate = parsedStartDate,
            startTime = parsedStartTime,
            endDate = parsedEndDate,
            endTime = parsedEndTime
        )

        val event = Event(
            title = current.title.trim(),
            duration = duration,
            description = current.description.trim(),
            location = current.location.trim()
        )

        viewModelScope.launch {
            _state.value = current.copy(isLoading = true, errorMessage = null, didCreateEvent = false)
            tripRepository.addEvent(tripId, event).fold(
                onSuccess = {
                    _state.value = current.copy(
                        title = "",
                        description = "",
                        location = "",
                        startDate = "",
                        endDate = "",
                        startTime = "",
                        endTime = "",
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

    fun clearCompletion() {
        if (_state.value.didCreateEvent) {
            _state.value = _state.value.copy(didCreateEvent = false)
        }
    }

    fun clearError() {
        if (_state.value.errorMessage != null) {
            _state.value = _state.value.copy(errorMessage = null)
        }
    }
}
