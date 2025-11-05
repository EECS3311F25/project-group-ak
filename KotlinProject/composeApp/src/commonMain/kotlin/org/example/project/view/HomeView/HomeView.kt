package org.example.project.view.HomeView

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.controller.HomeViewComponent
import org.example.project.controller.HomeViewEvent
import org.example.project.model.Trip
import org.example.project.model.User
import org.example.project.model.SECONDARY
import org.example.project.model.BACKGROUND
import org.example.project.model.PRIMARY
import org.example.project.data.repository.TripRepository
import org.example.project.data.repository.UserRepository

@Composable
fun HomeView(
    component: HomeViewComponent,
    tripRepository: TripRepository,    // Accept shared repository
    userRepository: UserRepository     // Accept shared repository
) {
    // 🔥 Reactive state - automatically updates when repository data changes
    val trips by tripRepository.trips.collectAsState()
    val isLoading by tripRepository.isLoading.collectAsState()
    val error by tripRepository.error.collectAsState()
    
    var currentUser by remember { mutableStateOf<User?>(null) }
    
    // Load current user once
    LaunchedEffect(userRepository) {
        currentUser = userRepository.getCurrentUser()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    component.onEvent(HomeViewEvent.ClickAddTripHomeView) 
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Trip"
                )
            }
        }
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
                    .background(PRIMARY)
            ) {
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
                            key = { it.title }
                        ) { trip ->
                            TripCard(
                                trip = trip,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                onClick = { 
                                    component.onEvent(HomeViewEvent.ClickButtonHomeView(trip)) 
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
}