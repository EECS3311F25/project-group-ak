package org.example.project.view.TripView

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
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
            item {
                Header(
                    tripTitle = trip.title,
                    duration = trip.duration,
                    onShareClick = { component.onEvent(TripViewEvent.ClickShare) }
                )
            }
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
                contentColor = PRIMARY,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(y = (-12).dp)
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
                    onItemSelected = { index -> 
                        when (index) {
                            1 -> component.onEvent(TripViewEvent.ClickCalendar(trip)) // Calendar
                            // 2 -> Map view (to be implemented later)
                        }
                    },
                    onBack = { component.onBack() }
                )
            }
        }
    }
}
