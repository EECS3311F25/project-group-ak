package org.example.project.controller.TripController

import org.example.project.model.dataClasses.Trip

sealed interface TripViewEvent {
    data object ClickButtonTripView : TripViewEvent
    data object ClickShare : TripViewEvent
    data class ClickCalendar(val trip: Trip) : TripViewEvent
    data class ClickEditEvent(val eventId: String) : TripViewEvent
}
