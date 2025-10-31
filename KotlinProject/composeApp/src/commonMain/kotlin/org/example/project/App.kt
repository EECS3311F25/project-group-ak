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
import org.example.project.view.AddTripView.AddTripView
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlinx.datetime.LocalDate
import org.example.project.model.Event
import org.example.project.model.Trip
import org.example.project.model.User
import org.example.project.model.Duration

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
fun App(root: RootComponent) {
    MaterialTheme {
        val childStack by root.childStack.subscribeAsState()
        Children(
            stack = childStack,
            animation = stackAnimation(slide())
        ) { child ->
            when (val instance = child.instance) {
                is RootComponent.Child.TripView -> TripView(instance.component, trip)
                is RootComponent.Child.AddTripView -> AddTripView(instance.component)
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
