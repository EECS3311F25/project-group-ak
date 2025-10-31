package org.example.project.controller

sealed interface TripViewEvent {
    data object ClickButtonTripView : TripViewEvent
}
