package org.example.project.controller

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value



class SignupViewComponent (
    componentContext : ComponentContext,
    private val onNavigateToTripView : () -> Unit, private val onNavigateToLogin: () -> Unit,
) : ComponentContext by componentContext{

    private val _name = MutableValue("")
    val name : Value<String> = _name


    private val _email = MutableValue("")
    val email : Value <String> = _email

    private val _password = MutableValue("")
    val password : Value <String>  = _password

    private val _confirmPassword = MutableValue("")
    val confirmPassword : Value<String> = _confirmPassword


    
    fun onNameChange(newName : String){
        _name.value = newName
    }

    fun onEmailChange(newEmail : String){
        _email.value = newEmail
    }


    fun onPasswordChange(newPassword : String){
        _password.value = newPassword
    }


    fun onConfirmPasswordChange(newPassword : String){
        _confirmPassword.value = newPassword
    }


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

