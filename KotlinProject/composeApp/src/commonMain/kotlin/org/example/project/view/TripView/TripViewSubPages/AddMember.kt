package org.example.project.view.TripView.TripViewSubPages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.example.project.controller.TripController.AddMemberComponent
import org.example.project.model.PRIMARY
import org.example.project.model.SECONDARY
import org.example.project.viewmodel.trip.AddMemberViewModel

@Composable
fun AddMember(
    component: AddMemberComponent,
    viewModel: AddMemberViewModel
) {
    val (name, setName) = remember { mutableStateOf("") }
    val uiState by viewModel.state.collectAsState()

    LaunchedEffect(uiState.didAddMember) {
        if (uiState.didAddMember) {
            component.goBack()
            viewModel.clearCompletion()
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    setName(it)
                    viewModel.clearError()
                },
                label = { Text("Name") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = PRIMARY,
                    unfocusedIndicatorColor = PRIMARY,
                    cursorColor = PRIMARY,
                    focusedLabelColor = PRIMARY,
                    unfocusedLabelColor = PRIMARY,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
            uiState.errorMessage?.let { error ->
                Text(text = error, color = Color.Red)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(40.dp)) {
                Button(
                    onClick = { viewModel.addMember(name) },
                    enabled = name.isNotBlank() && !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SECONDARY,
                        contentColor = PRIMARY
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier,
                            color = PRIMARY,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Confirm")
                    }
                }
                Button(
                    onClick = { component.goBack() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SECONDARY,
                        contentColor = PRIMARY
                    )
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}
