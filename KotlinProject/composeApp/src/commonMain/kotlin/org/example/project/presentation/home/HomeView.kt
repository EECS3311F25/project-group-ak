package org.example.project.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.presentation.home.HomeViewComponent
import org.example.project.presentation.home.HomeViewEvent
import org.example.project.model.dataClasses.Trip
import org.example.project.model.SECONDARY
import org.example.project.model.BACKGROUND
import org.example.project.model.PRIMARY
import org.example.project.presentation.home.HomeViewModel

import org.example.project.presentation.home.uicomponents.TripCard

@Composable
fun HomeView(
    component: HomeViewComponent,
    viewModel: HomeViewModel
) {
    // Collect UI state from ViewModel
    val uiState by viewModel.uiState.collectAsState()
    val trips = uiState.trips
    val isLoading = uiState.isLoading
    val error = uiState.errorMessage
    val currentUser = uiState.currentUser
    
    var tripForOptions by remember { mutableStateOf<Trip?>(null) }

    Scaffold(

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Profile/Header Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(PRIMARY)
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0x99000000)
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .background(SECONDARY)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profile icon",
                            tint = BACKGROUND,
                            modifier = Modifier
                                .size(64.dp)
                                .align(Alignment.Center)
                        )
                    }
                    Text(
                        text = currentUser?.name ?: "Loading...",
                        modifier = Modifier
                            .padding(top = 8.dp),
                        color = SECONDARY
                    )
                }
            }
            
            // Trips Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BACKGROUND)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Header with title and inline button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp, top = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Trips",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        
                        OutlinedButton(
                            onClick = { 
                                component.onEvent(HomeViewEvent.ClickAddTripHomeView) 
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Trip")
                        }
                    }
                    
                    // 🔥 Show loading state
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    
                    // 🔥 Show error state
                    error?.let { errorMessage ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = "Error: $errorMessage",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    
                    // 🔥 Reactive trips list - automatically updates!
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(
                            items = trips, // Automatically updated via StateFlow
                            key = { it.id }
                        ) { trip ->
                            TripCard(
                                trip = trip,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                onClick = { 
                                    component.onEvent(HomeViewEvent.ClickButtonHomeView(trip)) 
                                },
                                onDeleteClick = {
                                    // Show options dialog
                                    tripForOptions = trip
                                }
                            )
                        }
                    }
                    
                    // Show empty state
                    if (!isLoading && trips.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No trips yet. Create your first trip!",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Trip Options Dialog
    tripForOptions?.let { trip ->
        AlertDialog(
            onDismissRequest = { tripForOptions = null },
            title = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Are you sure?",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            },
            text = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Your trip data will be lost.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { tripForOptions = null }
                ) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTrip(trip.id)
                        tripForOptions = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete Trip")
                }
            }
        )
    }
}
