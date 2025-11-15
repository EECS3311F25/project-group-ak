package org.example.project.controller.TripController

sealed interface TripViewEvent {
    data object ClickButtonTripView : TripViewEvent
    data object ClickShare : TripViewEvent
    data class ClickEditEvent(val eventId: String) : TripViewEvent
}
