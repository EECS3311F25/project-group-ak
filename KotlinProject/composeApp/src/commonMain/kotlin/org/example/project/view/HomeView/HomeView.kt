package org.example.project.view.HomeView

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.controller.HomeViewComponent
import org.example.project.controller.HomeViewEvent
import org.example.project.model.Trip
import org.example.project.model.User
import org.example.project.model.SECONDARY
import org.example.project.model.BACKGROUND
import org.example.project.model.PRIMARY
import org.example.project.data.repository.TripRepository
import org.example.project.data.source.LocalTripDataSource

val user = User(
    name = "Aga Khan",
    pfpUrl = null
)

/**
 * Renders the Home screen UI for the application.
 *
 * This composable is the top-level presentation of the Home view. It
 * observes UI state and delegates user interactions to the provided
 * HomeViewComponent, which should expose the necessary state flows,
 * event handlers, and navigation callbacks.
 *
 * Responsibilities:
 *  - Display current UI state supplied by the component.
 *  - Forward user actions (clicks, selections, refresh, etc.) to the component.
 *
 * @param component: HomeViewComponent that provides state and handlers required by the Home screen.
 */
@Composable
fun HomeView(component: HomeViewComponent) {
    // Create repository and load trips from MockSource
    val tripRepository = remember { TripRepository(LocalTripDataSource()) }
    var trips by remember { mutableStateOf<List<Trip>>(emptyList()) }
    
    // Load trips when component is created
    LaunchedEffect(tripRepository) {
        trips = tripRepository.getAllTrips()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    component.onEvent(HomeViewEvent.ClickAddTripHomeView) 
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Trip"
                )
            }
        }
    ) { innerPadding ->
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
            
            // Trips: a column of clickable TripCards
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
                    // Header with title and inline button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp, top = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Trips",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        
                        OutlinedButton(
                            onClick = { 
                                component.onEvent(HomeViewEvent.ClickAddTripHomeView) 
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Trip")
                        }
                    }
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Use trips from MockSource (already sorted by createdDate in repository)
                        items(
                            items = trips,
                            key = { it.title }
                        ) { trip ->
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