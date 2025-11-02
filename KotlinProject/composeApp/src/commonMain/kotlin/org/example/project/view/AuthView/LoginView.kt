package org.example.project.view.AuthView

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


import com.arkivanov.decompose.extensions.compose.subscribeAsState
import org.example.project.controller.AuthEvent
import org.example.project.controller.LoginViewComponent

// Haven't write the code for these 2 of them
import org.example.project.view.AuthView.AuthComponents.AuthButton
import org.example.project.view.AuthView.AuthComponents.InputField

@Composable

fun LoginView(component : LoginViewComponent, modifier : Modifier = Modifier)
{
    val email by component.email.subscribeAsState()
    val password by component.password.subscribeAsState()


    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )

    {
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

        InputField(
            value = email,
            onValueChange = {component.onEmailChange(it)},
            label = "Email",
            keyboardType = KeyboardType.Email,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        InputField(
            value = password,
            onValueChange = {component.onPasswordChange(it)},
            label = "Password",
            isPassword = true,
            keyboardType = KeyboardType.Password,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        AuthButton(
            text  = "Login",
            onClick = {component.onEvent(AuthEvent.OnLoginClick)},
            enabled = email.isNotBlank() && password.isNotBlank()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically)
        { // Row
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


