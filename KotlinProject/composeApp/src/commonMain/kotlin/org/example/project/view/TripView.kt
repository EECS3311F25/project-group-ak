package org.example.project.view
// TODO: Split into multiple files, shorter files are easier to work with.

/**
 * Trip screen UI components rendered in commonMain.
 *
 * Contains the TripView entry and section composables (header, members,
 * summary, and events). 
 */


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import kotlinx.datetime.LocalDate
import org.example.project.model.Trip
import org.example.project.model.Event
import org.example.project.model.User
import org.example.project.utils.rangeUntil
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.graphics.vector.rememberVectorPainter


@Composable
/**
 * Renders the Trip screen.
 *
 * Uses a `LazyColumn` to vertically stack sections and provide scrolling.
 *
 * @param modifier Optional modifier applied to the root container.
 * @param trip Data model providing content for the screen.
 */
fun TripView(modifier: Modifier = Modifier, trip: Trip) {
    Box(
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Header(trip.title, trip.startDate, trip.endDate) }
            item { ListMembersSection(trip.users) }
            item { TripSummarySection(trip.description) }
            item { EventsSection(trip.startDate, trip.endDate, trip.events) }
        }
    }
}

@Composable
/**
 * Header hero with background image, title, and date range.
 *
 * A vertical gradient overlay improves text contrast over the background.
 * Uses a 16:9 aspect ratio.
 *
 * @param tripTitle The title shown prominently.
 * @param startDate Trip start date.
 * @param endDate Trip end date.
 */
// TODO: This pattern is used multiple times, abstract it
fun Header(tripTitle: String, startDate: LocalDate, endDate: LocalDate) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f),
        contentAlignment = Alignment.BottomStart,
    ){
        Image(
            // TODO: This should be adjustable by the user.
            // TODO: This should have a default image instead of a plain color.
            painter = ColorPainter(Color(0xFF3F51B5)),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x99000000)
                        )
                    )
                )
        )
        Column (Modifier.padding(16.dp)) {
            Text(
                text = tripTitle,
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.CalendarToday,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.height(0.dp).then(Modifier.padding(start = 8.dp)))
                Text(
                    text = "${startDate.dayOfMonth}/${startDate.monthNumber}/${startDate.year} - ${endDate.dayOfMonth}/${endDate.monthNumber}/${endDate.year}",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

/**
 * Section listing trip members in a horizontal row.
 *
 * @param members Users participating in the trip.
 */
@Composable
fun ListMembersSection(members: List<User>) {
    Column (Modifier.padding(start = 16.dp, top = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Group,
                contentDescription = null,
                tint = Color.Black
            )
            Spacer(modifier = Modifier.height(0.dp).then(Modifier.padding(start = 8.dp)))
            Text(
                text = "Who's Going", //TODO: Move into Resources
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(members) {user ->
                MemberCard(user)
            }
        }
    }
}

/**
 * Small avatar + name for an individual member.
 * Shows a default vector avatar when no profile picture is available.
 *
 * @param user The member to render.
 */
@Composable
fun MemberCard(user: User) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
        ) {
            if (user.pfpUrl == null) {
                // Default vector avatar when there's no profile picture
                Image(
                    painter = rememberVectorPainter(image = Icons.Filled.Person),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            } else {
                continue // TODO: Add pfp fetch and display
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = user.name, color = Color.Black, style = MaterialTheme.typography.bodySmall)
    }
}

/**
 * Summary card describing the trip.
 *
 * @param tripSummary Descriptive text shown inside the card.
 */
@Composable
fun TripSummarySection(tripSummary: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFF212121))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.height(0.dp).then(Modifier.padding(start = 8.dp)))
            Text(
                text = "Trip Summary",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = tripSummary,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

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
fun EventsSection(startDate: LocalDate, endDate: LocalDate, events: List<Event>) {
    val dates = startDate.rangeUntil(endDate)
    val eventsByDate = events.groupBy { it.date }
    Column {
        dates.forEachIndexed { index, date ->
            val list = eventsByDate[date].orEmpty()
            EventsGroup(date, index, list)
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
fun EventsGroup(date: LocalDate, index: Int, eventsForDate: List<Event>) {
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
                EventCard(event, modifier = Modifier.width(280.dp))
            }
        }
    }
}

@Composable
/**
 * Compact card representation of a single event.
 *
 * Displays a background image placeholder, gradient overlay, and title.
 *
 * @param event Event data rendered by the card.
 * @param modifier Optional modifier for sizing within a row.
 */
fun EventCard(event: Event, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(280.dp)
            .aspectRatio(16f / 9f)   // fixed width + aspect ratio gives a stable size in a LazyRow
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.BottomStart
    ) {
        Image(
            painter = ColorPainter(Color(0xFF3F51B5)),
            contentDescription = event.title,   // use title for accessibility
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x99000000)
                        )
                    )
                )
        )

        Column(Modifier.padding(16.dp)) {
            Text(
                text = event.title,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
