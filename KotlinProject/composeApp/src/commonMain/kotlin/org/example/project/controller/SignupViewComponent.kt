package org.example.project.controller

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

/*
 * SignupViewComponent - Controller for the Signup screen
 * 
 * PURPOSE:
 * - Manages signup form state (name, email, password, confirmPassword)
 * - Handles user registration logic
 * - Coordinates navigation after successful signup
 * 
 * STATE MANAGEMENT:
 * - Uses MutableValue for observable state
 * - Each field has private mutable state and public read-only access
 * - UI automatically updates when state changes via subscribeAsState()
 * 
 * HOW IT CONNECTS:
 * - RootComponent creates it with navigation callbacks
 * - SignupView reads its state and calls its functions
 * - User interactions → Events → Component → State Changes → UI Updates
 * 
 * NAVIGATION FLOW:
 * - OnSignupClick → navigates to HomeView (after successful registration)
 * - OnNavigateToLogin → goes back to LoginView (pop from stack)
 */

class SignupViewComponent (
    componentContext : ComponentContext,
    private val onNavigateToTripView : () -> Unit, 
    private val onNavigateToLogin: () -> Unit,
) : ComponentContext by componentContext{

    // === STATE: User's full name ===
    private val _name = MutableValue("")  // Private: can change internally
    val name : Value<String> = _name      // Public: read-only for UI

    // === STATE: User's email address ===
    private val _email = MutableValue("")
    val email : Value <String> = _email

    // === STATE: User's password ===
    private val _password = MutableValue("")
    val password : Value <String>  = _password

    // === STATE: Password confirmation (must match password) ===
    private val _confirmPassword = MutableValue("")
    val confirmPassword : Value<String> = _confirmPassword


    
    // === STATE UPDATERS: Called by UI when user types ===
    
    /** Updates name field - triggered when user types in name input */
    fun onNameChange(newName : String){
        _name.value = newName
    }

    /** Updates email field - triggered when user types in email input */
    fun onEmailChange(newEmail : String){
        _email.value = newEmail
    }

    /** Updates password field - triggered when user types in password input */
    fun onPasswordChange(newPassword : String){
        _password.value = newPassword
    }

    /** Updates confirmPassword field - triggered when user types in confirm password input */
    fun onConfirmPasswordChange(newPassword : String){
        _confirmPassword.value = newPassword
    }

    // === EVENT HANDLER: Processes user actions ===
    
    /**
     * Handles authentication events from SignupView
     * 
     * Events:
     * - OnSignupClick: User clicked signup button → register & navigate to home
     * - OnNavigateToLogin: User clicked "Login" link → navigate back to login screen
     */
    fun onEvent(event: AuthEvent){
        when (event) {
            AuthEvent.OnSignupClick -> {
                // will write the code after checking the protoType
                onNavigateToTripView()
            }

            AuthEvent.OnNavigateToLogin -> onNavigateToLogin()
            else -> {}
        }
    }
}

