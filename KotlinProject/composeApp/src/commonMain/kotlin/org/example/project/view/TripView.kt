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
import androidx.compose.foundation.lazy.LazyRow
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

@Composable
fun TripView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
    ) {
        Column () {
            Header("Trip title", "May 12–20, 2026")
            ListMembersSection()
            TripSummarySection("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")
        }
    }
}

@Composable
fun Header(tripTitle: String, dateRange: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.3f),
        contentAlignment = Alignment.BottomStart,
    ){
        Image(
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
                text = dateRange,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
fun ListMembersSection(members: List<String> = listOf("Alice", "Bob", "Charlie", "Delia", "Eric", "Heather", "Irwin", "Jackie", "Kim")) {
    Column (Modifier.padding(start = 16.dp, top = 16.dp)) {
        Text(
            text = "Who's Going",
            color = Color.Black,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(members) { name ->
                MemberCard(name)
            }
        }
    }
}

@Composable
fun MemberCard(name: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(0xFF3F51B5))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = name, color = Color.Black, style = MaterialTheme.typography.bodySmall)
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

// @Composable
// fun EventsSection () {
//     LazyColumn () {
//         for (day in days) {
//             EventsGroup(day)
//         }
//     }

// }

// @Composable 
// fun EventsGroup () {
//     LazyRow () {
//         for (event in day) {
//             EventCard()
//         }
//     }
// }

// @Composable
// fun EventCard () {
//     Box(
//         modifier = Modifier
//             .fillMaxWidth()
//             .fillMaxHeight(0.3f),
//         contentAlignment = Alignment.BottomStart,
//     ){
//         Image(
//             painter = ColorPainter(Color(0xFF3F51B5)),
//             contentDescription = null,
//             modifier = Modifier.fillMaxSize(),
//             contentScale = ContentScale.Crop
//         )
//         Box(
//             modifier = Modifier
//                 .fillMaxSize()
//                 .background(
//                     brush = Brush.verticalGradient(
//                         colors = listOf(
//                             Color.Transparent,
//                             Color(0x99000000)
//                         )
//                     )
//                 )
//         )
//         Column (Modifier.padding(16.dp)) {
//             Text(
//                 text = eventTitle,
//                 color = Color.White,
//                 style = MaterialTheme.typography.headlineLarge.copy(
//                     fontWeight = FontWeight.Bold
//                 )
//             )
//         }
//     }
// }
