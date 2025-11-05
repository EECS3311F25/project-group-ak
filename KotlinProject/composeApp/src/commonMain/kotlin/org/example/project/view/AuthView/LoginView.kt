package org.example.project.view.AuthView

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.arkivanov.decompose.extensions.compose.subscribeAsState

import org.example.project.AppConstants
import org.example.project.controller.AuthEvent
import org.example.project.controller.LoginViewComponent
// Reusable authentication UI components
import org.example.project.view.AuthView.AuthComponents.AuthButton
import org.example.project.view.AuthView.AuthComponents.InputField

import com.sunildhiman90.kmauth.core.KMAuthInitializer
import com.sunildhiman90.kmauth.google.KMAuthGoogle
import com.sunildhiman90.kmauth.core.KMAuthConfig
import kotlinx.coroutines.launch

/*
 * LoginView - UI layer for the Login screen
 * 
 * PURPOSE:
 * - Displays login form (email + password)
 * - Provides navigation link to signup
 * - Sends user actions to LoginViewComponent
 * 
 * STATE OBSERVATION:
 * - Uses subscribeAsState() to observe component's state
 * - Automatically recomposes when state changes
 * - Email and password fields are two-way bound to component state
 * 
 * DATA FLOW:
 * User types → InputField.onValueChange → component.onEmailChange() 
 * → _email.value updates → email Value emits → subscribeAsState() detects
 * → LoginView recomposes → TextField shows new value
 * 
 * USER INTERACTIONS:
 * - Type email/password → Updates component state
 * - Click Login button → Sends AuthEvent.OnLoginClick to component
 * - Click "Sign Up" link → Sends AuthEvent.OnNavigateToSignup to component
 * 
 * @param component The LoginViewComponent that manages logic and state
 * @param modifier Optional Compose modifier for customization
 */
@Composable
fun LoginView(component : LoginViewComponent, modifier : Modifier = Modifier)
{
    // === SUBSCRIBE TO STATE: Observe component's email and password ===
    // When these values change in component, UI automatically updates
    val email by component.email.subscribeAsState()
    val password by component.password.subscribeAsState()


    // === LAYOUT: Vertical column, centered on screen ===
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // === HEADER: Welcome message ===
        Text(
            text = "Welcome Back",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Sign in to Continue",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // === INPUT: Email field ===
        // Two-way binding: value from component, changes sent back via onEmailChange
        InputField(
            value = email,
            onValueChange = {component.onEmailChange(it)},
            label = "Email",
            keyboardType = KeyboardType.Email,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // === INPUT: Password field (masked) ===
        InputField(
            value = password,
            onValueChange = {component.onPasswordChange(it)},
            label = "Password",
            isPassword = true,
            keyboardType = KeyboardType.Password,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // === ACTION: Login button ===
        // Disabled until both fields have content
        // Sends OnLoginClick event when pressed
        AuthButton(
            text  = "Login",
            onClick = {component.onEvent(AuthEvent.OnLoginClick)},
            enabled = email.isNotBlank() && password.isNotBlank()
        )

        Spacer(modifier = Modifier.height(16.dp))

        KMAuthInitializer.initialize(
            config = KMAuthConfig.forGoogle(AppConstants.WEB_CLIENT_ID))

        val googleAuthManager = KMAuthGoogle.googleAuthManager

        val scope = rememberCoroutineScope()

        //  Google signin button
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(
                onClick = {
                    scope.launch {
                        val result = googleAuthManager.signIn()
                    }
                    component.onEvent(AuthEvent.OnLoginClick)
                }) {
                Text("Continue with Google")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // === NAVIGATION: Link to signup screen ===
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Don't have an account?",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            TextButton( onClick = {component.onEvent(AuthEvent.OnNavigateToSignup)})
            {
                Text("Sign Up")
            }
        }
    }
}


