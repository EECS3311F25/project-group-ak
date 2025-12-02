package org.example.project.presentation.map.uicomponents

import androidx.compose.runtime.Composable

/**
 * Platform-specific day dropdown overlay for map view
 * This is needed because the dropdown must appear on top of the fixed-position map
 */
@Composable
expect fun RenderDayDropdownOverlay(
    days: List<String>,
    selectedDayIndex: Int,
    onDaySelected: (Int) -> Unit,
    onCleanup: () -> Unit
)
