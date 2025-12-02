package org.example.project.presentation.home

import com.arkivanov.decompose.ComponentContext
import org.example.project.presentation.home.HomeViewModel
import org.example.project.model.dataClasses.Trip

sealed interface HomeViewEvent {
    data class ClickButtonHomeView(val trip: Trip) : HomeViewEvent
    data object ClickAddTripHomeView : HomeViewEvent
}

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

    /**
     * Helper to delegate deletion from the UI layer through a ViewModel.
     * Keeps the controller API stable while allowing the view to call into the ViewModel.
     *
     * Usage from a composable:
     *   component.deleteTrip(viewModel, trip.title)
     */
    fun deleteTrip(viewModel: HomeViewModel, tripTitle: String) {
        viewModel.deleteTrip(tripTitle)
    }
}

