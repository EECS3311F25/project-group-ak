package org.example.project.presentation.home.tripcreation

import com.arkivanov.decompose.ComponentContext
import org.example.project.data.repository.UserRepository
import org.example.project.model.dataClasses.Trip

sealed interface TripCreationEvent {
    data object ClickBack : TripCreationEvent
    data class ClickCreate(val trip: Trip) : TripCreationEvent
}

class TripCreationComponent(
    componentContext: ComponentContext,
    private val onNavigateToTripView: (Trip) -> Unit,
    private val onNavigateToHomeView: () -> Unit
) : ComponentContext by componentContext {

    fun onEvent(event: TripCreationEvent) {
        println("DEBUG: TripCreationComponent.onEvent called: $event") // Add this
        when (event) {
            is TripCreationEvent.ClickBack -> {
                println("DEBUG: Navigating back to home") // Add this
                onNavigateToHomeView()
            }
            is TripCreationEvent.ClickCreate -> {
                println("DEBUG: Navigating to trip view with: ${event.trip.title}") // Add this
                onNavigateToTripView(event.trip)
            }
        }
    }
}