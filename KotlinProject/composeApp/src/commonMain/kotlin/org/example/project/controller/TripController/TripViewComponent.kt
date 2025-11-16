package org.example.project.controller.TripController

import com.arkivanov.decompose.ComponentContext
import org.example.project.controller.TripController.TripViewEvent


class TripViewComponent(
    componentContext: ComponentContext,
    private val onNavigateToAddTripView: () -> Unit,
    private val onGoBack: () -> Unit, // new callback to request pop()
    private val onNavigateToAddMember: () -> Unit,
    private val onNavigateToEditEvent: (String) -> Unit,
    private val onNavigateToCalendar: () -> Unit
) : ComponentContext by componentContext {

    fun onEvent(event: TripViewEvent) {
        when (event) {
            TripViewEvent.ClickButtonTripView -> onNavigateToAddTripView()
            TripViewEvent.ClickShare -> onNavigateToAddMember()
            is TripViewEvent.ClickEditEvent -> onNavigateToEditEvent(event.eventId)
            is TripViewEvent.ClickCalendar -> onNavigateToCalendar()
            else -> {}
        }
    }

    // call this from UI when back is pressed
    fun onBack() {
        onGoBack()
    }
}
