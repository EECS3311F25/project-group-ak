package org.example.project.view

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color


@Composable
@Preview
fun App() {
    MaterialTheme {
        Home()
    }
}

val mainGreen = Color(red = 0.5f, green = 1f, blue = 0.6f)