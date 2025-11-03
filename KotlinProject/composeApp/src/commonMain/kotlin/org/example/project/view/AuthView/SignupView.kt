
package org.example.project.view.AuthView


// Layout component - column, Row, Box
import androidx.compose.foundation.layout.*


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

//UI Modifiers and Alignment - Play around with different variations
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier 
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// Decompose - Subscribe to state changes (Connects Components state to UI)
import com.arkivanov.decompose.extensions.compose.subscribeAsState

import org.example.project.controller.AuthEvent
import org.example.project.controller.SignupViewComponent
import org.example.project.view.AuthView.AuthComponents.AuthButton
import org.example.project.view.AuthView.AuthComponents.InputField

@Composable
fun SignupView(component : SignupViewComponent, modifier : Modifier=Modifier) {

    val name by component.name.subscribeAsState()
    val email by component.email.subscribeAsState()
    val password by component.password.subscribeAsState()
    val confirmPassword by component.confirmPassword.subscribeAsState()

    // subscribeAsState Converts Deceompose's Value<T> -> Compose's State<T> : UI Automatically

    
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

    Text(
        text = "Create Account",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    
    Text(
        text = "Sign up to get started",
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom= 48.dp)
    )

    InputField(
        value = name,
        onValueChange = {component.onNameChange(it)},
        label = "Full Name",
        modifier = Modifier.padding(bottom= 16.dp)
    )

    InputField(
        value = email,
        onValueChange ={component.onEmailChange(it)},
        label = "Email",
        keyboardType = KeyboardType.Email,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    InputField(
        value = password,
        onValueChange = {component.onPasswordChange(it)},
        label =  "Password",
        isPassword = true,
        keyboardType = KeyboardType.Password, 
        modifier = Modifier.padding(bottom = 16.dp)
    )

    InputField(
        value = confirmPassword,
        onValueChange = {component.onConfirmPasswordChange(it)},
        label = "Confirm Password",
        isPassword = true, 
        keyboardType = KeyboardType.Password,
        modifier = Modifier.padding(bottom = 24.dp)
    )

    // check for form Validations
    val isFormValid = name.isNotBlank() &&
                    email.isNotBlank() &&
                    password.isNotBlank() &&
                    password == confirmPassword
    

    // Sign up Button
    AuthButton(
        text = "Sign Up",
        onClick = {component.onEvent(AuthEvent.OnSignupClick)},
        enabled = isFormValid // only clickable when form is valid 
    )
    Spacer(modifier = Modifier.height(16.dp))


    
    Row (verticalAlignment = Alignment.CenterVertically)
    {
        Text(
            text = "Already have an account?",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        TextButton(
            onClick = {component.onEvent(AuthEvent.OnNavigateToLogin)}
        ){
            Text("Login")
        }
    }
    }
}




// Navigation Link






