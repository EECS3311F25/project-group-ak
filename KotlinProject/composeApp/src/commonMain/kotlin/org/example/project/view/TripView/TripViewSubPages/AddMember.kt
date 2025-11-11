package org.example.project.view.TripView.TripViewSubPages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.example.project.controller.AddMemberComponent
import org.example.project.model.PRIMARY
import org.example.project.model.SECONDARY

@Composable
fun AddMember(component: AddMemberComponent) {
    val (name, setName) = remember { mutableStateOf("") }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = setName,
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
            Row(horizontalArrangement = Arrangement.spacedBy(40.dp)) {
                Button(
                    onClick = { component.addMember(name) },
                    enabled = name.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SECONDARY,
                        contentColor = PRIMARY
                    )
                ) {
                    Text("Confirm")
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
