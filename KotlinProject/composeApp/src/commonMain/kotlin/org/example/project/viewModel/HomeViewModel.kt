package org.example.project.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.project.data.repository.TripRepository
import org.example.project.data.repository.UserRepository
import org.example.project.model.Trip
import org.example.project.model.User

class HomeViewModel(
    private val tripRepository: TripRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    // Expose repository states as StateFlows
    val trips: StateFlow<List<Trip>> = tripRepository.trips
    val isLoading: StateFlow<Boolean> = tripRepository.isLoading
    val error: StateFlow<String?> = tripRepository.error
    
    private var _currentUser = mutableStateOf<User?>(null)
    val currentUser: State<User?> = _currentUser
    
    init {
        viewModelScope.launch {
            _currentUser.value = userRepository.getCurrentUser()
        }
    }
    
    fun deleteTrip(tripId: String) {
        viewModelScope.launch {
            tripRepository.deleteTrip(tripId)
        }
    }
}
