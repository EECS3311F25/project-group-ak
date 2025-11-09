package org.example.project.view.CalendarView

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.controller.CalendarViewComponent
import org.example.project.viewModel.CalendarViewModel
import org.example.project.model.Event

@Composable
fun CalendarView(
    component: CalendarViewComponent,
    viewModel: CalendarViewModel,
    modifier: Modifier = Modifier
) {
    // Collect states from ViewModel
    val events by viewModel.events.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
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
}

@Composable
private fun EventCard(event: Event) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = event.description ?: "",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
