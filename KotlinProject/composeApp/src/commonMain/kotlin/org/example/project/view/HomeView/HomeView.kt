package org.example.project.view.HomeView

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.controller.HomeViewComponent
import org.example.project.controller.HomeViewEvent
import org.example.project.model.Trip
import org.example.project.model.Duration
import org.example.project.model.Event
import org.example.project.model.User
import kotlinx.datetime.LocalDate

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
fun HomeView(component: HomeViewComponent) {
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = { component.onEvent(HomeViewEvent.ClickButtonHomeView(trip)) }) {
                Text("Go to Trip")
            }
        }
    }
}

