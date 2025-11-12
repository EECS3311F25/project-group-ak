package org.example.project.view.TripView

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.controller.TripController.TripViewComponent
import org.example.project.controller.TripController.TripViewEvent
import org.example.project.model.PRIMARY
import org.example.project.model.SECONDARY
import org.example.project.view.EventsSection
import org.example.project.view.Header
import org.example.project.view.ListMembersSection
import org.example.project.view.TripSummarySection
import org.example.project.view.components.NavBar
import org.example.project.viewmodel.trip.TripViewModel

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
    viewModel: TripViewModel,
) {

    val trip by viewModel.trip.collectAsState()
    // height used to inset the list so content is not hidden behind the nav bar
    val navBarHeight = 64.dp

    if (trip == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val tripData = trip!!

        Box(modifier = Modifier.fillMaxSize()) {
            // main scrollable content with bottom padding so it doesn't scroll under the nav bar
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = navBarHeight),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Header(
                        tripTitle = tripData.title,
                        duration = tripData.duration,
                        onShareClick = { component.onEvent(TripViewEvent.ClickShare) }
                    )
                }
                item { ListMembersSection(tripData.users) }
                item { TripSummarySection(tripData.description) }
                item { EventsSection(tripData.duration, tripData.events) }
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
                modifier = Modifier.align(Alignment.BottomEnd)
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
                key(tripData.id) {
                    NavBar(
                        tripTitle = tripData.title,
                        selectedIndex = 0,
                        onItemSelected = { /* ... */ },
                        onBack = { component.onBack() }
                    )
                }
            }
        }
    }
}
