package org.example.project.presentation.uishared

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.suspendCancellableCoroutine
import org.jetbrains.skia.Image as SkiaImage
import org.w3c.fetch.Response
import org.w3c.files.FileReader
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.khronos.webgl.get
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Composable
actual fun NetworkImage(url: String, contentDescription: String?, modifier: Modifier) {
    var imageBitmap by remember(url) { mutableStateOf<ImageBitmap?>(null) }
    var isLoading by remember(url) { mutableStateOf(true) }
    var hasError by remember(url) { mutableStateOf(false) }

    LaunchedEffect(url) {
        try {
            isLoading = true
            hasError = false
            
            val response: Response = window.fetch(url).await()
            
            if (response.ok) {
                val blob = response.blob().await()
                
                // Use FileReader to convert blob to ArrayBuffer
                val arrayBuffer = suspendCancellableCoroutine<ArrayBuffer> { continuation ->
                    val reader = FileReader()
                    reader.onload = {
                        continuation.resume(reader.result as ArrayBuffer)
                    }
                    reader.onerror = {
                        continuation.resumeWithException(Exception("Failed to read blob"))
                    }
                    reader.readAsArrayBuffer(blob)
                }
                
                val int8Array = Int8Array(arrayBuffer)
                
                // Convert to ByteArray
                val byteArray = ByteArray(int8Array.length)
                for (i in 0 until int8Array.length) {
                    byteArray[i] = int8Array[i]
                }
                
                // Decode image
                val skiaImage = SkiaImage.makeFromEncoded(byteArray)
                imageBitmap = skiaImage.toComposeImageBitmap()
            } else {
                hasError = true
            }
        } catch (e: Exception) {
            console.error("Error loading image: $url", e)
            hasError = true
        } finally {
            isLoading = false
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        when {
            isLoading -> CircularProgressIndicator()
            hasError -> {
                // Show placeholder or error state
                Box(modifier = Modifier.fillMaxSize())
            }
            imageBitmap != null -> {
                Image(
                    bitmap = imageBitmap!!,
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
