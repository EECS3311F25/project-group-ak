package org.example.project.controller

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue

class TripViewComponent(
    componentContext: ComponentContext,
    private val onNavigateToAddTripView: () -> Unit,
    private val onGoBack: () -> Unit, // new callback to request pop()
) : ComponentContext by componentContext {
    fun onEvent(event: TripViewEvent) {
        when (event) {
            TripViewEvent.ClickButtonTripView -> onNavigateToAddTripView()
        }
    }

    // call this from UI when back is pressed
    fun onBack() {
        onGoBack()
    }
}
