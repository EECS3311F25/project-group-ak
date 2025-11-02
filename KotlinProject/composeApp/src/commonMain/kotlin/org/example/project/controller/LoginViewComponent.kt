package org.example.project.controller

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class LoginViewComponent(
    componentContext: ComponentContext,
    private val onNavigateToTripView: () -> Unit,
    private val onNavigateToSignup: () -> Unit,
) : ComponentContext by componentContext {

    private val _email = MutableValue("")
    val email: Value<String> = _email

    private val _password = MutableValue("")
    val password: Value<String> = _password

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun onEvent(event: AuthEvent) {
        when (event) {
            AuthEvent.OnLoginClick -> {
                // TODO: Add actual authentication logic
                onNavigateToTripView()
            }
            AuthEvent.OnNavigateToSignup -> onNavigateToSignup()
            else -> {}
        }
    }
}
