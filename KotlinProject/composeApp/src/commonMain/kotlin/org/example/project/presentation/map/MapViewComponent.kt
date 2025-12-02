package org.example.project.presentation.map

import com.arkivanov.decompose.ComponentContext
import org.example.project.model.dataClasses.Location

class MapViewComponent(
    componentContext: ComponentContext,
    val tripId: String,
    private val onGoBack: () -> Unit,
    private val onNavigateToTripView: () -> Unit,
    private val onNavigateToCalendarView: () -> Unit,
    private val onNavigateToNavigation: (Location, Location, String, String) -> Unit = { _, _, _, _ -> }
) : ComponentContext by componentContext {

    fun onBack() {
        onGoBack()
    }

    fun onEvent(event: MapViewEvent) {
        when (event) {
            is MapViewEvent.Back -> onBack()
            is MapViewEvent.NavigateToTrip -> onNavigateToTripView()
            is MapViewEvent.NavigateToCalendar -> onNavigateToCalendarView()
            is MapViewEvent.NavigateToNavigation -> onNavigateToNavigation(
                event.startLocation,
                event.endLocation,
                event.startTitle,
                event.endTitle
            )
        }
    }
}

sealed class MapViewEvent {
    object Back : MapViewEvent()
    object NavigateToTrip : MapViewEvent()
    object NavigateToCalendar : MapViewEvent()
    data class NavigateToNavigation(
        val startLocation: Location,
        val endLocation: Location,
        val startTitle: String,
        val endTitle: String
    ) : MapViewEvent()
}
