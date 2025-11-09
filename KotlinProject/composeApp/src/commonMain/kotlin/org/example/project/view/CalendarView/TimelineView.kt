package org.example.project.view.CalendarView

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.example.project.model.Event
import kotlin.math.abs

@Composable
fun TimelineView(
    events: List<Event>,
    selectedDate: LocalDate?,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val pixelsPerHour = 120.dp // Height for each hour
    val pixelsPerMinute = pixelsPerHour / 60
    
    println("=== TimelineView Debug ===")
    println("Selected Date: $selectedDate")
    println("Events count: ${events.size}")
    events.forEachIndexed { index, event ->
        println("Event $index: ${event.title}")
        println("  Start: ${event.duration.startDate} ${event.duration.startTime}")
        println("  End: ${event.duration.endDate} ${event.duration.endTime}")
    }
    
    // Auto-scroll to first event when events change
    LaunchedEffect(events.firstOrNull()?.duration?.startTime) {
        events.firstOrNull()?.let { firstEvent ->
            val startHour = firstEvent.duration.startTime.hour
            // Scroll to 2 hours before the event (or hour 0 if event is early)
            val targetHour = (startHour - 2).coerceAtLeast(0)
            println("Auto-scrolling to hour $targetHour (event at hour $startHour)")
            listState.scrollToItem(targetHour)
        }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Single LazyColumn for both time labels and grid (background layer)
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(24) { hour ->
                Row(modifier = Modifier.height(pixelsPerHour)) {
                    // Time label
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.TopStart
                    ) {
                        Text(
                            text = String.format("%02d:00", hour),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    
                    // Grid lines
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Hour line
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outline,
                            thickness = 1.dp
                        )
                        Spacer(modifier = Modifier.height(pixelsPerHour / 2 - 0.5.dp))
                        
                        // Half-hour line
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            thickness = 0.5.dp
                        )
                        Spacer(modifier = Modifier.height(pixelsPerHour / 2 - 0.5.dp))
                    }
                }
            }
        }
        
        // Events overlay - on top of the grid (foreground layer)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 68.dp, end = 8.dp) // 60dp for time + 8dp padding
                .background(Color.Red.copy(alpha = 0.1f)) // DEBUG: visible background
        ) {
            println("Rendering ${events.size} event cards...")
            // Events are already filtered by ViewModel for the selected date
            events.forEachIndexed { index, event ->
                val startMinutes = event.duration.startTime.hour * 60 + event.duration.startTime.minute
                val endMinutes = event.duration.endTime.hour * 60 + event.duration.endTime.minute
                val durationMinutes = endMinutes - startMinutes
                
                val topOffset = pixelsPerMinute * startMinutes
                val height = pixelsPerMinute * durationMinutes
                
                println("Card $index: offset=${topOffset}, height=${height}")
                
                EventCard(
                    event = event,
                    isTimelineView = true,
                    modifier = Modifier
                        .offset(y = topOffset)
                        .height(height)
                        .fillMaxWidth()
                        .background(Color.Blue.copy(alpha = 0.2f)) // DEBUG: visible card background
                )
            }
        }
    }
}

private fun formatTime(time: LocalTime): String {
    return String.format("%02d:%02d", time.hour, time.minute)
}
