package org.example.project.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
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

@Composable
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

@Composable
fun ListMembersSection(members: List<User>) {
    Column (Modifier.padding(start = 16.dp, top = 16.dp)) {
        Text(
            text = "Who's Going", //TODO: Move into Resources
            color = Color.Black,
            style = MaterialTheme.typography.titleMedium
        )
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
                // Use colored background when there's no profile picture
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF3F51B5))
                )
            } else {
                // For now this uses a placeholder painter; swap for your image loader (Coil/Glide/etc).
                Image(
                    painter = ColorPainter(Color.LightGray),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = user.name, color = Color.Black, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun TripSummarySection(tripSummary: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFF212121))
            .padding(16.dp)
    ) {
        Text(
            text = "Trip Summary",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = tripSummary,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

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
