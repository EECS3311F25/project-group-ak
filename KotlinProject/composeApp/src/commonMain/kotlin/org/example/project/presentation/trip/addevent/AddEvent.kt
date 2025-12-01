@file:OptIn(ExperimentalMaterial3Api::class)

package org.example.project.presentation.trip.addevent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.LocalDate
import org.example.project.presentation.trip.addevent.AddEventComponent
import org.example.project.presentation.trip.addevent.AddEventEvent
import org.example.project.presentation.uishared.DatePickerSection
import org.example.project.presentation.trip.addevent.AddEventViewModel
import org.example.project.presentation.uishared.LocationTextField

@Composable
fun AddEvent(
    component: AddEventComponent,
    viewModel: AddEventViewModel
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(state.didCreateEvent) {
        if (state.didCreateEvent) {
            component.goBack()
            viewModel.clearCompletion()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // === TOP BAR ===
        TopAppBar(
            title = {
                Text(
                    text = if (state.isEditMode) "Edit Event" else "Add New Event",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = { component.goBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go Back"
                    )
                }
            }
        )
        OutlinedTextField(
            value = state.title,
            onValueChange = viewModel::updateTitle,
            label = { Text("Event Title *") },
            placeholder = { Text("Enter event title") },
            isError = !viewModel.isFieldValid("title") && state.title.isNotEmpty(),
            supportingText = {
                viewModel.getFieldError("title")?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = addEventTextFieldColors()
        )

        OutlinedTextField(
            value = state.description,
            onValueChange = viewModel::updateDescription,
            label = { Text("Description") },
            placeholder = { Text("What’s happening during this event?") },
            singleLine = false,
            minLines = 3,
            modifier = Modifier.fillMaxWidth(),
            colors = addEventTextFieldColors()
        )

        LocationTextField(
            label = "Location *",
            value = state.locationQuery,
            onValueChange = { viewModel.updateLocationQuery(it) },
            suggestions = state.locationSuggestions,
            onSuggestionClick = viewModel::onLocationSuggestionSelected
        )
        if (state.isSuggestionsLoading) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
                Text(
                    text = "Searching locations...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (!state.isSuggestionsLoading) {
            state.suggestionsMessage?.let { message ->
                val isError = message.startsWith("Failed", ignoreCase = true)
                Text(
                    text = message,
                    color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }
        }
        // Debug status to verify suggestion flow is triggering
        Text(
            text = "Debug: query='${state.locationQuery}' (${state.locationQuery.length} chars), loading=${state.isSuggestionsLoading}, suggestions=${state.locationSuggestions.size}, selected=${state.selectedLocation != null}",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
        )
        viewModel.getFieldError("location")?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }

        OutlinedTextField(
            value = state.imageUrl,
            onValueChange = viewModel::updateImageUrl,
            label = { Text("Header image URL") },
            placeholder = { Text("https://example.com/event.jpg") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = addEventTextFieldColors()
        )

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
                    text = "Event Duration *",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                val parsedStartDate = state.durationFields.startDate
                    .takeIf { it.isNotBlank() }
                    ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
                val parsedEndDate = state.durationFields.endDate
                    .takeIf { it.isNotBlank() }
                    ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
                val tripDuration = state.tripDuration

                DatePickerSection(
                    startDate = parsedStartDate,
                    endDate   = parsedEndDate,
                    minDate = tripDuration?.startDate,
                    maxDate = tripDuration?.endDate,
                    onStartDateSelected = { viewModel.updateStartDate(it) },
                    onEndDateSelected   = { viewModel.updateEndDate(it) }
                )

                tripDuration?.let {
                    Text(
                        text = "Trip window: ${it.startDate} - ${it.endDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                viewModel.getFieldError("duration")?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val startTimeText = state.durationFields.startTime
                val endTimeText = state.durationFields.endTime
                val startTimeError = viewModel.getFieldError("startTime")
                val endTimeError = viewModel.getFieldError("endTime")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = startTimeText,
                        onValueChange = { input ->
                            viewModel.updateStartTime(formatTimeInput(input))
                        },
                        label = { Text("Start Time *") },
                        placeholder = { Text("e.g., 09:00") },
                        isError = false,
                        supportingText = {
                            startTimeError?.let { error ->
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = endTimeText,
                        onValueChange = { input ->
                            viewModel.updateEndTime(formatTimeInput(input))
                        },
                        label = { Text("End Time *") },
                        placeholder = { Text("e.g., 17:30") },
                        isError = false,
                        supportingText = {
                            endTimeError?.let { error ->
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        state.errorMessage?.let { error ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Button(
            onClick = { viewModel.submit() },
            enabled = viewModel.canSubmit(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text(if (state.isEditMode) "Update Event" else "Save Event")
            }
        }

        OutlinedButton(
            onClick = { component.goBack() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Cancel")
        }
    }
}

@Composable
private fun addEventTextFieldColors() = TextFieldDefaults.colors(
    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
    unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    disabledContainerColor = MaterialTheme.colorScheme.surface,
    errorContainerColor = MaterialTheme.colorScheme.surface
)

private fun formatTimeInput(raw: String): String {
    val digitsOnly = raw.filter(Char::isDigit).take(4)
    if (digitsOnly.isEmpty()) return ""
    return if (digitsOnly.length <= 2) {
        digitsOnly
    } else {
        val minutes = digitsOnly.takeLast(2)
        val hours = digitsOnly.dropLast(2)
        "${hours}:${minutes}"
    }
    
}
