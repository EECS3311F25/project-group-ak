package org.example.project.controller.TripController

import com.arkivanov.decompose.ComponentContext
import org.example.project.controller.HomeController.TripCreationEvent
import org.example.project.model.dataClasses.Trip

class AddEventComponent(
    componentContext: ComponentContext,
    private val onGoBack: () -> Unit,
    private val onCreateEvent: (Trip) -> Unit,
): ComponentContext by componentContext {

    fun onEvent(event: AddEventEvent.goBack) {
        println("DEBUG: TripCreationComponent.onEvent called: $event") // Add this
        when (event) {
            is TripCreationEvent.ClickBack -> {
                println("DEBUG: Navigating back to home") // Add this
                onGoBack()
            }
            is TripCreationEvent.ClickCreate -> {
                println("DEBUG: Navigating to trip view with: ${event.trip.title}") // Add this
                onCreateEvent(event.trip)
            }
        }
    }

    fun goBack() = onGoBack()
}