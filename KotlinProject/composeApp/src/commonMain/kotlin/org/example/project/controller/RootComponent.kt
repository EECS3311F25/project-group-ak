package org.example.project.controller

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import kotlinx.serialization.Serializable

class RootComponent(
    componentContext: ComponentContext,
): ComponentContext by componentContext {

    private val navigation = StackNavigation<Configuration>()
    val childStack = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        initialConfiguration = Configuration.LoginView, // changed the configuration from TripView to LoginView
        handleBackButton = true,
        childFactory = ::createChild
    )

    private fun createChild(
        config: Configuration,
        context: ComponentContext
    ) : Child {
        return when(config) {

            // Adding LoginView and SignupView

            Configuration.LoginView -> Child.LoginView(
                LoginViewComponent(
                    componentContext = context,
                    onNavigateToTripView = {navigation.pushNew(Configuration.TripView)},
                    onNavigateToSignup = {navigation.pushNew(Configuration.SignupView)}
                )
            )

            Configuration.SignupView -> Child.SignupView(
                SignupViewComponent(
                    componentContext = context,
                    onNavigateToTripView = {navigation.pushNew(Configuration.TripView)},
                    onNavigateToLogin = {navigation.pop()}

                )
            )



        // Trip view
            Configuration.TripView -> Child.TripView(
                TripViewComponent(
                    componentContext = context,
                    onNavigateToAddTripView = { navigation.pushNew(Configuration.AddTripView) }
                )
            )
            is Configuration.AddTripView -> Child.AddTripView(
                AddTripViewComponent(
                    componentContext = context,
                    onGoBack = { navigation.pop() }
                )
            )
        }
    }

    sealed class Child {
        data class TripView(val component: TripViewComponent) : Child()
        data class AddTripView(val component: AddTripViewComponent) : Child()
        data class LoginView(val component : LoginViewComponent) : Child()
        data class SignupView(val component : SignupViewComponent) : Child()
    }

    @Serializable
    sealed class Configuration {

        @Serializable
        data object TripView: Configuration()
        @Serializable
        data object AddTripView : Configuration()
        @Serializable
        data object LoginView : Configuration()
        @Serializable
        data object SignupView : Configuration()
    }
}
