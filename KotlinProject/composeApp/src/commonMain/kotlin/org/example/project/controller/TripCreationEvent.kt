package org.example.project.controller

import org.example.project.model.Trip

sealed interface TripCreationEvent {
    data object ClickBack : TripCreationEvent
    data class ClickCreate(val trip: Trip) : TripCreationEvent
}