package org.example.project.controller.TripController

sealed interface TripViewEvent {
    data object ClickButtonTripView : TripViewEvent
    data object ClickShare : TripViewEvent
}