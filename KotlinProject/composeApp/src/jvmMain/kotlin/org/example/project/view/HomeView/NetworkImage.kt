package org.example.project.view.HomeView

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
actual fun NetworkImage(url: String, contentDescription: String?, modifier: Modifier) {
    KamelImage(
        resource = asyncPainterResource(url),
        contentDescription = contentDescription,
        modifier = modifier
    )
}