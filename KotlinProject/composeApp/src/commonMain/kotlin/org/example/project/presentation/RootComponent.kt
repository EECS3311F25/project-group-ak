package org.example.project.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.replaceAll
import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate
import org.example.project.presentation.auth.login.LoginViewComponent
import org.example.project.presentation.auth.signup.SignupViewComponent
import org.example.project.presentation.home.HomeViewComponent
import org.example.project.presentation.home.tripcreation.TripCreationComponent
import org.example.project.presentation.trip.TripViewComponent
import org.example.project.presentation.trip.addevent.AddEventComponent
import org.example.project.presentation.trip.addmember.AddMemberComponent
import org.example.project.presentation.trip.edittrip.EditTripComponent
import org.example.project.presentation.calendar.CalendarViewComponent
import org.example.project.presentation.calendar.navigation.NavigationViewComponent
import org.example.project.presentation.map.MapMarker
import org.example.project.model.dataClasses.Trip

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
                    onNavigateToAddTripView = { navigation.pushNew(Configuration.AddEvent(config.tripId)) },
                    onNavigateToAddMember = { navigation.pushNew(Configuration.AddMember(config.tripId)) },
                    onGoBack = { navigation.replaceAll(Configuration.HomeView) },
                    onNavigateToEditEvent = { eventId ->
                        navigation.pushNew(Configuration.AddEvent(config.tripId, eventId))
                    },
                    onNavigateToCalendar = {
                        navigation.pop()
                        navigation.pushNew(Configuration.CalendarView(config.tripId))
                    },
                    onNavigateToEditTrip = { navigation.pushNew(Configuration.EditTrip(config.tripId)) }
                ),
                config.tripId
            )

            is Configuration.AddEvent-> Child.AddEvent(
                AddEventComponent(
                    componentContext = context,
                    onGoBack = { navigation.pop() },
                    onCreateEvent = { navigation.pop() }
                ),
                config.tripId,
                config.eventId,
                config.initialDate
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
            is Configuration.EditTrip -> Child.EditTrip(
                component = EditTripComponent(
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
                )
            )

            is Configuration.CalendarView -> Child.CalendarView(
                CalendarViewComponent(
                    componentContext = context,
                    tripId = config.tripId,
                    onGoBack = { navigation.replaceAll(Configuration.HomeView) },
                    onNavigateToTripView = { 
                        navigation.pop()
                        navigation.pushNew(Configuration.TripView(config.tripId))
                    },
                    onEditEvent = { eventId ->
                        navigation.pushNew(Configuration.AddEvent(config.tripId, eventId))
                    },
                    onAddEvent =  { initialDate ->
                        navigation.pushNew(Configuration.AddEvent(config.tripId, null, initialDate)) 
                    },
                    onNavigateToNavigation = { startLat, startLng, startTitle, endLat, endLng, endTitle ->
                        navigation.pushNew(Configuration.NavigationView(
                            startLat, startLng, startTitle, endLat, endLng, endTitle
                        ))
                    }
                ),
                config.tripId
            )
            
            is Configuration.NavigationView -> Child.NavigationView(
                component = NavigationViewComponent(
                    componentContext = context,
                    onGoBack = { navigation.pop() }
                ),
                startLocation = MapMarker(
                    latitude = config.startLat,
                    longitude = config.startLng,
                    title = config.startTitle,
                    description = "Start"
                ),
                endLocation = MapMarker(
                    latitude = config.endLat,
                    longitude = config.endLng,
                    title = config.endTitle,
                    description = "Destination"
                )
            )
        }
    }

    sealed class Child {
        data class TripView(val component: TripViewComponent, val tripId: String) : Child()
        data class AddEvent(val component: AddEventComponent, val tripId: String, val eventId: String?, val initialDate: LocalDate?) : Child()
        data class HomeView(val component: HomeViewComponent) : Child()
        data class TripCreationView(val component: TripCreationComponent) : Child()
        data class LoginView(val component : LoginViewComponent) : Child()
        data class SignupView(val component : SignupViewComponent) : Child()
        data class AddMember(val component : AddMemberComponent, val tripId: String) : Child()
        data class CalendarView(val component: CalendarViewComponent, val tripId: String) : Child()
        data class EditTrip(val component : EditTripComponent, val tripId: String) : Child()
        data class NavigationView(
            val component: NavigationViewComponent,
            val startLocation: MapMarker,
            val endLocation: MapMarker
        ) : Child()
    }

    @Serializable
    sealed class Configuration {
        @Serializable
        data class TripView(val tripId: String): Configuration()
        @Serializable
        data class AddEvent(val tripId: String, val eventId: String? = null, val initialDate: LocalDate? = null) : Configuration()
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
        @Serializable
        data class EditTrip(val tripId: String) : Configuration()
        @Serializable
        data class CalendarView(val tripId: String): Configuration()
        @Serializable
        data class NavigationView(
            val startLat: Double,
            val startLng: Double,
            val startTitle: String,
            val endLat: Double,
            val endLng: Double,
            val endTitle: String
        ): Configuration()
    }
}
