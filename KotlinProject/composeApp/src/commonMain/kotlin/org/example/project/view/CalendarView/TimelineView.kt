package org.example.project.view.CalendarView

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Commute
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.example.project.model.dataClasses.Event

@Composable
fun TimelineView(
    events: List<Event>,
    selectedDate: LocalDate?,
    component: org.example.project.controller.CalendarViewComponent,
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

                // Precompute effective start/end minutes for each event (clamped to the selected date)
                val selDate = selectedDate
                val eventWithMinutes = events.map { event ->
                    val eventStartDate = event.duration.startDate
                    val eventEndDate = event.duration.endDate
                    val rawStartMin = event.duration.startTime.hour * 60 + event.duration.startTime.minute
                    val rawEndMin = event.duration.endTime.hour * 60 + event.duration.endTime.minute
                    val effectiveStartMin = if (eventStartDate < (selDate ?: eventStartDate)) 0 else rawStartMin
                    val effectiveEndMin = if (eventEndDate > (selDate ?: eventStartDate)) 24 * 60 else rawEndMin
                    Triple(event, effectiveStartMin, effectiveEndMin)
                }.sortedBy { it.second }

                // Render events as single boxes positioned relative to top of the selected day.
                eventWithMinutes.forEach { triple ->
                    val event = triple.first
                    val effectiveStartMin = triple.second
                    val effectiveEndMin = triple.third
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
                            .padding(start = 68.dp, end = 8.dp),
                        onEdit = { component.onEvent(org.example.project.controller.CalendarViewEvent.ClickEditEvent(event.title)) }
                    )
                }

                // Draw vertical line and commute icons on the right side between consecutive events when there is a gap
                val minGapToShowIconMinutes = 5 // don't show for very tiny gaps
                val iconPaddingEnd = 8.dp
                val iconSize = 30.dp
                val iconBoxSize = iconSize + 8.dp
                val lineWidth = 4.dp
                val lineColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                eventWithMinutes.windowed(2).forEach { pair ->
                    val first = pair[0]
                    val second = pair[1]
                    val firstEnd = first.third
                    val secondStart = second.second
                    val gap = secondStart - firstEnd
                    if (gap > minGapToShowIconMinutes) {
                        val lineTop = pixelsPerMinute * firstEnd
                        val lineHeight = pixelsPerMinute * gap

                        // Draw vertical line (behind the icon). Drawn before the icon so icon appears on top.
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(y = lineTop)
                                .padding(end = iconPaddingEnd + (iconBoxSize / 2f))
                                .width(lineWidth)
                                .height(lineHeight)
                                .background(lineColor)
                        )

                        val midpointMin = firstEnd + gap / 2f
                        // Position the top of the icon box so the icon is vertically centered at midpoint
                        val iconBoxTopOffset = (pixelsPerMinute * midpointMin) - (iconBoxSize / 2f)

                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(y = iconBoxTopOffset)
                                .padding(end = iconPaddingEnd)
                                .size(iconBoxSize)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Commute,
                                contentDescription = "Commute",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(iconSize)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(time: LocalTime): String {
    return String.format("%02d:%02d", time.hour, time.minute)
}
