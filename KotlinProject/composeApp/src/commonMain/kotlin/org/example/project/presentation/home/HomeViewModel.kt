package org.example.project.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.repository.TripRepository
import org.example.project.data.repository.UserRepository
import org.example.project.model.dataClasses.Trip
import org.example.project.model.dataClasses.User

data class HomeUiState(
    val trips: List<Trip> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentUser: User? = null
)

class HomeViewModel(
    private val tripRepository: TripRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    // Backwards-compatible direct repository exposures (optional)
    val trips = tripRepository.trips
    val isLoading = tripRepository.isLoading
    val error = tripRepository.error

    // Single UiState for the UI
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var _currentUser = mutableStateOf<User?>(null)
    val currentUser: State<User?> = _currentUser

    init {
        // Keep uiState in sync with repository flows
        viewModelScope.launch {
            tripRepository.trips.collect { repoTrips ->
                _uiState.update { it.copy(trips = repoTrips) }
            }
        }
        viewModelScope.launch {
            tripRepository.isLoading.collect { loading ->
                _uiState.update { it.copy(isLoading = loading) }
            }
        }
        viewModelScope.launch {
            tripRepository.error.collect { err ->
                _uiState.update { it.copy(errorMessage = err) }
            }
        }

        // Load current user once
        viewModelScope.launch {
            try {
                val user = userRepository.getCurrentUser()
                _currentUser.value = user
                _uiState.update { it.copy(currentUser = user) }
                println("HomeViewModel: Loaded user: ${user.name} (ID: ${user.id})")
            } catch (e: Exception) {
                println("HomeViewModel: Error loading user: ${e.message}")
                e.printStackTrace()
            }
        }
        
        // Initial data load
        viewModelScope.launch {
            tripRepository.refresh()
        }
    }

    fun deleteTrip(tripTitle: String) {
        viewModelScope.launch {
            // repository will update its flows which will propagate to uiState via collectors above
            tripRepository.deleteTrip(tripTitle)
        }
    }
}
