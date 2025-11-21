package org.example.project.view

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.example.project.data.remote.RemoteTripDataSource

@Composable
fun ApiTestView() {
    val dataSource = remember { RemoteTripDataSource() }
    var result by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = {
                isLoading = true
                result = null
                scope.launch {
                    try {
                        result = dataSource.helloWorld()
                    } catch (e: Exception) {
                        result = "Error: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Loading..." else "Call HelloWorld API")
        }
        result?.let {
            Text(it)
        }
    }
}
