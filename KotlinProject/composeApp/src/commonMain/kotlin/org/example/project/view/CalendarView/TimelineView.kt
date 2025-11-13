package org.example.project.view.CalendarView

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.example.project.model.Event

@Composable
fun TimelineView(
    events: List<Event>,
    selectedDate: LocalDate?,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val pixelsPerHour = 120.dp
    val pixelsPerMinute = pixelsPerHour / 60f
    val totalHeight = pixelsPerHour * 24

    // capture density in composition (Composable context)
    val density = LocalDensity.current

    // Auto-scroll to first event when events change
    LaunchedEffect(events.firstOrNull()?.duration?.startTime) {
        events.firstOrNull()?.let { firstEvent ->
            val startHour = firstEvent.duration.startTime.hour
            val targetHour = (startHour - 2).coerceAtLeast(0)
            // convert Dp to px for scroll offset using captured density
            val offsetPx = (with(density) { (pixelsPerHour * targetHour).toPx() }).toInt()
            // use animateScrollToItem for smoothness (or scrollToItem for instant jump)
            listState.animateScrollToItem(0, offsetPx)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize()
    ) {
        item {
            Box(modifier = Modifier
                .height(totalHeight)
                .fillMaxWidth()
            ) {
                // Draw hour labels and grid lines
                for (hour in 0..23) {
                    val hourOffset = pixelsPerHour * hour
                    // Hour label
                    androidx.compose.material3.Text(
                        text = String.format("%02d:00", hour),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .offset(y = hourOffset)
                            .padding(start = 8.dp)
                            .align(Alignment.TopStart)
                    )

                    // Hour divider
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline,
                        thickness = 1.dp,
                        modifier = Modifier
                            .offset(y = hourOffset)
                            .fillMaxWidth()
                    )

                    // Half-hour divider
                    val halfOffset = hourOffset + (pixelsPerHour / 2f)
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        thickness = 0.5.dp,
                        modifier = Modifier
                            .offset(y = halfOffset)
                            .fillMaxWidth()
                    )
                }

                // Render events as single boxes positioned relative to top of the selected day.
                // Handle multi-day events and overnight events by clamping start/end to [00:00, 24:00] of selectedDate.
                events.forEach { event ->
                    val selDate = selectedDate ?: event.duration.startDate

                    val eventStartDate = event.duration.startDate
                    val eventEndDate = event.duration.endDate

                    // minutes from midnight for start/end times
                    val rawStartMin = event.duration.startTime.hour * 60 + event.duration.startTime.minute
                    val rawEndMin = event.duration.endTime.hour * 60 + event.duration.endTime.minute

                    val dayStartMin = 0
                    val dayEndMin = 24 * 60

                    // If the event started before the selected date, begin at 00:00
                    val effectiveStartMin = if (eventStartDate < selDate) dayStartMin else rawStartMin

                    // If the event ends after the selected date, end at 24:00
                    val effectiveEndMin = if (eventEndDate > selDate) dayEndMin else rawEndMin

                    val durationMinutes = (effectiveEndMin - effectiveStartMin).coerceAtLeast(1)

                    val topOffset = pixelsPerMinute * effectiveStartMin
                    val height = pixelsPerMinute * durationMinutes

                    // place event card across the full timeline (leaving time label gutter)
                    EventCard(
                        event = event,
                        modifier = Modifier
                            .offset(y = topOffset)
                            .height(height)
                            .fillMaxWidth()
                            .padding(start = 68.dp, end = 8.dp)
                    )
                }
            }
        }
    }
}

private fun formatTime(time: LocalTime): String {
    return String.format("%02d:%02d", time.hour, time.minute)
}
