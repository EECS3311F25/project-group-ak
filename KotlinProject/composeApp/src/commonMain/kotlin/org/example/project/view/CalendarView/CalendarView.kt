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

    // Initialize with trip's start date if no date selected
    LaunchedEffect(Unit) {
        if (selectedDate == null) {
            println("=== CalendarView: Initializing with trip start date: ${trip.duration.startDate} ===")
            viewModel.selectDate(trip.duration.startDate)
        }
    }
    
    println("=== CalendarView State ===")
    println("Events: ${events.size}, Selected: $selectedDate, Loading: $isLoading, Error: $error")

    // height used to inset the list so content is not hidden behind the nav bar
    val navBarHeight = 64.dp

    Box(modifier = modifier.fillMaxSize()) {
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = navBarHeight)
        ) {
            // Header with date selector
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Calendar View",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    
                    selectedDate?.let { date ->
                        Text(
                            text = "${date.dayOfMonth} ${date.month} ${date.year}",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            // Loading state
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Error state
                error?.let { errorMessage ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                // Timeline view
                TimelineView(
                    events = events,
                    selectedDate = selectedDate,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
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