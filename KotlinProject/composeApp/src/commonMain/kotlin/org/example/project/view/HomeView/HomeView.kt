package org.example.project.view.HomeView

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.example.project.controller.HomeViewComponent
import org.example.project.controller.HomeViewEvent
import org.example.project.model.Trip
import org.example.project.model.Duration
import org.example.project.model.Event
import org.example.project.model.User
import kotlinx.datetime.LocalDate
import org.example.project.model.SECONDARY
import org.example.project.model.BACKGROUND
import org.example.project.model.PRIMARY

// TODO: Fetch from API
// MOCK DATA ==========================================================
val trips = listOf(
    Trip(
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
        ),
        imageHeaderUrl = "https://images.pexels.com/photos/1285625/pexels-photo-1285625.jpeg"
    ),
    Trip(
        title = "European Adventure",
        description = "Backpacking through Europe",
        location = "Paris to Rome",
        duration = Duration(
            startDate = LocalDate(2025, 8, 15),
            startTime = kotlinx.datetime.LocalTime(10, 0),
            endDate = LocalDate(2025, 8, 30),
            endTime = kotlinx.datetime.LocalTime(18, 0)
        ),
        users = listOf(User(name = "Alice"), User(name = "Bob")),
        events = emptyList(),
        imageHeaderUrl = "https://images.pexels.com/photos/532826/pexels-photo-532826.jpeg"
    ),
    Trip(
        title = "Mountain Retreat",
        description = "Peaceful getaway in the mountains",
        location = "Banff National Park",
        duration = Duration(
            startDate = LocalDate(2025, 9, 5),
            startTime = kotlinx.datetime.LocalTime(8, 0),
            endDate = LocalDate(2025, 9, 12),
            endTime = kotlinx.datetime.LocalTime(16, 0)
        ),
        users = listOf(User(name = "Charlie"), User(name = "Diana")),
        events = emptyList(),
        imageHeaderUrl = null
    )
)

val user = User(
    name = "Aga Khan",
    pfpUrl = null
)
// ====================================================================

@Composable
fun HomeView(component: HomeViewComponent) {

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Profile/Header Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(PRIMARY)
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .background(SECONDARY)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profile icon",
                            tint = BACKGROUND,
                            modifier = Modifier
                                .size(64.dp)
                                .align(Alignment.Center)
                        )
                    }
                    Text(
                        text = user.name,
                        modifier = Modifier
                            .padding(top = 8.dp),
                        color = SECONDARY
                    )
                }
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BACKGROUND)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "My Trips",
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(bottom = 8.dp, top = 16.dp)
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(trips) { trip ->
                            TripCard(
                                trip = trip,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                onClick = { component.onEvent(HomeViewEvent.ClickButtonHomeView(trip)) }
                            )
                        }
                    }
                }
            }
        }
    }
}