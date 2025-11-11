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
import org.example.project.viewModel.HomeViewModel
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.example.project.controller.RootComponent
import org.example.project.view.TripView.TripView
import org.example.project.view.HomeView.HomeView
import org.example.project.view.HomeView.TripCreationView
import org.example.project.view.TripViewSubPages.AddTripView
import org.example.project.view.TripViewSubPages.AddMember
import org.example.project.view.AuthView.LoginView
import org.example.project.view.AuthView.SignupView
import org.example.project.viewModel.TripCreationViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.example.project.data.repository.TripRepository
import org.example.project.data.repository.UserRepository
import org.example.project.data.source.LocalTripDataSource
import org.example.project.data.source.LocalUserDataSource

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
    val tripRepository = remember { TripRepository(LocalTripDataSource()) }
    val userRepository = remember { UserRepository(LocalUserDataSource()) }
    
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
                // === AUTHENTICATION SCREENS ===
                // LoginView: Entry point, shows login form
                is RootComponent.Child.LoginView -> LoginView(instance.component)
                // SignupView: Registration form, accessible from LoginView
                is RootComponent.Child.SignupView -> SignupView(instance.component)
                
                is RootComponent.Child.TripView -> {
                    // Use the specific trip passed from navigation
                    TripView(instance.component, instance.trip)
                }
                
                is RootComponent.Child.AddTripView -> AddTripView(instance.component)
                
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
                
                is RootComponent.Child.AddMember -> {
                    AddMember(instance.component)
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
}
