package org.example.project.controller

sealed interface HomeViewEvent {
    data object ClickButtonHomeView : HomeViewEvent
}
