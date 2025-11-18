package org.example.project.controller.HomeController

import org.example.project.model.dataClasses.Trip

sealed interface HomeViewEvent {
    data class ClickButtonHomeView(val trip: Trip) : HomeViewEvent
    data object ClickAddTripHomeView : HomeViewEvent
}