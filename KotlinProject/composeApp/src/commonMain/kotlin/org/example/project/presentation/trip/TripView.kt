package org.example.project.presentation.trip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.presentation.trip.TripViewComponent
import org.example.project.presentation.trip.TripViewEvent
import org.example.project.model.PRIMARY
import org.example.project.model.SECONDARY
import org.example.project.presentation.trip.uicomponents.EventsSection
import org.example.project.presentation.trip.uicomponents.ListMembersSection
import org.example.project.presentation.trip.uicomponents.TripSummarySection
import org.example.project.presentation.trip.uicomponents.Header
import org.example.project.presentation.uishared.NavBar
import org.example.project.presentation.trip.TripViewModel

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

    val uiState by viewModel.uiState.collectAsState()
    
    // Refresh trip data when TripView is shown or trip changes
    LaunchedEffect(uiState.trip?.id) {
        viewModel.refreshTrip()
    }
    
    // height used to inset the list so content is not hidden behind the nav bar
    val navBarHeight = 64.dp

    if (uiState.isLoading || uiState.trip == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val tripData = uiState.trip!!
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
                        imageUrl = tripData.imageHeaderUrl,
                        onShareClick = { component.onEvent(TripViewEvent.ClickShare) },
                        onEditTitleClick = { component.onEvent(TripViewEvent.ClickEditTrip) }
                    )
                }
                item { ListMembersSection(tripData.users) }
                item { TripSummarySection(tripData.description) }
                item {
                    EventsSection(
                        duration = tripData.duration,
                        events = tripData.events,
                        onDeleteEvent = { event ->
                            viewModel.deleteEvent(event.id)
                        },
                        onEditEvent = { event ->
                            component.onEvent(TripViewEvent.ClickEditEvent(event.id))
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
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
                key(tripData.title) {
                    NavBar(
                        tripTitle = tripData.title,
                        selectedIndex = 0,
                        onItemSelected = { index ->
                            when (index) {
                                1 -> component.onEvent(TripViewEvent.ClickCalendar(tripData)) // Calendar
                                2 -> component.onEvent(TripViewEvent.ClickMap) // Map
                            }
                        },
                        onBack = { component.onBack() }
                    )
                }
            }
        }
    }
}
