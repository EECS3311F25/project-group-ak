package org.example.project.presentation.calendar.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * JVM implementation uses regular Compose UI
 */
@Composable
actual fun RenderBackButtonOverlay(
    onBack: () -> Unit,
    onCleanup: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .background(Color.White, shape = CircleShape)
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Go back"
            )
        }
    }
}
