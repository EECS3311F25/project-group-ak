package org.example.project.view.CalendarView

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
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
                    selectedDate?.let { date ->
                        Text(
                            text = "${date.dayOfMonth} ${date.month} ${date.year}",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    
                    // Horizontal scrollable day selector with center snapping
                    val tripDays = trip.duration.getAllDates()
                    val listState = rememberLazyListState()
                    val coroutineScope = rememberCoroutineScope()
                    
                    // Calculate center index based on scroll position
                    val centerIndex by remember {
                        derivedStateOf {
                            listState.firstVisibleItemIndex + 
                            if (listState.firstVisibleItemScrollOffset > listState.layoutInfo.visibleItemsInfo.firstOrNull()?.size?.div(2) ?: 0) 1 else 0
                        }
                    }
                    
                    // Auto-select the centered date
                    LaunchedEffect(centerIndex) {
                        if (centerIndex in tripDays.indices) {
                            val centeredDate = tripDays[centerIndex]
                            if (centeredDate != selectedDate) {
                                viewModel.selectDate(centeredDate)
                            }
                        }
                    }
                    
                    // Initialize scroll position to first day
                    LaunchedEffect(Unit) {
                        if (selectedDate != null) {
                            val initialIndex = tripDays.indexOf(selectedDate).coerceAtLeast(0)
                            listState.scrollToItem(initialIndex)
                        }
                    }
                    
                    // Top and bottom dividers around the horizontal selector
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Divider(color = Color.LightGray, thickness = 1.dp)

                        LazyRow(
                            state = listState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 120.dp), // Center padding
                            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
                        ) {
                            items(tripDays.size) { index ->
                                val date = tripDays[index]
                                val isSelected = date == selectedDate
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        viewModel.selectDate(date)
                                        coroutineScope.launch {
                                            listState.animateScrollToItem(index)
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = "Day ${index + 1}",
                                            style = if (isSelected) {
                                                MaterialTheme.typography.labelLarge.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.Black
                                                )
                                            } else {
                                                MaterialTheme.typography.labelLarge.copy(
                                                    fontWeight = FontWeight.Thin,
                                                    color = Color.Gray
                                                )
                                            }
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = Color.Transparent,
                                        selectedContainerColor = Color.Transparent,
                                        labelColor = if (isSelected) Color.Black else Color.Gray,
                                        selectedLabelColor = Color.Black
                                    ),
                                    border = null
                                )
                            }
                        }

                        Divider(color = Color.LightGray, thickness = 1.dp)
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