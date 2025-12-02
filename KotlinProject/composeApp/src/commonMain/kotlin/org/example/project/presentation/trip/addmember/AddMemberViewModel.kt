package org.example.project.presentation.trip.addmember

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.data.repository.TripRepository

/**
 * Defines the ViewModel and UI state for adding members to a trip and managing related UI behavior.
 */

/**
 * Represents the UI state of the "Add Member" screen, including loading, error, and completion flags.
 *
 * @property isLoading Whether a member is currently being added.
 * @property errorMessage Stores an error message if an error occurs.
 * @property didAddMember Indicates whether a member was successfully added.
 */
data class AddMemberUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val didAddMember: Boolean = false
)

/**
 * ViewModel that handles member addition logic, validation, and interaction with [TripRepository].
 *
 * @property tripId The ID of the trip to which members are being added.
 * @property tripRepository The repository used to perform member addition operations.
 */
class AddMemberViewModel(
    private val tripId: String,
    private val tripRepository: TripRepository
) : ViewModel() {

    /** Holds the mutable UI state. */
    private val _state = MutableStateFlow(AddMemberUiState())

    /** Exposes an immutable view of the UI state. */
    val state: StateFlow<AddMemberUiState> = _state.asStateFlow()

    /**
     * Validates user input and calls the repository to add a member, updating the UI state accordingly.
     *
     * @param userId The user ID of the member to add.
     */
    fun addMember(userId: String) {
        val trimmed = userId.trim()
        if (trimmed.isEmpty()) {
            _state.value = _state.value.copy(errorMessage = "Name is required")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                errorMessage = null,
                didAddMember = false
            )

            tripRepository.addMember(tripId, trimmed).fold(
                onSuccess = {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        didAddMember = true
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to add member",
                        didAddMember = false
                    )
                }
            )
        }
    }

    /**
     * Resets the [AddMemberUiState.didAddMember] flag after a successful addition.
     */
    fun clearCompletion() {
        if (_state.value.didAddMember) {
            _state.value = _state.value.copy(didAddMember = false)
        }
    }

    /**
     * Clears any existing error message from the state.
     */
    fun clearError() {
        if (_state.value.errorMessage != null) {
            _state.value = _state.value.copy(errorMessage = null)
        }
    }
}
