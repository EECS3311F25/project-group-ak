package org.example.project.presentation.map

import com.arkivanov.decompose.ComponentContext
import org.example.project.model.dataClasses.Location

class MapViewComponent(
    componentContext: ComponentContext,
    val tripId: String,
    private val onGoBack: () -> Unit,
    private val onNavigateToTripView: () -> Unit,
    private val onNavigateToCalendarView: () -> Unit
) : ComponentContext by componentContext {

    fun onBack() {
        onGoBack()
    }

    fun onEvent(event: MapViewEvent) {
        when (event) {
            is MapViewEvent.Back -> onBack()
            is MapViewEvent.NavigateToTrip -> onNavigateToTripView()
            is MapViewEvent.NavigateToCalendar -> onNavigateToCalendarView()
        }
    }
}

sealed class MapViewEvent {
    object Back : MapViewEvent()
    object NavigateToTrip : MapViewEvent()
    object NavigateToCalendar : MapViewEvent()
}
