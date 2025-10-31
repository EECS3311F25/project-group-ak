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
import org.jetbrains.compose.ui.tooling.preview.Preview
 



@Composable
fun App(root: RootComponent) {
    MaterialTheme {
        val childStack by root.childStack.subscribeAsState()
        Children(
            stack = childStack,
            animation = stackAnimation(slide())
        ) { child ->
            when (val instance = child.instance) {
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
    App(root)
}
