package org.example.project.view.HomeView

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.example.project.model.Trip

@Composable
expect fun NetworkImage(url: String, contentDescription: String?, modifier: Modifier = Modifier)

@Composable
fun TripCard(
    trip: Trip,
    modifier: Modifier = Modifier,
    height: Dp = 180.dp,
    cornerRadius: Dp = 12.dp,
    painter: Painter? = null,
    onClick: () -> Unit = {}
) {
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
    }
}