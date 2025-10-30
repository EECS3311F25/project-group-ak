package org.example.project.view
// TODO: Split into multiple files, shorter files are easier to work with.

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.model.Trip

@Composable
/**
 * Renders the Trip screen.
 *
 * Uses a `LazyColumn` to vertically stack sections and provide scrolling.
 *
 * @param modifier Optional modifier applied to the root container.
 * @param trip Data model providing content for the screen.
 */
fun TripView(modifier: Modifier = Modifier, trip: Trip) {
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
}
