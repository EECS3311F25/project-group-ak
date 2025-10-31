package org.example.project.controller

import com.arkivanov.decompose.ComponentContext

class HomeViewComponent(
    componentContext: ComponentContext,
    private val onNavigateToTripView: () -> Unit,
) {

    fun onEvent(event: HomeViewEvent) {
        when (event) {
            HomeViewEvent.ClickButtonHomeView -> onNavigateToTripView()
        }
    }
}
