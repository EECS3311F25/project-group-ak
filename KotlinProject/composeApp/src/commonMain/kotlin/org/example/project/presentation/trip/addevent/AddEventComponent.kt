package org.example.project.presentation.trip.addevent

import com.arkivanov.decompose.ComponentContext
import org.example.project.model.dataClasses.Trip

interface AddEventEvent {
    data object goBack : AddEventEvent
    data class clickCreate(val trip: Trip) : AddEventEvent
}

class AddEventComponent(
    componentContext: ComponentContext,
    private val onGoBack: () -> Unit,
    private val onCreateEvent: (Trip) -> Unit,
): ComponentContext by componentContext {

    fun onEvent(event: AddEventEvent) {
        println("DEBUG: AddEventComponent.onEvent called: $event")
        when (event) {
            is AddEventEvent.goBack -> {
                println("DEBUG: Navigating back to home")
                onGoBack()
            }
            is AddEventEvent.clickCreate -> {
                println("DEBUG: Navigating to trip view with: ${event.trip.title}")
                onCreateEvent(event.trip)
            }
        }
    }

    fun goBack() = onGoBack()
}