package org.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.retainedComponent
import org.example.project.controller.RootComponent

import com.sunildhiman90.kmauth.core.KMAuthInitializer
import com.sunildhiman90.kmauth.core.KMAuthPlatformContext

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val root = retainedComponent { RootComponent(it) }
        setContent {
            KMAuthInitializer.initContext(
                kmAuthPlatformContext = KMAuthPlatformContext(this)
            )
            App(root)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
