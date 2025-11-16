package org.example.project.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SignupState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class SignupViewModel : ViewModel() {
    
    private val _state = MutableStateFlow(SignupState())
    val state: StateFlow<SignupState> = _state.asStateFlow()
    
    fun updateName(newName: String) {
        _state.value = _state.value.copy(name = newName, errorMessage = null)
    }
    
    fun updateEmail(newEmail: String) {
        _state.value = _state.value.copy(email = newEmail, errorMessage = null)
    }
    
    fun updatePassword(newPassword: String) {
        _state.value = _state.value.copy(password = newPassword, errorMessage = null)
    }
    
    fun updateConfirmPassword(newConfirmPassword: String) {
        _state.value = _state.value.copy(confirmPassword = newConfirmPassword, errorMessage = null)
    }
    
    fun signup(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val currentState = _state.value
        
        // Validate all fields are filled
        if (currentState.name.isBlank() || currentState.email.isBlank() || 
            currentState.password.isBlank() || currentState.confirmPassword.isBlank()) {
            onError("All fields are required")
            return
        }
        
        // Validate passwords match
        if (currentState.password != currentState.confirmPassword) {
            onError("Passwords do not match")
            return
        }
        
        // Validate email format
        if (!currentState.email.contains("@")) {
            onError("Please enter a valid email address")
            return
        }
        
        // Validate password strength
        if (currentState.password.length < 6) {
            onError("Password must be at least 6 characters")
            return
        }
        
        _state.value = currentState.copy(isLoading = true, errorMessage = null)
        
        // TODO: Add real signup logic here (API call, user creation, etc.)
        // For now, simulate a successful signup
        try {
            // Simulate network delay and validation
            _state.value = currentState.copy(isLoading = false)
            onSuccess()
        } catch (e: Exception) {
            _state.value = currentState.copy(isLoading = false, errorMessage = "Signup failed: ${e.message}")
            onError("Signup failed: ${e.message}")
        }
    }
    
    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }
    
    // Helper function to check if form is valid (used by UI to enable/disable signup button)
    fun isFormValid(): Boolean {
        val currentState = _state.value
        return currentState.name.isNotBlank() &&
               currentState.email.isNotBlank() &&
               currentState.password.isNotBlank() &&
               currentState.confirmPassword.isNotBlank() &&
               currentState.password == currentState.confirmPassword &&
               currentState.email.contains("@") &&
               currentState.password.length >= 6
    }
}