package org.example.project.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.example.project.utils.rangeUntil
import org.example.project.model.dataClasses.Duration
import org.example.project.model.dataClasses.Event

/**
 * Events grouped by day across the inclusive date range.
 *
 * Groups the flat events list by `Event.date` for quick lookups per day.
 *
 * @param startDate Start of the date range (inclusive).
 * @param endDate End of the date range (inclusive).
 * @param events Flat list of events to render.
 */
@Composable
// TODO: add duration
fun EventsSection(
    duration: Duration,
    events: List<Event>,
    onDeleteEvent: (Event) -> Unit = {},
    onEditEvent: (Event) -> Unit = {}
) {
    val dates = duration.startDate.rangeUntil(duration.endDate)
    val eventsByDate = mutableMapOf<LocalDate, MutableList<EventSegment>>()
    events.forEach { event ->
        // Expand multi-day events so they render under every day they cover
        val span = event.duration.startDate.rangeUntil(event.duration.endDate)
        span.forEach { date ->
            val isStartDate = date == event.duration.startDate
            val isEndDate = date == event.duration.endDate
            val dayStart = if (isStartDate) event.duration.startTime else LocalTime(0, 0)
            val dayEnd = if (isEndDate) event.duration.endTime else LocalTime(23, 59)
            eventsByDate
                .getOrPut(date) { mutableListOf() }
                .add(EventSegment(event, dayStart, dayEnd))
        }
    }
    Column {
        dates.forEachIndexed { index, date ->
            val list = eventsByDate[date]
                ?.sortedWith(compareBy<EventSegment> { it.displayStart }.thenBy { it.displayEnd })
                .orEmpty()
            EventsGroup(
                index = index,
                eventsForDate = list,
                onDeleteEvent = onDeleteEvent,
                onEditEvent = onEditEvent
            )
        }
    }
}

@Composable 
/**
 * One day's event row with a label and horizontally scrolling items.
 *
 * @param date The represented date.
 * @param index Zero‑based index used for the day label.
 * @param eventsForDate Events scheduled on this date.
 */
// TODO: Find a way to get rid of the counter/index, I don't like it
private fun EventsGroup(
    index: Int,
    eventsForDate: List<EventSegment>,
    onDeleteEvent: (Event) -> Unit = {},
    onEditEvent: (Event) -> Unit = {}
) {
    Column(Modifier.padding(start = 16.dp, top = 16.dp)) {
        Text(
            text = "Day ${index + 1}",
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier.padding(start = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(eventsForDate) { segment ->
                EventCard(
                    event = segment.event,
                    displayStart = segment.displayStart,
                    displayEnd = segment.displayEnd,
                    onEditClick = { onEditEvent(segment.event) },
                    onDeleteClick = { onDeleteEvent(segment.event) }
                )
            }
        }
    }
}

@Composable
/**
 * Compact card representation of a single event.
 *
 * Displays a background image placeholder (currently a solid colour), gradient overlay, and title.
 *
 * @param event Event data rendered by the card.
 * @param onDeleteClick Callback when the delete action is selected.
 */
fun EventCard(
    event: Event,
    displayStart: LocalTime,
    displayEnd: LocalTime,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }

    ImageCard(
        modifier = Modifier
            .width(280.dp)
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(12.dp)),
        content = {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = event.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${displayStart.asHourMinute()} - ${displayEnd.asHourMinute()}",
                        color = Color.White.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                IconButton(
                    onClick = { showMenu = true },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.5f),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options"
                    )
                }

                val menuShape = RoundedCornerShape(12.dp)
                val menuBackground = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(menuBackground, menuShape)
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit Event") },
                        onClick = {
                            onEditClick()
                            showMenu = false
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = MaterialTheme.colorScheme.onSurface,
                            leadingIconColor = MaterialTheme.colorScheme.primary
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete Event") },
                        onClick = {
                            onDeleteClick()
                            showMenu = false
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = MaterialTheme.colorScheme.onSurface,
                            leadingIconColor = MaterialTheme.colorScheme.error
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    )
                }
            }
        }
    )
}

private fun LocalTime.asHourMinute(): String =
    buildString(5) {
        append(hour.toString().padStart(2, '0'))
        append(':')
        append(minute.toString().padStart(2, '0'))
    }

private data class EventSegment(
    val event: Event,
    val displayStart: LocalTime,
    val displayEnd: LocalTime
)
