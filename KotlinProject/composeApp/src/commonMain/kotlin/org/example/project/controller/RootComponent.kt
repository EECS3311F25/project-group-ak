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
        initialConfiguration = Configuration.HomeView,
        handleBackButton = true,
        childFactory = ::createChild
    )

    private fun createChild(
        config: Configuration,
        context: ComponentContext
    ) : Child {
        return when(config) {
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
            is Configuration.HomeView -> Child.HomeView(
                HomeViewComponent(
                    componentContext = context,
                    onNavigateToTripView = { navigation.pushNew(Configuration.TripView) }
                )
            )
        }
    }

    sealed class Child {
        data class TripView(val component: TripViewComponent) : Child()
        data class AddTripView(val component: AddTripViewComponent) : Child()
        data class HomeView(val component: HomeViewComponent) : Child()
    }

    @Serializable
    sealed class Configuration {
        @Serializable
        data object TripView: Configuration()
        @Serializable
        data object AddTripView : Configuration()
        @Serializable
        data object HomeView : Configuration()
    }
}
