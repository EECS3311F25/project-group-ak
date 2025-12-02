@file:OptIn(ExperimentalMaterial3Api::class)

package org.example.project.presentation.trip.edittrip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.presentation.trip.edittrip.EditTripComponent
import org.example.project.presentation.trip.edittrip.EditTripViewModel
import org.example.project.presentation.uishared.DatePickerSection

@Composable
fun EditTrip(
    component: EditTripComponent,
    viewModel: EditTripViewModel
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(state.didUpdate) {
        if (state.didUpdate) {
            component.goBack()
            viewModel.clearCompletion()
        }
    }

    if (state.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Edit Trip",
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
            onValueChange = {
                viewModel.updateTitle(it)
                viewModel.clearError()
            },
            label = { Text("Trip title") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = editTripTextFieldColors()
        )

        OutlinedTextField(
            value = state.description,
            onValueChange = viewModel::updateDescription,
            label = { Text("Trip description") },
            placeholder = { Text("Share the vibe or plan for this trip") },
            singleLine = false,
            minLines = 3,
            modifier = Modifier.fillMaxWidth(),
            colors = editTripTextFieldColors()
        )

        OutlinedTextField(
            value = state.imageUrl,
            onValueChange = viewModel::updateImageUrl,
            label = { Text("Header image URL") },
            placeholder = { Text("https://example.com/image.jpg") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = editTripTextFieldColors()
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Trip Duration *",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                DatePickerSection(
                    startDate = state.startDate,
                    endDate = state.endDate,
                    onStartDateSelected = viewModel::updateStartDate,
                    onEndDateSelected = viewModel::updateEndDate
                )

                state.dateError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = state.startTime,
                        onValueChange = { viewModel.updateStartTime(formatTimeInput(it)) },
                        label = { Text("Start Time") },
                        placeholder = { Text("HH:MM") },
                        singleLine = true,
                        isError = state.startTimeError != null,
                        supportingText = {
                            state.startTimeError?.let { error ->
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = editTripTextFieldColors()
                    )
                    OutlinedTextField(
                        value = state.endTime,
                        onValueChange = { viewModel.updateEndTime(formatTimeInput(it)) },
                        label = { Text("End Time") },
                        placeholder = { Text("HH:MM") },
                        singleLine = true,
                        isError = state.endTimeError != null,
                        supportingText = {
                            state.endTimeError?.let { error ->
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = editTripTextFieldColors()
                    )
                }
            }
        }

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
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Button(
            onClick = { viewModel.saveChanges() },
            enabled = !state.isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isSaving) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text("Save Changes")
            }
        }

        OutlinedButton(
            onClick = { component.goBack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun editTripTextFieldColors() = TextFieldDefaults.colors(
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
