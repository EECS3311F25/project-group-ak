package org.example.project.presentation.calendar.navigation

import com.arkivanov.decompose.ComponentContext

class NavigationViewComponent(
    componentContext: ComponentContext,
    private val onGoBack: () -> Unit
) : ComponentContext by componentContext {

    fun onEvent(event: NavigationViewEvent) {
        when (event) {
            is NavigationViewEvent.Close -> onGoBack()
        }
    }
}

sealed class NavigationViewEvent {
    object Close : NavigationViewEvent()
}
