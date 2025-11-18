package org.example.project.presentation.auth

sealed interface AuthEvent {
    data object OnLoginClick : AuthEvent
    data object OnSignupClick : AuthEvent
    data object OnNavigateToSignup : AuthEvent
    data object OnNavigateToLogin : AuthEvent
}