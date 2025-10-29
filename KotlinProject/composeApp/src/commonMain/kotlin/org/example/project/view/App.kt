package org.example.project.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlinx.datetime.LocalDate
import org.example.project.model.Event
import org.example.project.model.Trip
import org.example.project.model.User

val trip = Trip(
    title = "Summer Getaway",
    description = "Road trip across Ontario",
    location = "Toronto to Ottawa",
    startDate = LocalDate(2025, 7, 1),
    endDate = LocalDate(2025, 7, 10),
    users = listOf(
        User(name = "Klodiana", email = "klodiana@example.com"),
        User(name = "Alex", email = "alex@example.com"),
        User(name = "Sam", email = "sam@example.com"),
        User(name = "Priya", email = "priya@example.com"),
        User(name = "Diego", email = "diego@example.com"),
        User(name = "Mei", email = "mei@example.com"),
        User(name = "Fatima", email = "fatima@example.com"),
        User(name = "John", email = "john@example.com"),
        User(name = "Maria", email = "maria@example.com"),
        User(name = "Chen", email = "chen@example.com"),
        User(name = "Liam", email = "liam@example.com"),
        User(name = "Zoe", email = "zoe@example.com")
    ),
    events = listOf(
        Event(title = "Niagara Falls Stop", date = LocalDate(2025, 7, 2)),
        Event(title = "Niagara Boat Tour", date = LocalDate(2025, 7, 2)),
        Event(title = "Table Rock Lunch", date = LocalDate(2025, 7, 2)),
        Event(title = "Ottawa Parliament Tour", date = LocalDate(2025, 7, 8))
    )
)


@Composable
@Preview 
fun App() {
    MaterialTheme {
        Scaffold { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // TODO: Add Routing
               TripView(modifier = Modifier.fillMaxSize(), trip)
            }
        }
    }
}
