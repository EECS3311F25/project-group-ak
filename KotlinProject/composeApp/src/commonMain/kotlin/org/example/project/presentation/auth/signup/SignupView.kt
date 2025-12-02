package org.example.project.presentation.auth.signup

// Layout components - Column, Row, Box
import androidx.compose.foundation.layout.*

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

// UI modifiers and alignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier 
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Decompose - Subscribe to state changes (connects component state to UI)
import com.arkivanov.decompose.extensions.compose.subscribeAsState

import org.example.project.presentation.auth.AuthEvent
import org.example.project.presentation.auth.signup.SignupViewComponent
import org.example.project.presentation.auth.uicomponents.AuthButton
import org.example.project.presentation.auth.uicomponents.InputField

/*
 * SignupView - UI layer for the Signup/Registration screen
 * 
 * PURPOSE:
 * - Displays registration form (name, email, password, confirm password)
 * - Validates that passwords match before enabling signup
 * - Provides navigation link back to login
 * - Sends user actions to SignupViewComponent
 * 
 * STATE OBSERVATION:
 * - Uses subscribeAsState() to observe component's state
 * - Automatically recomposes when any field changes
 * - All 4 input fields are two-way bound to component state
 * 
 * FORM VALIDATION:
 * - Checks all fields are filled (not blank)
 * - Ensures password matches confirmPassword
 * - Disables signup button until validation passes
 * 
 * DATA FLOW:
 * User types → InputField.onValueChange → component.onNameChange() 
 * → _name.value updates → name Value emits → subscribeAsState() detects
 * → SignupView recomposes → TextField shows new value
 * 
 * USER INTERACTIONS:
 * - Type in any field → Updates component state
 * - Click Sign Up button → Sends AuthEvent.OnSignupClick to component
 * - Click "Login" link → Sends AuthEvent.OnNavigateToLogin to component
 * 
 * @param component The SignupViewComponent that manages logic and state
 * @param modifier Optional Compose modifier for customization
 */
@Composable
fun SignupView(component : SignupViewComponent, modifier : Modifier=Modifier) {

    // === SUBSCRIBE TO STATE: Observe all form fields from component ===
    // subscribeAsState() converts Decompose's Value<T> → Compose's State<T>
    // UI automatically recomposes when these values change
    val name by component.name.subscribeAsState()
    val email by component.email.subscribeAsState()
    val password by component.password.subscribeAsState()
    val confirmPassword by component.confirmPassword.subscribeAsState()

    
    // === LAYOUT: Vertical column, centered on screen ===
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

    // === HEADER: Title and subtitle ===
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

    // === INPUT: Name field ===
    InputField(
        value = name,
        onValueChange = {component.onNameChange(it)},
        label = "Full Name",
        modifier = Modifier.padding(bottom= 16.dp)
    )

    // === INPUT: Email field ===
    InputField(
        value = email,
        onValueChange ={component.onEmailChange(it)},
        label = "Email",
        keyboardType = KeyboardType.Email,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    // === INPUT: Password field (masked) ===
    InputField(
        value = password,
        onValueChange = {component.onPasswordChange(it)},
        label =  "Password",
        isPassword = true,
        keyboardType = KeyboardType.Password, 
        modifier = Modifier.padding(bottom = 16.dp)
    )

    // === INPUT: Confirm password field (must match password) ===
    InputField(
        value = confirmPassword,
        onValueChange = {component.onConfirmPasswordChange(it)},
        label = "Confirm Password",
        isPassword = true, 
        keyboardType = KeyboardType.Password,
        modifier = Modifier.padding(bottom = 24.dp)
    )

    // === FORM VALIDATION ===
    // Button only enabled when:
    // 1. All fields have content (not blank)
    // 2. Password matches confirmPassword
    val isFormValid = name.isNotBlank() &&
                    email.isNotBlank() &&
                    password.isNotBlank() &&
                    password == confirmPassword
    

    // === ACTION: Sign up button ===
    // Sends OnSignupClick event when pressed
    // Disabled until form is valid
    AuthButton(
        text = "Sign Up",
        onClick = {component.onEvent(AuthEvent.OnSignupClick)},
        enabled = isFormValid // only clickable when form is valid 
    )
    Spacer(modifier = Modifier.height(16.dp))

    // === NAVIGATION: Link back to login screen ===
    Row (verticalAlignment = Alignment.CenterVertically) {
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






