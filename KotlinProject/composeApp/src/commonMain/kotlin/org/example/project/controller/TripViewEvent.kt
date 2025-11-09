package org.example.project.controller

import org.example.project.model.Trip

sealed interface TripViewEvent {
    data object ClickButtonTripView : TripViewEvent
    data object ClickShare : TripViewEvent
    data class ClickCalendar(val trip: Trip) : TripViewEvent
}
