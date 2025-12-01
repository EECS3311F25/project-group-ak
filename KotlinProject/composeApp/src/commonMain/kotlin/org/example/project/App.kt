package org.example.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import org.example.project.model.LightColorScheme
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import org.example.project.presentation.home.HomeViewModel
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.example.project.data.remote.RemoteLocationDataSource
import org.example.project.presentation.RootComponent
import org.example.project.presentation.trip.TripView
import org.example.project.presentation.home.HomeView
import org.example.project.presentation.home.tripcreation.TripCreationView
import org.example.project.presentation.trip.addevent.AddEvent
import org.example.project.presentation.trip.addmember.AddMember
import org.example.project.presentation.trip.edittrip.EditTrip
import org.example.project.presentation.auth.login.LoginView
import org.example.project.presentation.auth.signup.SignupView
import org.example.project.presentation.calendar.CalendarView
import org.example.project.presentation.home.tripcreation.TripCreationViewModel
import org.example.project.presentation.calendar.CalendarViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.example.project.data.repository.TripRepository
import org.example.project.data.repository.UserRepository
import org.example.project.data.remote.RemoteTripDataSource
import org.example.project.data.remote.RemoteUserDataSource
import org.example.project.data.repository.LocationRepository
import org.example.project.presentation.trip.TripViewModel
import org.example.project.presentation.trip.addmember.AddMemberViewModel
import org.example.project.presentation.trip.addevent.AddEventViewModel
import org.example.project.presentation.trip.edittrip.EditTripViewModel
import org.example.project.presentation.ApiTestView
import org.example.project.presentation.calendar.navigation.NavigationView
import org.example.project.presentation.map.MapView
import org.example.project.presentation.map.MapViewModel

@Composable
/*
 * App - Main entry point that renders the current screen
 * 
 * NAVIGATION FLOW:
 * 1. App starts → RootComponent created with initialConfiguration = LoginView
 * 2. LoginView displayed → User enters credentials → Clicks Login
 * 3. LoginViewComponent handles event → Calls onNavigateToTripView()
 * 4. RootComponent pushes HomeView → childStack updates
 * 5. App recomposes → Matches new Child.HomeView → Renders HomeView
 * 
 * AUTHENTICATION FLOW:
 * - LoginView is the entry point (first screen user sees)
 * - User can navigate LoginView ↔ SignupView
 * - After successful auth → Goes to HomeView
 * 
 * @param root The RootComponent that manages all navigation
 */ 
fun App(root: RootComponent) {
    // 🔥 Create shared repository instances at App level
    val userDataSource = remember { RemoteUserDataSource() }
    val tripRepository = remember { TripRepository(RemoteTripDataSource(userDataSource)) }
    val userRepository = remember { UserRepository(userDataSource) }
    val locationRepository = remember { LocationRepository(RemoteLocationDataSource()) }
    
    MaterialTheme(
        colorScheme = LightColorScheme
    ) {
        // Subscribe to navigation state changes
        val childStack by root.childStack.subscribeAsState()
        
        // Children handles screen transitions with slide animation
        Children(
            stack = childStack,
            animation = stackAnimation(slide())
        ) { child ->
            // Render the appropriate view based on current navigation state
            when (val instance = child.instance) {



                // =============================================================================================
                // === AUTHENTICATION SCREENS ==================================================================
                // =============================================================================================
                // LoginView: Entry point, shows login form
                is RootComponent.Child.LoginView -> LoginView(instance.component)
                // SignupView: Registration form, accessible from LoginView
                is RootComponent.Child.SignupView -> SignupView(instance.component)
                // =============================================================================================
                // === AUTHENTICATION SCREENS ==================================================================
                // =============================================================================================



                // =============================================================================================
                // === HOME SCREENS ============================================================================
                // =============================================================================================
                // 🔥 Create HomeViewModel and pass it to HomeView
                is RootComponent.Child.HomeView -> {
                    val homeViewModel: HomeViewModel = viewModel { 
                        HomeViewModel(tripRepository, userRepository) 
                    }
                    HomeView(
                        component = instance.component,
                        viewModel = homeViewModel
                    )
                }
                
                // 🔥 Pass shared repositories to TripCreationView
                is RootComponent.Child.TripCreationView -> {
                    val tripCreationViewModel: TripCreationViewModel = viewModel { 
                        TripCreationViewModel(tripRepository, userRepository)
                    }
                    TripCreationView(
                        component = instance.component,
                        viewModel = tripCreationViewModel
                    )
                }

                is RootComponent.Child.CalendarView -> {
                    val calendarViewModel: CalendarViewModel = viewModel(
                        key = "CalendarView-${instance.component.tripId}"
                    ) {
                        CalendarViewModel(instance.component.tripId, tripRepository)
                    }
                    CalendarView(
                        component = instance.component,
                        viewModel = calendarViewModel
                    )
                }
                // =============================================================================================
                // === HOME SCREENS ============================================================================
                // =============================================================================================




                // =============================================================================================
                // === TRIP SCREENS ============================================================================
                // =============================================================================================
                is RootComponent.Child.TripView -> {
                    val tripViewModel: TripViewModel = viewModel(
                        key = "TripView=${instance.tripId}"
                    ) {
                        TripViewModel(
                            tripRepository = tripRepository,
                            tripId = instance.tripId
                        )
                    }
                    TripView(
                        instance.component,
                        viewModel = tripViewModel,
                    )
                }

                is RootComponent.Child.AddEvent -> {
                    val addEventViewModel: AddEventViewModel = viewModel(
                        key = "AddEvent-${instance.tripId}-${instance.eventId ?: "new"}"
                    ) {
                        AddEventViewModel(
                            tripId = instance.tripId,
                            tripRepository = tripRepository,
                            locationRepository = locationRepository,
                            eventId = instance.eventId,
                            initialDate = instance.initialDate
                        )
                    }
                    AddEvent(
                        component = instance.component,
                        viewModel = addEventViewModel
                    )
                }

                is RootComponent.Child.AddMember -> {
                    val addMemberViewModel: AddMemberViewModel = viewModel(
                        key="AddMember-${instance.tripId}"
                    ) {
                        AddMemberViewModel(
                            tripId = instance.tripId,
                            tripRepository = tripRepository
                        )
                    }
                    AddMember(
                        component = instance.component,
                        viewModel = addMemberViewModel
                    )
                }
                is RootComponent.Child.EditTrip -> {
                    val editTripViewModel: EditTripViewModel = viewModel(
                        key = "EditTrip-${instance.tripId}"
                    ) {
                        EditTripViewModel(
                            tripId = instance.tripId,
                            tripRepository = tripRepository
                        )
                    }
                    EditTrip(
                        component = instance.component,
                        viewModel = editTripViewModel
                    )
                }
                
                is RootComponent.Child.NavigationView -> {
                    NavigationView(
                        component = instance.component,
                        startLocation = instance.startLocation,
                        endLocation = instance.endLocation
                    )
                }
                
                is RootComponent.Child.MapView -> {
                    val mapViewModel: MapViewModel = viewModel(
                        key = "MapView-${instance.tripId}"
                    ) {
                        MapViewModel(
                            tripId = instance.tripId,
                            tripRepository = tripRepository
                        )
                    }
                    MapView(
                        component = instance.component,
                        viewModel = mapViewModel
                    )
                }
                // =============================================================================================
                // === TRIP SCREENS ============================================================================
                // =============================================================================================



            }
        }
    }
}

@Composable
@Preview
fun App() {
    val root = remember { RootComponent(DefaultComponentContext(LifecycleRegistry())) }
    
    // DEV USE Temporary: ================================================
    // start the app on HomeView for development.
    LaunchedEffect(root) {
       root.navigateToHome()
    }
    //====================================================================
    
    App(root)

    //ApiTestView()
}
