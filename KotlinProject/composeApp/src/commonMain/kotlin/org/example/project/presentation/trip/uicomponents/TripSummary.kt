package org.example.project.presentation.trip.uicomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp

@Composable
fun TripSummarySection(
    tripSummary: String? = null,
    aiSummary: String? = null,
    isGenerating: Boolean = false,
    error: String? = null,
    onGenerateClick: () -> Unit = {},
    onRetryClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFF212121))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Trip Summary",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            if (!isGenerating && aiSummary == null && error != null) {
                TextButton(onClick = onRetryClick) {
                    Text("Retry", color = Color.White)
                }
            } else if (!isGenerating && aiSummary == null && tripSummary.isNullOrBlank()) {
                TextButton(onClick = onGenerateClick) {
                    Text("Generate", color = Color.White)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        when {
            isGenerating -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Generating AI summary...",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            error != null -> {
                Text(
                    text = "Error: $error",
                    color = Color(0xFFFF6B6B),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            aiSummary != null -> {
                Text(
                    text = aiSummary,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            !tripSummary.isNullOrBlank() -> {
                Text(
                    text = tripSummary,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            else -> {
                Text(
                    text = "No summary available. Click 'Generate' to create an AI summary.",
                    color = Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}