package org.example.project.controller

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.replaceAll
import kotlinx.serialization.Serializable
import org.example.project.controller.AuthController.LoginViewComponent
import org.example.project.controller.AuthController.SignupViewComponent
import org.example.project.controller.HomeController.HomeViewComponent
import org.example.project.controller.HomeController.TripCreationComponent
import org.example.project.controller.TripController.AddEventComponent
import org.example.project.controller.TripController.AddMemberComponent
import org.example.project.controller.TripController.TripViewComponent
import org.example.project.data.source.LocalUserDataSource
import org.example.project.data.repository.UserRepository

class RootComponent(
    componentContext: ComponentContext,
): ComponentContext by componentContext {

    private val navigation = StackNavigation<Configuration>()
    val childStack = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        initialConfiguration = Configuration.LoginView,
        handleBackButton = true,
        childFactory = ::createChild
    )

    private val userRepository = UserRepository(LocalUserDataSource())

    fun navigateToHome() {
        navigation.pushNew(Configuration.HomeView)
    }

    private fun createChild(
        config: Configuration,
        context: ComponentContext
    ) : Child {
        return when(config) {
            is Configuration.LoginView -> Child.LoginView(
                LoginViewComponent(
                    componentContext = context,
                    onNavigateToTripView = { navigation.pushNew(Configuration.HomeView) },
                    onNavigateToSignup = { navigation.pushNew(Configuration.SignupView) }
                )
            )

            is Configuration.SignupView -> Child.SignupView(
                SignupViewComponent(
                    componentContext = context,
                    onNavigateToTripView = { navigation.pushNew(Configuration.HomeView) },
                    onNavigateToLogin = { navigation.pop() }
                )
            )

            is Configuration.TripView -> Child.TripView(
                TripViewComponent(
                    componentContext = context,
                    onNavigateToAddTripView = { navigation.pushNew(Configuration.AddTripView(config.tripId)) },
                    onNavigateToAddMember = { navigation.pushNew(Configuration.AddMember(config.tripId)) },
                    onGoBack = { navigation.replaceAll(Configuration.HomeView) }
                ),
                config.tripId
            )

            is Configuration.AddTripView -> Child.AddTripView(
                AddEventComponent(
                    componentContext = context,
                    onGoBack = { navigation.pop() }
                ),
                config.tripId
            )

            is Configuration.HomeView -> Child.HomeView(
                HomeViewComponent(
                    componentContext = context,
                    onNavigateToTripView = { trip -> navigation.pushNew(Configuration.TripView(trip.id)) },
                    onNavigateToTripCreation = { navigation.pushNew(Configuration.TripCreationView) }
                )
            )

            is Configuration.AddMember -> Child.AddMember(
                component = AddMemberComponent(
                    componentContext = context,
                    onGoBack = { navigation.pop() }
                ),
                tripId = config.tripId
            )

            is Configuration.TripCreationView -> Child.TripCreationView(
                TripCreationComponent(
                    componentContext = context,
                    onNavigateToTripView = { trip ->
                        navigation.pop()
                        navigation.pushNew(Configuration.TripView(trip.id))
                    },
                    onNavigateToHomeView = { navigation.pop() },
                    userRepository = userRepository
                )
            )
        }
    }

    sealed class Child {
        data class TripView(val component: TripViewComponent, val tripId: String) : Child()
        data class AddTripView(val component: AddEventComponent, val tripId: String) : Child()
        data class HomeView(val component: HomeViewComponent) : Child()
        data class TripCreationView(val component: TripCreationComponent) : Child()
        data class LoginView(val component : LoginViewComponent) : Child()
        data class SignupView(val component : SignupViewComponent) : Child()
        data class AddMember(val component : AddMemberComponent, val tripId: String) : Child()
    }

    @Serializable
    sealed class Configuration {
        @Serializable
        data class TripView(val tripId: String): Configuration()
        @Serializable
        data class AddTripView(val tripId: String) : Configuration()
        @Serializable
        data object LoginView : Configuration()
        @Serializable
        data object SignupView : Configuration()
        @Serializable
        data object HomeView : Configuration()
        @Serializable
        data class AddMember(val tripId: String) : Configuration()
        @Serializable
        data object TripCreationView : Configuration()
    }
}
