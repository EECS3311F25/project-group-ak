package org.example.project.view.TripView

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
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


/**
 * Renders the Trip screen.
 *
 * Uses a `LazyColumn` to vertically stack sections and provide scrolling.
 *
 * @param component Decompose component for navigation/events.
 * @param trip Data model providing content for the screen.
 * @param modifier Optional modifier applied to the root container.
 */
@Composable
fun TripView(
    component: TripViewComponent,
    trip: Trip,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Header(trip.title, trip.duration) }
            item { ListMembersSection(trip.users) }
            item { TripSummarySection(trip.description) }
            item { EventsSection(trip.duration, trip.events) }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = {component.onEvent(TripViewEvent.ClickButtonTripView)},
            containerColor = SECONDARY,
            contentColor = PRIMARY
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null
            )
        }
    }
}
