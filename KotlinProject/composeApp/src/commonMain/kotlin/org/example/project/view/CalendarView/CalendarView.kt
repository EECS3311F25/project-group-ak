package org.example.project.view.CalendarView

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.view.components.NavBar
import org.example.project.controller.CalendarViewComponent
import org.example.project.controller.CalendarViewEvent
import org.example.project.viewModel.CalendarViewModel
import org.example.project.model.Event
import org.example.project.model.Trip

@Composable
fun CalendarView(
    component: CalendarViewComponent,
    viewModel: CalendarViewModel,
    trip: Trip,
    modifier: Modifier = Modifier
) {
    // Collect states from ViewModel
    val events by viewModel.events.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // height used to inset the list so content is not hidden behind the nav bar
    val navBarHeight = 64.dp

    Box(modifier = modifier.fillMaxSize()) {
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = navBarHeight)
    ) {
        // Calendar will be implemented here
        Text(
            text = "Calendar View",
            style = MaterialTheme.typography.headlineMedium
        )

        // Loading state
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(16.dp)
            )
        }

        // Error state
        error?.let { errorMessage ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Events list will be implemented here
        events.forEach { event ->
            EventCard(event = event)
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
                    selectedIndex = 1,  // Calendar tab
                    onItemSelected = { index ->
                        when (index) {
                            0 -> component.onEvent(CalendarViewEvent.NavigateToTrip)
                            // 2 -> Map view (to be implemented later)
                        }
                    },
                    onBack = { component.onBack() }
                )
            }
        }
    }
}