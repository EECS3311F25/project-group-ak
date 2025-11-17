package org.example.project.controller

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
    private val onAddEvent: (LocalDate) -> Unit = {}
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
        }
    }
}

sealed class CalendarViewEvent {
    object Back : CalendarViewEvent()
    object NavigateToTrip : CalendarViewEvent()
    data class ClickEditEvent(val eventId: String) : CalendarViewEvent()
    data class ClickAddEvent(val initialDate: LocalDate) : CalendarViewEvent()
}