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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import org.example.project.model.dataClasses.Location
import org.example.project.model.dataClasses.LocationSuggestion

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
    value: String?,
    onValueChange: (String) -> Unit,
    suggestions: List<LocationSuggestion> = emptyList(),
    onSuggestionClick: (LocationSuggestion) -> Unit = {},
//    onSearchClick: () -> Unit = {}
) {
    var hasFocus by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { state -> hasFocus = state.isFocused },
            value = value.toString(),
            onValueChange = { onValueChange(it) },
            label = { Text(label) },
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null
                )
            },
//            trailingIcon = {
//                IconButton(onClick = { onSearchClick() }) {
//                    Icon(
//                        imageVector = Icons.Default.Search,
//                        contentDescription = "Search"
//                    )
//                }
//            },
        )

        if (hasFocus && suggestions.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                items(suggestions) { suggestion ->
                    Text(
                        text = suggestion.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSuggestionClick(suggestion)
                                onValueChange(suggestion.title)
                                hasFocus = false
                            }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}
