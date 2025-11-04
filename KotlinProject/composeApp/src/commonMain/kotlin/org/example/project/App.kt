package org.example.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.example.project.controller.RootComponent
import org.example.project.view.TripView.TripView
import org.example.project.view.HomeView.HomeView
import org.example.project.view.TripViewSubPages.AddTripView
import org.example.project.view.TripViewSubPages.AddMember
import org.example.project.view.AuthView.LoginView
import org.example.project.view.AuthView.SignupView
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlinx.datetime.LocalDate
import org.example.project.model.Event
import org.example.project.model.Trip
import org.example.project.model.User
import org.example.project.model.Duration
import androidx.compose.runtime.LaunchedEffect //for DEV


// TODO: Fetch from API
val trip = Trip(
    title = "Summer Getaway",
    description = "Road trip across Ontario",
    location = "Toronto to Ottawa",
    duration = Duration(
        startDate = LocalDate(2025, 7, 1),
        startTime = kotlinx.datetime.LocalTime(9, 0),
        endDate = LocalDate(2025, 7, 10),
        endTime = kotlinx.datetime.LocalTime(17, 0)
    ),
    users = listOf(
        User(name = "Klodiana"),
        User(name = "Alex"),
        User(name = "Sam"),
        User(name = "Priya"),
        User(name = "Diego"),
        User(name = "Mei"),
        User(name = "Fatima"),
        User(name = "John"),
        User(name = "Maria"),
        User(name = "Chen"),
        User(name = "Liam"),
        User(name = "Zoe")
    ),
    events = listOf(
        // TODO: add duration
        Event(title = "Niagara Falls Stop", Duration(
            startDate = LocalDate(2025, 7, 1),
            startTime = kotlinx.datetime.LocalTime(9, 0),
            endDate = LocalDate(2025, 7, 1),
            endTime = kotlinx.datetime.LocalTime(17, 0)
        )),
        Event(title = "Niagara Boat Tour", Duration(
            startDate = LocalDate(2025, 7, 1),
            startTime = kotlinx.datetime.LocalTime(9, 0),
            endDate = LocalDate(2025, 7, 1),
            endTime = kotlinx.datetime.LocalTime(17, 0)
        )),
        Event(title = "Table Rock Lunch", Duration(
            startDate = LocalDate(2025, 7, 1),
            startTime = kotlinx.datetime.LocalTime(9, 0),
            endDate = LocalDate(2025, 7, 1),
            endTime = kotlinx.datetime.LocalTime(17, 0)
        )),
        Event(title = "Ottawa Parliament Tour", Duration(
            startDate = LocalDate(2025, 7, 1),
            startTime = kotlinx.datetime.LocalTime(9, 0),
            endDate = LocalDate(2025, 7, 1),
            endTime = kotlinx.datetime.LocalTime(17, 0)
        ))
    )
)


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
    MaterialTheme {
        // Subscribe to navigation state changes
        val childStack by root.childStack.subscribeAsState()
        
        // Children handles screen transitions with slide animation
        Children(
            stack = childStack,
            animation = stackAnimation(slide())
        ) { child ->
            // Render the appropriate view based on current navigation state
            when (val instance = child.instance) {
                // === AUTHENTICATION SCREENS ===
                // LoginView: Entry point, shows login form
                is RootComponent.Child.LoginView -> LoginView(instance.component)
                // SignupView: Registration form, accessible from LoginView
                is RootComponent.Child.SignupView -> SignupView(instance.component)
                is RootComponent.Child.TripView -> TripView(instance.component, trip)
                is RootComponent.Child.AddTripView -> AddTripView(instance.component)
                is RootComponent.Child.HomeView -> HomeView(instance.component)
                is RootComponent.Child.AddMember -> AddMember(instance.component)
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
    // LaunchedEffect(root) {
    //     root.navigateToHome()
    // }
    //====================================================================
  
 
    App(root)
}
