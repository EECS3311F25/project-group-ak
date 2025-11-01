package org.example.project.view.AuthView

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.example.project.controller.SignupViewComponent

@Composable
fun SignupView(component: SignupViewComponent, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Signup View - Coming Soon", style = MaterialTheme.typography.headlineMedium)
    }
}
