package org.example.project.viewmodel.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.data.repository.TripRepository

data class AddMemberUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val didAddMember: Boolean = false
)

class AddMemberViewModel(
    private val tripId: String,
    private val tripRepository: TripRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddMemberUiState())
    val state: StateFlow<AddMemberUiState> = _state.asStateFlow()

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

    fun clearCompletion() {
        if (_state.value.didAddMember) {
            _state.value = _state.value.copy(didAddMember = false)
        }
    }

    fun clearError() {
        if (_state.value.errorMessage != null) {
            _state.value = _state.value.copy(errorMessage = null)
        }
    }
}
