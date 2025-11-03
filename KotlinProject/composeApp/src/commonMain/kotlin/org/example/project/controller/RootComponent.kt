package org.example.project.controller

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import kotlinx.serialization.Serializable
import org.example.project.model.Trip

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

    // Temporary dev helper to navigate to a page =====
    fun navigateToHome() {
        navigation.pushNew(Configuration.HomeView)
    }
    //===================================================

    private fun createChild(
        config: Configuration,
        context: ComponentContext
    ) : Child {
        return when(config) {
            is Configuration.LoginView -> Child.LoginView(
                LoginViewComponent(
                    componentContext = context,
                    onNavigateToTripView = {navigation.pushNew(Configuration.HomeView)},
                    onNavigateToSignup = {navigation.pushNew(Configuration.SignupView)}
                )
            )

            is Configuration.SignupView -> Child.SignupView(
                SignupViewComponent(
                    componentContext = context,
                    onNavigateToTripView = {navigation.pushNew(Configuration.HomeView)},
                    onNavigateToLogin = {navigation.pop()}

                )
            )
            is Configuration.TripView -> Child.TripView(
                TripViewComponent(
                    componentContext = context,
                    onNavigateToAddTripView = { navigation.pushNew(Configuration.AddTripView) },
                    onGoBack = { navigation.pop() }
                ),
                config.trip
            )
            is Configuration.AddTripView -> Child.AddTripView(
                AddTripViewComponent(
                    componentContext = context,
                    onGoBack = { navigation.pop() }
                )
            )
            is Configuration.HomeView -> Child.HomeView(
                HomeViewComponent(
                    componentContext = context,
                    onNavigateToTripView = { trip -> navigation.pushNew(Configuration.TripView(trip)) }
                )
            )
        }
    }

    sealed class Child {
        data class TripView(val component: TripViewComponent, val trip: Trip) : Child()
        data class AddTripView(val component: AddTripViewComponent) : Child()
        data class HomeView(val component: HomeViewComponent) : Child()
        data class LoginView(val component : LoginViewComponent) : Child()
        data class SignupView(val component : SignupViewComponent) : Child()
    }

    @Serializable
    sealed class Configuration {
        @Serializable
        data class TripView(val trip: Trip): Configuration()
        @Serializable
        data object AddTripView : Configuration()
        @Serializable
        data object LoginView : Configuration()
        @Serializable
        data object SignupView : Configuration()
        @Serializable
        data object HomeView : Configuration()
    }
}
