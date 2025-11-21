package org.example.project.presentation.home.tripcreation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.coroutines.launch
import org.example.project.model.dataClasses.Trip
import org.example.project.model.dataClasses.Duration
import org.example.project.model.dataClasses.User
import org.example.project.model.dataClasses.Event
import org.example.project.data.repository.TripRepository
import org.example.project.data.repository.UserRepository
import org.example.project.data.remote.RemoteTripDataSource
import org.example.project.data.source.LocalUserDataSource

data class TripCreationState(
    val title: String = "",
    val duration: Duration? = null,
    val description: String = "",
    val location: String = "",
    val users: List<User> = emptyList(),
    val events: List<Event> = emptyList(),
    val imageHeaderUrl: String? = null,
    
    // UI state
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isFormValid: Boolean = false
)

class TripCreationViewModel(
    private val tripRepository: TripRepository = TripRepository(RemoteTripDataSource()),
    private val userRepository: UserRepository = UserRepository(LocalUserDataSource()) // Add UserRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(TripCreationState())
    val state: StateFlow<TripCreationState> = _state.asStateFlow()
    
    // 🔥 Initialize with current user when ViewModel is created
    init {
        viewModelScope.launch {
            try {
                val currentUser = userRepository.getCurrentUser()
                addUser(currentUser)
            } catch (e: Exception) {
                // Handle error if needed
                _state.value = _state.value.copy(
                    errorMessage = "Failed to load current user: ${e.message}"
                )
            }
        }
    }
    
    // 🔥 Expose UserRepository for getting all users (for adding members)
    suspend fun getAllUsers(): List<User> {
        return userRepository.getAllUsers()
    }
    
    suspend fun getCurrentUser(): User {
        return userRepository.getCurrentUser()
    }
    
    // Users
    fun addUser(user: User) {
        val currentUsers = _state.value.users.toMutableList()
        if (!currentUsers.contains(user)) {
            currentUsers.add(user)
            _state.value = _state.value.copy(
                users = currentUsers,
                errorMessage = null
            )
            validateForm()
        }
    }
    
    fun removeUser(user: User) {
        val currentUsers = _state.value.users.toMutableList()
        currentUsers.remove(user)
        _state.value = _state.value.copy(
            users = currentUsers,
            errorMessage = null
        )
        validateForm()
    }
    
    fun updateUsers(newUsers: List<User>) {
        _state.value = _state.value.copy(
            users = newUsers,
            errorMessage = null
        )
        validateForm()
    }
    
    // Title
    fun updateTitle(newTitle: String) {
        _state.value = _state.value.copy(
            title = newTitle,
            errorMessage = null
        )
        validateForm()
    }
    
    // Duration with times
    fun updateDuration(startDate: LocalDate, endDate: LocalDate, startTime: LocalTime = LocalTime(9, 0), endTime: LocalTime = LocalTime(18, 0)) {
        val duration = Duration(
            startDate = startDate, 
            endDate = endDate,
            startTime = startTime,
            endTime = endTime
        )
        _state.value = _state.value.copy(
            duration = duration,
            errorMessage = null
        )
        validateForm()
    }
    
    fun updateStartDate(startDate: LocalDate) {
        val currentDuration = _state.value.duration
        val newDuration = if (currentDuration != null) {
            currentDuration.copy(startDate = startDate)
        } else {
            Duration(
                startDate = startDate, 
                endDate = startDate,
                startTime = LocalTime(9, 0),  // Default start time 9:00 AM
                endTime = LocalTime(18, 0)    // Default end time 6:00 PM
            )
        }
        _state.value = _state.value.copy(
            duration = newDuration,
            errorMessage = null
        )
        validateForm()
    }
    
    fun updateEndDate(endDate: LocalDate) {
        val currentDuration = _state.value.duration
        val newDuration = if (currentDuration != null) {
            currentDuration.copy(endDate = endDate)
        } else {
            Duration(
                startDate = endDate, 
                endDate = endDate,
                startTime = LocalTime(9, 0),  // Default start time 9:00 AM
                endTime = LocalTime(18, 0)    // Default end time 6:00 PM
            )
        }
        _state.value = _state.value.copy(
            duration = newDuration,
            errorMessage = null
        )
        validateForm()
    }
    
    // Add methods to update times separately
    fun updateStartTime(startTime: LocalTime) {
        val currentDuration = _state.value.duration
        if (currentDuration != null) {
            _state.value = _state.value.copy(
                duration = currentDuration.copy(startTime = startTime),
                errorMessage = null
            )
            validateForm()
        }
    }
    
    fun updateEndTime(endTime: LocalTime) {
        val currentDuration = _state.value.duration
        if (currentDuration != null) {
            _state.value = _state.value.copy(
                duration = currentDuration.copy(endTime = endTime),
                errorMessage = null
            )
            validateForm()
        }
    }
    
    // Description
    fun updateDescription(newDescription: String) {
        _state.value = _state.value.copy(
            description = newDescription,
            errorMessage = null
        )
        validateForm()
    }
    
    // Location
    fun updateLocation(newLocation: String) {
        _state.value = _state.value.copy(
            location = newLocation,
            errorMessage = null
        )
        validateForm()
    }
    
    // Events
    fun addEvent(event: Event) {
        val currentEvents = _state.value.events.toMutableList()
        currentEvents.add(event)
        _state.value = _state.value.copy(
            events = currentEvents,
            errorMessage = null
        )
        validateForm()
    }
    
    fun removeEvent(event: Event) {
        val currentEvents = _state.value.events.toMutableList()
        currentEvents.remove(event)
        _state.value = _state.value.copy(
            events = currentEvents,
            errorMessage = null
        )
        validateForm()
    }
    
    fun updateEvents(newEvents: List<Event>) {
        _state.value = _state.value.copy(
            events = newEvents,
            errorMessage = null
        )
        validateForm()
    }
    
    // Image Header URL
    fun updateImageHeaderUrl(newImageUrl: String?) {
        _state.value = _state.value.copy(
            imageHeaderUrl = newImageUrl?.takeIf { it.isNotBlank() },
            errorMessage = null
        )
        validateForm()
    }
    
    // Form validation
    private fun validateForm() {
        val currentState = _state.value
        val isValid = currentState.title.isNotBlank() &&
                     currentState.duration != null &&
                     currentState.location.isNotBlank()
                     // Note: Don't require users.isNotEmpty() since current user is auto-added        
        _state.value = currentState.copy(isFormValid = isValid)
    }
    
    // Create trip
    fun createTrip(
        onSuccess: (Trip) -> Unit,
        onError: (String) -> Unit
    ) {
        val currentState = _state.value
        
        // Validate required fields
        if (!currentState.isFormValid) {
            val errorMsg = when {
                currentState.title.isBlank() -> "Trip title is required"
                currentState.duration == null -> "Trip duration is required"
                currentState.location.isBlank() -> "Trip location is required"
                else -> "Please fill in all required fields"
            }
            onError(errorMsg)
            return
        }
        
        // Additional validation
        val duration = currentState.duration!!
        if (duration.startDate > duration.endDate) {
            onError("End date cannot be before start date")
            return
        }
        
        _state.value = currentState.copy(isLoading = true, errorMessage = null)
        
        viewModelScope.launch {
            try {
                val trip = Trip(
                    id = Clock.System.now().toEpochMilliseconds().toString(),
                    title = currentState.title.trim(),
                    duration = duration,
                    description = currentState.description.trim(),
                    location = currentState.location.trim(),
                    users = currentState.users, // Includes current user + any added users
                    events = currentState.events,
                    imageHeaderUrl = currentState.imageHeaderUrl,
                    createdDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
                )
                
                tripRepository.createTrip(trip).fold(
                    onSuccess = { createdTrip ->
                        _state.value = currentState.copy(isLoading = false)
                        onSuccess(createdTrip)
                    },
                    onFailure = { error ->
                        _state.value = currentState.copy(
                            isLoading = false,
                            errorMessage = "Failed to create trip: ${error.message}"
                        )
                        onError("Failed to create trip: ${error.message}")
                    }
                )
                
            } catch (e: Exception) {
                _state.value = currentState.copy(
                    isLoading = false,
                    errorMessage = "Failed to create trip: ${e.message}"
                )
                onError("Failed to create trip: ${e.message}")
            }
        }
    }
    
    // Reset form
    fun resetForm() {
        _state.value = TripCreationState()
        // Re-add current user after reset
        viewModelScope.launch {
            try {
                val currentUser = userRepository.getCurrentUser()
                addUser(currentUser)
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }
    
    // Clear error
    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }
    
    // Helper functions for UI
    fun isFieldValid(field: String): Boolean {
        return when (field) {
            "title" -> _state.value.title.isNotBlank()
            "location" -> _state.value.location.isNotBlank()
            "duration" -> _state.value.duration != null
            "users" -> _state.value.users.isNotEmpty()
            else -> false
        }
    }
    
    fun getFieldError(field: String): String? {
        if (_state.value.errorMessage != null) return null
        
        return when (field) {
            "title" -> if (_state.value.title.isBlank()) "Title is required" else null
            "location" -> if (_state.value.location.isBlank()) "Location is required" else null
            "duration" -> if (_state.value.duration == null) "Duration is required" else null
            "users" -> if (_state.value.users.isEmpty()) "At least one user is required" else null
            else -> null
        }
    }
}
