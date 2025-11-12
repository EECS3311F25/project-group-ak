package org.example.project.controller.TripController

import com.arkivanov.decompose.ComponentContext

class AddMemberComponent(
    componentContext: ComponentContext,
    private val onGoBack: () -> Unit
) : ComponentContext by componentContext {

    fun goBack() = onGoBack()
}