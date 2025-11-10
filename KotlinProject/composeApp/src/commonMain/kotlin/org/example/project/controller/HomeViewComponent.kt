package org.example.project.controller

import com.arkivanov.decompose.ComponentContext
import org.example.project.model.Trip

class HomeViewComponent(
    componentContext: ComponentContext,
    private val onNavigateToTripView: (Trip) -> Unit,
    private val onNavigateToTripCreation: () -> Unit
) : ComponentContext by componentContext {

    fun onEvent(event: HomeViewEvent) {
        when (event) {
            is HomeViewEvent.ClickButtonHomeView -> onNavigateToTripView(event.trip)
            is HomeViewEvent.ClickAddTripHomeView -> onNavigateToTripCreation()
        }
    }
}
