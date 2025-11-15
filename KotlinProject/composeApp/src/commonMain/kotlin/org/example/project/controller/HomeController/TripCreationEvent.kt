package org.example.project.controller.HomeController

import org.example.project.model.dataClasses.Trip

sealed interface TripCreationEvent {
    data object ClickBack : TripCreationEvent
    data class ClickCreate(val trip: Trip) : TripCreationEvent
}