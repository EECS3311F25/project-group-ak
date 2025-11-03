package org.example.project.controller

import com.arkivanov.decompose.ComponentContext

class AddMemberComponent(
    componentContext: ComponentContext,
    private val onGoBack: () -> Unit,
    private val onAddMember: (String) -> Unit,
): ComponentContext by componentContext {

    fun goBack() = onGoBack()
    fun addMember(name: String) = onAddMember(name)
}
