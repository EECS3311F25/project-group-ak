package org.example.project.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.example.project.model.Event

class CalendarViewModel : ViewModel() {
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        loadEventsForDate(date)
    }

    private fun loadEventsForDate(date: LocalDate) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // TODO: Implement event loading logic
                // This will be connected to your event repository
                _events.value = emptyList() // Placeholder
            } catch (e: Exception) {
                _error.value = "Failed to load events: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}