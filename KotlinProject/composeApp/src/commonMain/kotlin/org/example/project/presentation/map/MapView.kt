package org.example.project.presentation.map

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.example.project.presentation.uishared.MapWindow
import org.example.project.presentation.uishared.MapMarker
import org.example.project.presentation.uishared.NavBar

@Composable
fun MapView(
    component: MapViewComponent,
    viewModel: MapViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Refresh trip data when MapView is shown
    LaunchedEffect(component.tripId) {
        viewModel.refreshTrip()
    }
    
    // height used to inset the content so it's not hidden behind the nav bar
    val navBarHeight = 64.dp
    
    Box(modifier = modifier.fillMaxSize()) {
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = navBarHeight)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = uiState.error ?: "Unknown error",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
                
                else -> {
                    // Render the map with bottom padding for NavBar
                    Box(modifier = Modifier.fillMaxSize()) {
                        MapWindow(
                            latitude = uiState.centerLatitude,
                            longitude = uiState.centerLongitude,
                            zoom = uiState.zoom,
                            markers = uiState.markers,
                            modifier = Modifier.fillMaxSize()
                                .padding(bottom = navBarHeight) // Add padding to prevent overlap with NavBar
                        )
                        
                        // Optional: Show marker count
                        Card(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "${uiState.markers.size} locations",
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
        
        // NavBar floating on top of content at bottom center (not scrollable)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            key(uiState.trip?.title ?: "") {
                NavBar(
                    tripTitle = uiState.trip?.title ?: "Map",
                    selectedIndex = 2,  // Map tab
                    onItemSelected = { index: Int ->
                        when (index) {
                            0 -> component.onEvent(MapViewEvent.NavigateToTrip)
                            1 -> component.onEvent(MapViewEvent.NavigateToCalendar)
                            2 -> { /* Already on Map view, do nothing */ }
                        }
                    },
                    onBack = { component.onBack() }
                )
            }
        }
    }
}
