package org.example.project.presentation.auth.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class LoginViewModel : ViewModel() {
    
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()
    
    fun updateEmail(newEmail: String) {
        _state.value = _state.value.copy(email = newEmail, errorMessage = null)
    }
    
    fun updatePassword(newPassword: String) {
        _state.value = _state.value.copy(password = newPassword, errorMessage = null)
    }
    
    fun login(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val currentState = _state.value
        if (currentState.email.isBlank() || currentState.password.isBlank()) {
            onError("Email and password are required")
            return
        }
        
        _state.value = currentState.copy(isLoading = true, errorMessage = null)
        
        // TODO: Add real authentication logic here
        // For now, simulate a simple validation
        if (currentState.email.contains("@") && currentState.password.length >= 6) {
            // Simulate successful login
            _state.value = currentState.copy(isLoading = false)
            onSuccess()
        } else {
            // Simulate login failure
            val errorMsg = when {
                !currentState.email.contains("@") -> "Please enter a valid email address"
                currentState.password.length < 6 -> "Password must be at least 6 characters"
                else -> "Invalid email or password"
            }
            _state.value = currentState.copy(isLoading = false, errorMessage = errorMsg)
            onError(errorMsg)
        }
    }
    
    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }
}