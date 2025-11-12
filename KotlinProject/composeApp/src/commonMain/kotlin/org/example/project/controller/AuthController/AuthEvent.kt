package org.example.project.controller.AuthController

sealed interface AuthEvent {
    data object OnLoginClick : AuthEvent
    data object OnSignupClick : AuthEvent
    data object OnNavigateToSignup : AuthEvent
    data object OnNavigateToLogin : AuthEvent
}