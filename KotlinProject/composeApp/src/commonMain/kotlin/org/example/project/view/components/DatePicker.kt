package org.example.project.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.project.viewModel.TripCreationViewModel
import org.example.project.viewModel.TripCreationState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerSection(
    state: TripCreationState,
    viewModel: TripCreationViewModel
) {
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Start Date
        OutlinedTextField(
            value = state.duration?.startDate?.toString() ?: "",
            onValueChange = { },
            label = { Text("Start Date") },
            placeholder = { Text("Select date") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showStartDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Select Start Date")
                }
            },
            modifier = Modifier.weight(1f)
        )
        
        // End Date
        OutlinedTextField(
            value = state.duration?.endDate?.toString() ?: "",
            onValueChange = { },
            label = { Text("End Date") },
            placeholder = { Text("Select date") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showEndDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Select End Date")
                }
            },
            modifier = Modifier.weight(1f)
        )
    }
    
    // Start Date Picker Dialog
    if (showStartDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
                viewModel.updateStartDate(date)
                showStartDatePicker = false
            },
            onDismiss = { showStartDatePicker = false }
        )
    }
    
    // End Date Picker Dialog
    if (showEndDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
            viewModel.updateEndDate(date)
            showEndDatePicker = false
            },
            onDismiss = { showEndDatePicker = false },
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val datePickerState = rememberDatePickerState()
    
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val instant = Instant.fromEpochMilliseconds(millis)
                        val localDate = instant.toLocalDateTime(TimeZone.UTC).date
                        onDateSelected(localDate)
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}