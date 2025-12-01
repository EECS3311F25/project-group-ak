package org.example.project.presentation.uishared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
actual fun NetworkImage(url: String, contentDescription: String?, modifier: Modifier) {
    // Placeholder implementation for Android
    // In production, use Coil or Glide for image loading
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Image: ${url.takeLast(20)}")
    }
}
