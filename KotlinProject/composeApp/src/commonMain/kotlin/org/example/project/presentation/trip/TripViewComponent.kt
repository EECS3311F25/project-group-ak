package org.example.project.presentation.trip

import org.example.project.model.dataClasses.Trip
import com.arkivanov.decompose.ComponentContext
// No import needed for TripViewEvent, it's local now

sealed interface TripViewEvent {
    data object ClickButtonTripView : TripViewEvent
    data object ClickShare : TripViewEvent
    data object ClickEditTrip : TripViewEvent
    data class ClickCalendar(val trip: Trip) : TripViewEvent
    data class ClickEditEvent(val eventId: String) : TripViewEvent
    data object ClickMap : TripViewEvent
}

class TripViewComponent(
    componentContext: ComponentContext,
    private val onNavigateToAddTripView: () -> Unit,
    private val onGoBack: () -> Unit, // new callback to request pop()
    private val onNavigateToAddMember: () -> Unit,
    private val onNavigateToEditEvent: (String) -> Unit,
    private val onNavigateToEditTrip: () -> Unit,
    private val onNavigateToCalendar: () -> Unit,
    private val onNavigateToMap: () -> Unit
) : ComponentContext by componentContext {

    fun onEvent(event: TripViewEvent) {
        when (event) {
            is TripViewEvent.ClickButtonTripView -> onNavigateToAddTripView()
            is TripViewEvent.ClickShare -> onNavigateToAddMember()
            is TripViewEvent.ClickEditTrip -> onNavigateToEditTrip()
            is TripViewEvent.ClickEditEvent -> onNavigateToEditEvent(event.eventId)
            is TripViewEvent.ClickCalendar -> onNavigateToCalendar()
            is TripViewEvent.ClickMap -> onNavigateToMap()
        }
    }

    // call this from UI when back is pressed
    fun onBack() {
        onGoBack()
    }
}
