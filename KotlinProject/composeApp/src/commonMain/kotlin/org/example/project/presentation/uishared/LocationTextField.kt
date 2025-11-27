package org.example.project.presentation.uishared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp

/**
 * Reusable location search text field with suggestions dropdown.
 *
 * @param label Label shown in the TextField.
 * @param value Current text value.
 * @param onValueChange Called when the user edits the text.
 * @param suggestions List of suggestion strings to show below the field.
 * @param onSuggestionClick Called when the user selects a suggestion.
 * @param onSearchClick Optional callback when the search icon is pressed.
 */
@Composable
fun LocationTextField(
    label: String = "Search location",
    value: String,
    onValueChange: (String) -> Unit,
    suggestions: List<String> = emptyList(),
    onSuggestionClick: (String) -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    var hasFocus by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { state -> hasFocus = state.isFocused },
            value = value,
            onValueChange = { onValueChange(it) },
            label = { Text(label) },
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null
                )
            },
            trailingIcon = {
                IconButton(onClick = { onSearchClick() }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }
            },
        )

        if (hasFocus && suggestions.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                items(suggestions) { suggestion ->
                    Text(
                        text = suggestion,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSuggestionClick(suggestion)
                                onValueChange(suggestion)
                                hasFocus = false
                            }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

/**
 * Simple internal stateful wrapper if you just want a drop-in widget.
 */
@Composable
fun LocationTextField(
    label: String = "Search location",
    suggestions: List<String> = emptyList(),
    onSuggestionClick: (String) -> Unit = {},
    onSearchClick: (String) -> Unit = {}
) {
    var text by remember { mutableStateOf("") }

    LocationTextField(
        label = label,
        value = text,
        onValueChange = { text = it },
        suggestions = suggestions,
        onSuggestionClick = {
            text = it
            onSuggestionClick(it)
        },
        // Pass the current text to whoever triggers the API call
        onSearchClick = { onSearchClick(text) }
    )
}