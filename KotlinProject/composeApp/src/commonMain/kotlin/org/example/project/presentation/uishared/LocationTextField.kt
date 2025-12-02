package org.example.project.presentation.uishared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import org.example.project.model.dataClasses.LocationSuggestion

/**
 * Reusable location search text field with suggestions dropdown.
 *
 * @param label Label shown in the TextField.
 * @param value Current text value.
 * @param onValueChange Called when the user edits the text.
 * @param suggestions List of suggestion strings to show below the field.
 * @param onSuggestionClick Called when the user selects a suggestion.
 */
@Composable
fun LocationTextField(
    label: String = "Search location",
    value: String,
    onValueChange: (String) -> Unit,
    suggestions: List<LocationSuggestion> = emptyList(),
    onSuggestionClick: (LocationSuggestion) -> Unit = {},
) {
    var isDropdownOpen by remember { mutableStateOf(false) }
    val menuShape = RoundedCornerShape(12.dp)
    val menuBackground = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { state ->
                        isDropdownOpen = state.isFocused || (isDropdownOpen && suggestions.isNotEmpty())
                    },
                value = value,
                onValueChange = {
                    isDropdownOpen = true
                    onValueChange(it)
                },
                label = { Text(label) },
                singleLine = true
            )

            DropdownMenu(
                expanded = isDropdownOpen && suggestions.isNotEmpty(),
                onDismissRequest = { isDropdownOpen = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .background(menuBackground, menuShape)
            ) {
                suggestions.forEach { suggestion ->
                    DropdownMenuItem(
                        text = { Text(text = suggestion.title) },
                        colors = MenuDefaults.itemColors(
                            textColor = MaterialTheme.colorScheme.onSurface
                        ),
                        onClick = {
                            onSuggestionClick(suggestion)
                            isDropdownOpen = false
                        }
                    )
                }
            }
        }
    }
}
