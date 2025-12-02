package org.example.project.presentation.trip.addmember

import com.arkivanov.decompose.ComponentContext

interface AddMemberEvent {
}

class AddMemberComponent(
    componentContext: ComponentContext,
    private val onGoBack: () -> Unit
) : ComponentContext by componentContext {

    fun goBack() = onGoBack()
}