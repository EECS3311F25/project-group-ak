package org.example.project.presentation.calendar

import com.arkivanov.decompose.ComponentContext
import org.example.project.model.dataClasses.Event
import org.example.project.model.dataClasses.Trip
import kotlinx.datetime.LocalDate



class CalendarViewComponent(
    componentContext: ComponentContext,
    val tripId: String,
    private val onGoBack: () -> Unit,
    private val onNavigateToTripView: () -> Unit,
    private val onEditEvent: (String) -> Unit = {},
    private val onAddEvent: (LocalDate) -> Unit = {},
    private val onNavigateToNavigation: (Double, Double, String, Double, Double, String) -> Unit = { _, _, _, _, _, _ -> }
) : ComponentContext by componentContext {

    fun onBack() {
        onGoBack()
    }

    fun onEvent(event: CalendarViewEvent) {
        when (event) {
            is CalendarViewEvent.Back -> onBack()
            is CalendarViewEvent.NavigateToTrip -> onNavigateToTripView()
            is CalendarViewEvent.ClickEditEvent -> onEditEvent(event.eventId)
            is CalendarViewEvent.ClickAddEvent -> onAddEvent(event.initialDate)
            is CalendarViewEvent.ShowNavigation -> onNavigateToNavigation(
                event.startLat, event.startLng, event.startTitle,
                event.endLat, event.endLng, event.endTitle
            )
        }
    }
}

sealed class CalendarViewEvent {
    object Back : CalendarViewEvent()
    object NavigateToTrip : CalendarViewEvent()
    data class ClickEditEvent(val eventId: String) : CalendarViewEvent()
    data class ClickAddEvent(val initialDate: LocalDate) : CalendarViewEvent()
    data class ShowNavigation(
        val startLat: Double,
        val startLng: Double,
        val startTitle: String,
        val endLat: Double,
        val endLng: Double,
        val endTitle: String
    ) : CalendarViewEvent()
}