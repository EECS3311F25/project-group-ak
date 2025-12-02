package org.example.project.presentation.trip.edittrip

import com.arkivanov.decompose.ComponentContext

class EditTripComponent(
    componentContext: ComponentContext,
    private val onGoBack: () -> Unit
) : ComponentContext by componentContext {

    fun goBack() = onGoBack()
}
