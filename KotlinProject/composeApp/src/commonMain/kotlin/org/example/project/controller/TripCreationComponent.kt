package org.example.project.controller

import com.arkivanov.decompose.ComponentContext
import org.example.project.model.Trip

class TripCreationComponent(
    componentContext: ComponentContext,
    private val onNavigateToTripView: (Trip) -> Unit,
    private val onNavigateToHomeView: () -> Unit,
) : ComponentContext by componentContext {

    fun onEvent(event: TripCreationEvent) {
        when (event) {
            is TripCreationEvent.ClickBack -> onNavigateToHomeView()
            is TripCreationEvent.ClickCreate -> onNavigateToTripView(event.trip)
        }
    }
}