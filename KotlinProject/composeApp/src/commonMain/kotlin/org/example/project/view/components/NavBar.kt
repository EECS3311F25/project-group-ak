package org.example.project.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Map
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.example.project.model.PRIMARY
import org.example.project.model.SECONDARY

data class NavItem(val title: String, val icon: ImageVector)

/**
 * A simple bottom navigation bar with a slim surface above it that contains a Row
 * with a Box (back icon + label) and a Text element.
 *
 * Now takes `tripTitle` as a parameter.
 */
@Composable
fun NavBar(
    tripTitle: String,
    selectedIndex: Int? = null,
    onItemSelected: (index: Int) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val items = listOf(
        NavItem("Trip", Icons.Filled.Menu),
        NavItem("Calendar", Icons.Filled.CalendarToday),
        NavItem("Map", Icons.Filled.Map)
    )

    var internalSelected by remember { mutableStateOf(0) }
    val current = selectedIndex ?: internalSelected

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        // single slim surface containing a Row with Box (back + label) and a trailing Text
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp),
            color = MaterialTheme.colorScheme.background,
            shadowElevation = 4.dp,
            shape = RoundedCornerShape(bottomStart = 12.dp) // only left-bottom corner rounded
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Box with back icon and home text
                Box(
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.secondary)
                ){
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(100.dp)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(topEnd = 12.dp))
                            .background(color = MaterialTheme.colorScheme.surface)
                            .clickable(onClick = onBack)
                    ) {

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier
                                    .size(18.dp)
                            )
                            Text(
                                text = "Home",
                                modifier = Modifier.padding(start = 8.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }


                // Box showing trip title (clip bottom-start corner)
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 12.dp))
                        .background(color = MaterialTheme.colorScheme.secondary)
                ) {
                    Text(
                        text = tripTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondary,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 16.dp, end = 8.dp)
                    )
                }
            }
        }
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            items.forEachIndexed { index, item ->
                val selected = index == current
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (selectedIndex == null) internalSelected = index
                        onItemSelected(index)
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                )
            }
        }
    }
}