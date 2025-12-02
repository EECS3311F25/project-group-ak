package org.example.project.presentation.calendar.navigation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * JVM implementation using Compose UI
 */
@Composable
actual fun RenderBottomSheetOverlay(
    startTitle: String,
    startAddress: String,
    endTitle: String,
    endAddress: String,
    distance: Double,
    drivingDuration: Double,
    walkingDuration: Double,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    onCleanup: () -> Unit
) {
    val sheetHeight by animateDpAsState(
        targetValue = if (isExpanded) 400.dp else 120.dp
    )
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(sheetHeight)
            .shadow(8.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Drag handle / expand button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExpandToggle() },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = Color.Gray
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Compact view (always visible)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Route Info",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                Text(
                    text = "${(distance * 10).toInt() / 10.0} km • 🚗 ${drivingDuration.toInt()} min • 🚶 ${walkingDuration.toInt()} min",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
        
        // Expanded content
        if (isExpanded) {
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            
            // Start location
            RouteInfoItem(
                label = "Start",
                title = startTitle,
                subtitle = startAddress
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Destination
            RouteInfoItem(
                label = "Destination",
                title = endTitle,
                subtitle = endAddress
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            
            // Route details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(distance * 10).toInt() / 10.0} km",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Distance",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🚗 ${formatMinutes(drivingDuration)}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Driving",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                walkingDuration?.let {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🚶 ${formatMinutes(it)}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Walking",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RouteInfoItem(
    label: String,
    title: String,
    subtitle: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

private fun formatMinutes(minutes: Double): String {
    val totalMinutes = minutes.roundToInt()
    val hours = totalMinutes / 60
    val mins = totalMinutes % 60
    return if (hours > 0) {
        "${hours} hr ${mins.toString().padStart(2, '0')} min"
    } else {
        "${mins} min"
    }
}
