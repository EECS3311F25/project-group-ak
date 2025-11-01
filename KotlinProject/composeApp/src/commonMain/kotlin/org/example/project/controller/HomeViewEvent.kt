package org.example.project.controller

import org.example.project.model.Trip

sealed interface HomeViewEvent {
    data class ClickButtonHomeView(val trip: Trip) : HomeViewEvent
}
