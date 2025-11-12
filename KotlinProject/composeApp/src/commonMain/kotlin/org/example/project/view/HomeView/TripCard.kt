package org.example.project.view.HomeView

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.example.project.model.dataClasses.Trip

@Composable
expect fun NetworkImage(url: String, contentDescription: String?, modifier: Modifier = Modifier)

/**
 * TripCard:
 * A composable that renders a stylized card representing a Trip.
 *
 * The card displays trip-related information and an optional image. It is
 * clickable and its visual appearance can be customized via the provided
 * parameters.
 *
 * @param trip The Trip model containing the data to display (title, dates,
 * locations, etc.). This value is required and drives the content shown inside
 * the card.
 * @param modifier A [Modifier] to be applied to the card for layout, drawing,
 * or input handling. Use this to control placement, padding, or additional
 * gestures.
 * @param height The height of the card. Defaults to 180.dp. Adjust this to make
 * the card taller or shorter while keeping internal content scaling/layout.
 * @param cornerRadius The corner radius applied to the card's background shape.
 * Defaults to 12.dp. Increase for a more rounded look or decrease for a
 * sharper rectangle.
 * @param painter Optional [Painter] used to draw an image or background for the
 * trip. If null, the implementation should show a default placeholder (color,
 * pattern, or icon) appropriate for empty images.
 * @param onClick Lambda invoked when the user clicks/taps the card. Defaults to
 * a no-op; provide this to perform navigation or other actions when the card
 * is activated.
 *
 * Accessibility:
 * Provide meaningful content descriptions for images or interactive elements
 * inside the card so that assistive technologies can convey the trip details to
 * users with disabilities.
 *
 * Example:
 * TripCard(trip = myTrip, modifier = Modifier.padding(8.dp), onClick = { openTrip(myTrip) })
 */
@Composable
fun TripCard(
    trip: Trip,
    modifier: Modifier = Modifier,
    height: Dp = 180.dp,
    cornerRadius: Dp = 12.dp,
    painter: Painter? = null,
    onClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}  // Add delete callback
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Box(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .clip(RoundedCornerShape(cornerRadius))
            .background(Color.Gray) // fallback color while image loads
            .clickable { onClick() }
    ) {
        if (trip.imageHeaderUrl == null) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0x80000000)),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.Landscape,
                    contentDescription = "Landscape",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(36.dp)
                )
            }
        } else {
            // NetworkImage actual fun only implemented for jvm target
            NetworkImage(
                url = trip.imageHeaderUrl ?: "",
                contentDescription = trip.title,
                modifier = Modifier.size(200.dp)
            )

            // subtle bottom gradient so the text is readable
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0x80000000)),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
        }

        // Trip info overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Text(
                text = trip.title,
                color = Color.White,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            if (trip.location.isNotEmpty()) {
                Text(
                    text = trip.location,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier
                )
            }
        }

        // Add MoreVert menu at top right
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
            
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Delete Trip") },
                    onClick = {
                        onDeleteClick()
                        showMenu = false
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                )
            }
        }
    }
}