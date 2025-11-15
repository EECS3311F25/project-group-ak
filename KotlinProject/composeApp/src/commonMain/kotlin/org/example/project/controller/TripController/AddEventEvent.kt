package org.example.project.controller.TripController

import org.example.project.model.dataClasses.Trip

interface AddEventEvent {
    data object goBack : AddEventEvent
    data class clickCreate(val trip: Trip) : AddEventEvent
}