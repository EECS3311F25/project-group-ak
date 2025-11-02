package org.example.project.controller

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue

class TripViewComponent (
    componentContext: ComponentContext,
    private val onNavigateToAddTripView: () -> Unit,
) : ComponentContext by componentContext {
    fun onEvent(event: TripViewEvent) {
        when (event) {
            TripViewEvent.ClickButtonTripView -> onNavigateToAddTripView()
        }
    }
}
