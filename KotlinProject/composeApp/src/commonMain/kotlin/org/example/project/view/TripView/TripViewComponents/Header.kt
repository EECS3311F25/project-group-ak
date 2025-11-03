package org.example.project.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.model.Duration
import org.example.project.model.PRIMARY
import org.example.project.model.SECONDARY

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
fun Header(tripTitle: String, duration: Duration, onShareClick: () -> Unit = {}) {
    // TODO: Fetch image from backend via url then pass it in here as painter param.
    ImageCard(
        modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f),
        content = {
        SmallFloatingActionButton(
            onClick = onShareClick,
            containerColor = SECONDARY,
            contentColor = PRIMARY,
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = "Share trip"
            )
        }
        Column(Modifier.padding(16.dp)) {
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
                    text = "${duration.startDate.dayOfMonth}/${duration.startDate.monthNumber}/${duration.startDate.year} - ${duration.endDate.dayOfMonth}/${duration.endDate.monthNumber}/${duration.endDate.year}",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
        }
    )
}
