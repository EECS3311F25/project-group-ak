package org.example.project.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import androidx.compose.foundation.layout.padding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerSection(
    startDate: LocalDate?,
    endDate: LocalDate?,
    onStartDateSelected: (LocalDate) -> Unit,
    onEndDateSelected: (LocalDate) -> Unit,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null
) {
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Start Date
        OutlinedTextField(
            value = startDate?.toString() ?: "",
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
            value = endDate?.toString() ?: "",
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
            initialDate = startDate ?: endDate ?: minDate,
            minDate = minDate,
            maxDate = maxDate,
            onDateSelected = { date ->
                onStartDateSelected(date)
                showStartDatePicker = false
            },
            onDismiss = { showStartDatePicker = false }
        )
    }

    // End Date Picker Dialog
    if (showEndDatePicker) {
        DatePickerDialog(
            initialDate = endDate ?: startDate ?: minDate,
            minDate = minDate,
            maxDate = maxDate,
            onDateSelected = { date ->
                onEndDateSelected(date)
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
    initialDate: LocalDate?,
    minDate: LocalDate?,
    maxDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val initialSelectedMillis = remember(initialDate, minDate) {
        (initialDate ?: minDate)?.atStartOfDayIn(TimeZone.UTC)?.toEpochMilliseconds()
    }
    val selectableDates = remember(minDate, maxDate) {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = Instant.fromEpochMilliseconds(utcTimeMillis).toLocalDateTime(TimeZone.UTC).date
                val afterMin = minDate?.let { date >= it } ?: true
                val beforeMax = maxDate?.let { date <= it } ?: true
                return afterMin && beforeMax
            }

            override fun isSelectableYear(year: Int): Boolean {
                val afterMin = minDate?.let { year >= it.year } ?: true
                val beforeMax = maxDate?.let { year <= it.year } ?: true
                return afterMin && beforeMax
            }
        }
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedMillis,
        selectableDates = selectableDates
    )

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
        },
        modifier = modifier
    ) {
        DatePicker(state = datePickerState)
    }
}
