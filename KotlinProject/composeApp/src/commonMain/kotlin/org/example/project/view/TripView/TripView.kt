package org.example.project.view.TripView

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.controller.TripViewComponent
import org.example.project.controller.TripViewEvent
import org.example.project.model.Trip
import org.example.project.model.PRIMARY
import org.example.project.model.SECONDARY
import org.example.project.view.EventsSection
import org.example.project.view.Header
import org.example.project.view.ListMembersSection
import org.example.project.view.TripSummarySection
import org.example.project.view.components.NavBar

@Composable
fun TripView(
    component: TripViewComponent,
    trip: Trip,
    modifier: Modifier = Modifier,
) {
    // height used to inset the list so content is not hidden behind the nav bar
    val navBarHeight = 64.dp

    Box(modifier = modifier.fillMaxSize()) {
        // main scrollable content with bottom padding so it doesn't scroll under the nav bar
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = navBarHeight),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Header(trip.title, trip.duration) }
            item { ListMembersSection(trip.users) }
            item { TripSummarySection(trip.description) }
            item { EventsSection(trip.duration, trip.events) }
        }

        // Floating action button anchored above the nav bar
        Box(
            modifier = Modifier
            .fillMaxSize()
            .padding(end = 16.dp, bottom = navBarHeight + 16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
            onClick = { component.onEvent(TripViewEvent.ClickButtonTripView) },
            containerColor = SECONDARY,
            contentColor = PRIMARY
            ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null
            )
            }
        }

        // NavBar floating on top of content at bottom center (not scrollable)
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            key(trip.title) {
                NavBar(
                    tripTitle = trip.title,
                    selectedIndex = 0,
                    onItemSelected = { /* ... */ },
                    onBack = { /* ... */ }
                )
            }
        }
    }
}
