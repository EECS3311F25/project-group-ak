package org.example.project.controller

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import org.example.project.model.Trip

class TripViewComponent(
    componentContext: ComponentContext,
    private val onNavigateToAddTripView: () -> Unit,
    private val onGoBack: () -> Unit, // new callback to request pop()
    private val onNavigateToAddMember: () -> Unit,
    private val onNavigateToCalendar: (Trip) -> Unit
) : ComponentContext by componentContext {
    fun onEvent(event: TripViewEvent) {
        when (event) {
            TripViewEvent.ClickButtonTripView -> onNavigateToAddTripView()
            TripViewEvent.ClickShare -> onNavigateToAddMember()
            is TripViewEvent.ClickCalendar -> onNavigateToCalendar(event.trip)
        }
    }

    // call this from UI when back is pressed
    fun onBack() {
        onGoBack()
    }
}
