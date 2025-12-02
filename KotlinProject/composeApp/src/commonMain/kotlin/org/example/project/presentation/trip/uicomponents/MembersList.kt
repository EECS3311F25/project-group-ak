package org.example.project.presentation.trip.uicomponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import org.example.project.model.dataClasses.User

/**
 * Section listing trip members in a horizontal row.
 *
 * @param members Users participating in the trip.
 */
@Composable
fun ListMembersSection(members: List<User>) {
    Column(Modifier.padding(start = 16.dp, top = 16.dp)) {
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
            items(members) { user ->
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
                // TODO: Add pfp fetch and display
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = user.name, color = Color.Black, style = MaterialTheme.typography.bodySmall)
    }
}
