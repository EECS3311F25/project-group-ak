package org.example.project

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import org.example.project.App

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    println("Starting Compose app")
    
    val target = document.getElementById("ComposeTarget")!!
    ComposeViewport(target) {
        App()
    }
}