package org.example.project.presentation.trip.uicomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import org.example.project.model.dataClasses.Duration

@Composable
/**
 * Header hero with background image, title, and date range.
 *
 * A vertical gradient overlay improves text contrast over the background.
 * Uses a 16:9 aspect ratio.
 *
 * @param tripTitle The title shown prominently.
 * @param duration Trip duration data class.
 */
fun Header(
    tripTitle: String,
    duration: Duration,
    imageUrl: String? = null,
    onShareClick: () -> Unit = {},
    onEditTitleClick: () -> Unit = {}
) {
    // TODO: Fetch image from backend via url then pass it in here as painter param.
    ImageCard(
        modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f),
        imageUrl = imageUrl,
        content = {
        Row(
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SmallFloatingActionButton(
                onClick = onEditTitleClick,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit trip title"
                )
            }
            SmallFloatingActionButton(
                onClick = onShareClick,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = "Share trip"
                )
            }
        }
        Column(Modifier.padding(16.dp)) {
            Text(
                text = tripTitle,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.CalendarToday,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${duration.startDate.asDayMonthYear()} - ${duration.endDate.asDayMonthYear()}",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
        }
    )
}

private fun LocalDate.asDayMonthYear(): String =
    buildString(10) {
        append(dayOfMonth.toString().padStart(2, '0'))
        append('/')
        append(monthNumber.toString().padStart(2, '0'))
        append('/')
        append(year)
    }
