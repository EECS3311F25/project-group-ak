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
// import androidx.compose.foundation.layout.weight
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

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
            color = androidx.compose.ui.graphics.Color.Gray,
            elevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Box with back icon and small label
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(100.dp)
                        .fillMaxHeight()
                        .background(color = MaterialTheme.colors.surface)
                ) {
        
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier
                                .size(18.dp)
                                .clickable(onClick = onBack)
                        )
                        Text(
                            text = "Home",
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.body2
                        )
                    }
                }

                //Spacer(modifier = Modifier.weight(1f))

                // trailing text inside same surface
                Text(
                    text = tripTitle,
                    style = MaterialTheme.typography.body2,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    modifier = Modifier.padding(end = 8.dp, start = 16.dp)
                )
            }
        }

        BottomNavigation(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            backgroundColor = MaterialTheme.colors.surface,
            elevation = 8.dp
        ) {
            items.forEachIndexed { index, item ->
                BottomNavigationItem(
                    icon = { Icon(item.icon, contentDescription = item.title) },
                    selected = index == current,
                    onClick = {
                        if (selectedIndex == null) internalSelected = index
                        onItemSelected(index)
                    }
                )
            }
        }
    }
}