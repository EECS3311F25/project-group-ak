package org.example.project.view.CalendarView

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalTime
import org.example.project.model.Event

@Composable
fun EventCard(
    event: Event,
    modifier: Modifier = Modifier,
    isTimelineView: Boolean = false
) {
    println("EventCard composing: ${event.title}, isTimelineView=$isTimelineView")
    
    Card(
        modifier = if (isTimelineView) {
            modifier.padding(vertical = 2.dp)
        } else {
            modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        },
        colors = if (isTimelineView) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        } else {
            CardDefaults.cardColors()
        },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = if (isTimelineView) {
                Modifier.fillMaxSize().padding(8.dp)
            } else {
                Modifier.fillMaxWidth().padding(16.dp)
            }
        ) {
            Text(
                text = event.title,
                style = if (isTimelineView) {
                    MaterialTheme.typography.titleSmall
                } else {
                    MaterialTheme.typography.titleMedium
                },
                color = if (isTimelineView) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                maxLines = if (isTimelineView) 1 else Int.MAX_VALUE
            )
            
            if (isTimelineView) {
                Text(
                    text = "${formatTime(event.duration.startTime)} - ${formatTime(event.duration.endTime)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
            
            if (event.description.isNotEmpty() && !isTimelineView) {
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            if (event.location.isNotEmpty()) {
                Text(
                    text = event.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isTimelineView) {
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    },
                    maxLines = if (isTimelineView) 1 else Int.MAX_VALUE
                )
            }
        }
    }
}

private fun formatTime(time: LocalTime): String {
    return String.format("%02d:%02d", time.hour, time.minute)
}
