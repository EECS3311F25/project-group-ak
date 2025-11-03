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
import org.example.project.view.AddTripView.AddTripView
import org.example.project.view.AuthView.LoginView
import org.example.project.view.AuthView.SignupView
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlinx.datetime.LocalDate
import org.example.project.model.Event
import org.example.project.model.Trip
import org.example.project.model.User
import org.example.project.model.Duration
import androidx.compose.runtime.LaunchedEffect //for DEV

@Composable
fun App(root: RootComponent) {
    MaterialTheme {
        val childStack by root.childStack.subscribeAsState()
        Children(
            stack = childStack,
            animation = stackAnimation(slide())
        ) { child ->
            when (val instance = child.instance) {
                is RootComponent.Child.LoginView -> LoginView(instance.component)
                is RootComponent.Child.SignupView -> SignupView(instance.component)
                is RootComponent.Child.TripView -> TripView(instance.component, instance.trip)
                is RootComponent.Child.AddTripView -> AddTripView(instance.component)
                is RootComponent.Child.HomeView -> HomeView(instance.component)
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
