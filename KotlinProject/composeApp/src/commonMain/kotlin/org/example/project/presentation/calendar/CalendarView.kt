package org.example.project.presentation.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Spacer
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
import org.example.project.presentation.calendar.uicomponents.TimelineView
import org.example.project.presentation.uishared.NavBar
import org.example.project.presentation.calendar.CalendarViewComponent
import org.example.project.presentation.calendar.CalendarViewEvent
import org.example.project.presentation.calendar.CalendarViewModel
import org.example.project.model.dataClasses.Event
import org.example.project.model.dataClasses.Trip

@Composable
fun CalendarView(
    component: CalendarViewComponent,
    viewModel: CalendarViewModel,
    modifier: Modifier = Modifier
) {
    // Collect UI state from ViewModel
    val uiState by viewModel.uiState.collectAsState()
    val events = uiState.events
    val tripDays = uiState.currentTrip.duration?.getAllDates() ?: emptyList()
    val selectedDate = uiState.selectedDate ?: tripDays.getOrNull(0)
    val isLoading = uiState.isLoading
    val error = uiState.error
    val trip = uiState.currentTrip

    // Refresh trip data when CalendarView is shown or trip changes
    LaunchedEffect(trip.id) {
        viewModel.refreshTrip()
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
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                Column(modifier = Modifier.padding(8.dp)) {    
                    selectedDate?.let { date ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp, top = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ){ 
                            Text(
                                text = "${date.month} ${date.dayOfMonth}, ${date.year}",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .weight(1f)
                            )
                            OutlinedButton(
                                onClick = { 
                                    component.onEvent(CalendarViewEvent.ClickAddEvent(date))
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Event")
                            }
                        }
                    }
                    


                    val tripDays = trip.duration?.getAllDates() ?: emptyList()
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
                    
                    // Initialize scroll position to first event
                    LaunchedEffect(Unit) {
                        if (selectedDate != null) {
                            val initialIndex = tripDays.indexOf(selectedDate).coerceAtLeast(1)
                            listState.scrollToItem(initialIndex)
                        }
                    }
                    
                    // Day Selector Row with Top and bottom dividers
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        HorizontalDivider(color = Color.LightGray, thickness = 1.dp)

                        LazyRow(
                            state = listState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 140.dp), // Center padding
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

                        HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
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
                    component = component,
                    viewModel = viewModel,
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
                    onItemSelected = { index: Int ->
                        when (index) {
                            0 -> component.onEvent(CalendarViewEvent.NavigateToTrip)
                            2 -> component.onEvent(CalendarViewEvent.NavigateToMap)
                        }
                    },
                    onBack = { component.onBack() }
                )
            }
        }
    }

    // (Event delete confirmation is now handled in TimelineView via eventIdForDelete)
}
