package org.example.project.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.unit.dp
import org.example.project.model.Duration
import org.example.project.model.Event
import androidx.compose.ui.layout.ContentScale
import org.example.project.utils.rangeUntil

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
fun EventsSection(duration: Duration, events: List<Event>) {
    val dates = duration.startDate.rangeUntil(duration.endDate)
    val eventsByDate = events.groupBy { it.duration.startDate }
    Column {
        dates.forEachIndexed { index, date ->
            val list = eventsByDate[date].orEmpty()
            EventsGroup(index, list)
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
fun EventsGroup(index: Int, eventsForDate: List<Event>) {
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
                EventCard(event)
                // EventCard(event)
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
 * @param modifier Optional modifier for sizing within a row.
 */
fun EventCard(event: Event) {
    ImageCard(
        modifier = Modifier.width(280.dp).aspectRatio(16f / 9f).clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp)),
        content = {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = event.title,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
    )
}

