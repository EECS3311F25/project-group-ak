package org.example.project.controller

import com.arkivanov.decompose.ComponentContext
import org.example.project.model.Event
import org.example.project.model.Trip
import kotlinx.datetime.LocalDate

class CalendarViewComponent(
    componentContext: ComponentContext,
    private val onGoBack: () -> Unit,
    private val onNavigateToTripView: () -> Unit
) : ComponentContext by componentContext {

    fun onBack() {
        onGoBack()
    }

    fun onEvent(event: CalendarViewEvent) {
        when (event) {
            CalendarViewEvent.Back -> onBack()
            CalendarViewEvent.NavigateToTrip -> onNavigateToTripView()
        }
    }
}

sealed class CalendarViewEvent {
    object Back : CalendarViewEvent()
    object NavigateToTrip : CalendarViewEvent()
}