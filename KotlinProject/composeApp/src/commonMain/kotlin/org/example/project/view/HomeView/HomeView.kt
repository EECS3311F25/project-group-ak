package org.example.project.view.HomeView

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.controller.HomeViewComponent
import org.example.project.controller.HomeViewEvent

@Composable
fun HomeView(component: HomeViewComponent) {
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = { component.onEvent(HomeViewEvent.ClickButtonHomeView) }) {
                Text("Go to Trip")
            }
        }
    }
}
