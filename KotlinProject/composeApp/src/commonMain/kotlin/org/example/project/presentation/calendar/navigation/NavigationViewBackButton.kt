package org.example.project.presentation.calendar.navigation

import androidx.compose.runtime.Composable

/**
 * Platform-specific back button overlay for navigation view
 * This is needed because the back button must appear on top of the fixed-position map
 */
@Composable
expect fun RenderBackButtonOverlay(
    onBack: () -> Unit,
    onCleanup: () -> Unit
)
