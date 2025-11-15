package org.example.project.view.HomeView

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.LocalDate
import org.example.project.controller.TripCreationComponent
import org.example.project.controller.TripCreationEvent
import org.example.project.viewModel.TripCreationViewModel
import org.example.project.model.User
import org.example.project.model.Event
import org.example.project.model.Duration
import org.example.project.view.components.DatePickerSection


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripCreationView(
    component: TripCreationComponent,
    viewModel: TripCreationViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    var currentUser by remember { mutableStateOf<User?>(null) }
    val scrollState = rememberScrollState()

    // Load current user when component is created
    LaunchedEffect(component.users) {
        currentUser = component.users.getCurrentUser()
        currentUser?.let { user ->
            viewModel.addUser(user)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // === TOP BAR ===
        TopAppBar(
            title = {
                Text(
                    text = "Create New Trip",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = { 
                    component.onEvent(TripCreationEvent.ClickBack) 
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go Back"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // === TITLE FIELD ===
        OutlinedTextField(
            value = state.title,
            onValueChange = viewModel::updateTitle,
            label = { Text("Trip Title *") },
            placeholder = { Text("Enter trip title") },
            isError = !viewModel.isFieldValid("title") && state.title.isNotEmpty(),
            supportingText = {
                viewModel.getFieldError("title")?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // === LOCATION FIELD ===
        OutlinedTextField(
            value = state.location,
            onValueChange = viewModel::updateLocation,
            label = { Text("Location *") },
            placeholder = { Text("Enter destination") },
            isError = !viewModel.isFieldValid("location") && state.location.isNotEmpty(),
            supportingText = {
                viewModel.getFieldError("location")?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // === DATE SELECTION ===
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Trip Duration *",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                DatePickerSection(
                    state = state,
                    viewModel = viewModel
                )
                
                if (!viewModel.isFieldValid("duration")) {
                    Text(
                        text = "Trip duration is required",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // === DESCRIPTION FIELD ===
        OutlinedTextField(
            value = state.description,
            onValueChange = viewModel::updateDescription,
            label = { Text("Description") },
            placeholder = { Text("Describe your trip (optional)") },
            minLines = 3,
            maxLines = 5,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // === IMAGE URL FIELD ===
        OutlinedTextField(
            value = state.imageHeaderUrl ?: "",
            onValueChange = { viewModel.updateImageHeaderUrl(it) },
            label = { Text("Header Image URL") },
            placeholder = { Text("Enter image URL (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        
        // === ERROR MESSAGE ===
        state.errorMessage?.let { error ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }

        // === ACTION BUTTONS ===
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cancel Button
            OutlinedButton(
                onClick = { 
                    viewModel.resetForm()
                    component.onEvent(TripCreationEvent.ClickBack) 
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
            
            // Create Trip Button
            Button(
                onClick = { 
                    viewModel.createTrip(
                        onSuccess = { trip ->
                            component.onEvent(TripCreationEvent.ClickCreate(trip))
                        },
                        onError = { error ->
                            // Error is handled in ViewModel state
                        }
                    )
                },
                enabled = state.isFormValid && !state.isLoading,
                modifier = Modifier.weight(1f)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (state.isLoading) "Creating..." else "Create Trip")
            }
        }

        // Bottom spacing for scroll
        Spacer(modifier = Modifier.height(32.dp))
    }
}
