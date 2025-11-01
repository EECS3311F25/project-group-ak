package org.example.project.controller

import com.arkivanov.decompose.ComponentContext

class AddTripViewComponent(
    componentContext: ComponentContext,
    private val onGoBack: () -> Unit,
): ComponentContext by componentContext {

    fun goBack() = onGoBack()
}
