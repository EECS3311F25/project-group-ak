package org.example.project.presentation.trip.edittrip

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

data class EditTripUiState(
    val title: String = "",
    val description: String = "",
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val startTime: String = "",
    val endTime: String = "",
    val imageUrl: String = "",
    val dateError: String? = null,
    val startTimeError: String? = null,
    val endTimeError: String? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val didUpdate: Boolean = false
)

class EditTripViewModel(
    private val tripId: String,
    private val tripRepository: TripRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EditTripUiState())
    val state: StateFlow<EditTripUiState> = _state.asStateFlow()

    init {
        loadTrip()
    }

    private fun loadTrip() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            val trip = runCatching { tripRepository.getTripById(tripId) }.getOrNull()
            if (trip != null) {
                _state.value = _state.value.copy(
                    title = trip.title,
                    description = trip.description,
                    startDate = trip.duration.startDate,
                    endDate = trip.duration.endDate,
                    startTime = formatTime(trip.duration.startTime),
                    endTime = formatTime(trip.duration.endTime),
                    imageUrl = trip.imageHeaderUrl.orEmpty(),
                    isLoading = false
                )
            } else {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Unable to load trip details"
                )
            }
        }
    }

    fun updateTitle(newTitle: String) {
        _state.value = _state.value.copy(title = newTitle)
    }

    fun updateDescription(newDescription: String) {
        _state.value = _state.value.copy(description = newDescription)
    }

    fun updateStartDate(date: LocalDate) {
        _state.value = _state.value.copy(startDate = date, dateError = null)
    }

    fun updateEndDate(date: LocalDate) {
        _state.value = _state.value.copy(endDate = date, dateError = null)
    }

    fun updateStartTime(value: String) {
        _state.value = _state.value.copy(startTime = value, startTimeError = null)
    }

    fun updateEndTime(value: String) {
        _state.value = _state.value.copy(endTime = value, endTimeError = null)
    }

    fun updateImageUrl(value: String) {
        _state.value = _state.value.copy(imageUrl = value)
    }

    fun saveChanges() {
        val trimmed = _state.value.title.trim()
        val description = _state.value.description.trim()
        var hasError = false
        var dateError: String? = null
        var startTimeError: String? = null
        var endTimeError: String? = null
        var generalError: String? = null

        if (trimmed.isEmpty()) {
            generalError = "Title cannot be empty"
            hasError = true
        }

        val startDate = _state.value.startDate
        val endDate = _state.value.endDate
        if (startDate == null || endDate == null) {
            dateError = "Start and end dates are required"
            hasError = true
        }

        val startTime = parseTime(_state.value.startTime).also {
            if (it == null) {
                startTimeError = "Use HH:MM format"
                hasError = true
            }
        }

        val endTime = parseTime(_state.value.endTime).also {
            if (it == null) {
                endTimeError = "Use HH:MM format"
                hasError = true
            }
        }

        if (!hasError && startDate != null && endDate != null && startTime != null && endTime != null) {
            if (endDate < startDate || (endDate == startDate && endTime < startTime)) {
                dateError = "End must be after start"
                hasError = true
            }
        }

        if (hasError) {
            _state.value = _state.value.copy(
                errorMessage = generalError,
                dateError = dateError,
                startTimeError = startTimeError,
                endTimeError = endTimeError
            )
            return
        }

        val duration = Duration(
            startDate = startDate!!,
            startTime = startTime!!,
            endDate = endDate!!,
            endTime = endTime!!
        )

        val normalizedImageUrl = _state.value.imageUrl.trim().ifBlank { null }

        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, errorMessage = null)
            tripRepository.updateTripDetails(tripId, trimmed, duration, normalizedImageUrl, description).fold(
                onSuccess = {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        didUpdate = true,
                        title = trimmed,
                        description = description,
                        imageUrl = normalizedImageUrl.orEmpty(),
                        dateError = null,
                        startTimeError = null,
                        endTimeError = null
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isSaving = false,
                        errorMessage = error.message ?: "Unable to update trip"
                    )
                }
            )
        }
    }

    fun clearCompletion() {
        if (_state.value.didUpdate) {
            _state.value = _state.value.copy(didUpdate = false)
        }
    }

    fun clearError() {
        if (_state.value.errorMessage != null ||
            _state.value.dateError != null ||
            _state.value.startTimeError != null ||
            _state.value.endTimeError != null
        ) {
            _state.value = _state.value.copy(
                errorMessage = null,
                dateError = null,
                startTimeError = null,
                endTimeError = null
            )
        }
    }

    private fun parseTime(value: String): LocalTime? {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return null
        return runCatching { LocalTime.parse(trimmed) }.getOrNull()
    }

    private fun formatTime(time: LocalTime): String =
        "${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"
}
