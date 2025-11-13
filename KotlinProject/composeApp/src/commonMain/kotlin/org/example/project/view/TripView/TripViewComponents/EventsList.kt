package org.example.project.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
    val eventsByDate = events.groupBy { it.duration.startDate }
    Column {
        dates.forEachIndexed { index, date ->
            val list = eventsByDate[date].orEmpty()
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
fun EventsGroup(
    index: Int,
    eventsForDate: List<Event>,
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
            items(eventsForDate) { event ->
                EventCard(
                    event = event,
                    onEditClick = { onEditEvent(event) },
                    onDeleteClick = { onDeleteEvent(event) }
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
