package org.example.project.presentation.calendar.navigation

import androidx.compose.runtime.*
import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement

/**
 * Web-specific back button overlay using DOM manipulation
 * This ensures the button appears on top of the fixed-position map
 */
@Composable
actual fun RenderBackButtonOverlay(
    onBack: () -> Unit,
    onCleanup: () -> Unit
) {
    DisposableEffect(Unit) {
        // Find ComposeTarget to position the button correctly
        val composeTarget = document.getElementById("ComposeTarget") as? HTMLDivElement
        if (composeTarget == null) {
            console.error("NavigationViewBackButton: Cannot find ComposeTarget element")
            return@DisposableEffect onDispose { }
        }
        
        val rect = composeTarget.getBoundingClientRect()
        
        // Create back button container
        val buttonContainer = document.createElement("div") as HTMLDivElement
        buttonContainer.id = "navigation-back-button"
        buttonContainer.style.apply {
            position = "fixed"
            top = "${rect.top + 16}px"
            left = "${rect.left + 16}px"
            zIndex = "1001" // Above map (1) and bottom sheet (1000)
            width = "48px"
            height = "48px"
            borderRadius = "50%"
            backgroundColor = "white"
            boxShadow = "0 2px 8px rgba(0, 0, 0, 0.2)"
            display = "flex"
            alignItems = "center"
            justifyContent = "center"
            cursor = "pointer"
            transition = "all 0.2s ease"
        }
        
        // Add hover effect
        buttonContainer.asDynamic().onmouseenter = {
            buttonContainer.style.boxShadow = "0 4px 12px rgba(0, 0, 0, 0.3)"
            buttonContainer.style.transform = "scale(1.05)"
        }
        buttonContainer.asDynamic().onmouseleave = {
            buttonContainer.style.boxShadow = "0 2px 8px rgba(0, 0, 0, 0.2)"
            buttonContainer.style.transform = "scale(1)"
        }
        
        // Add click handler
        buttonContainer.onclick = {
            onBack()
        }
        
        // Create arrow icon using SVG
        buttonContainer.innerHTML = """
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z" fill="#000000"/>
            </svg>
        """.trimIndent()
        
        // Append to body
        document.body?.appendChild(buttonContainer)
        console.log("NavigationViewBackButton: Back button overlay created")
        
        onDispose {
            buttonContainer.remove()
            onCleanup()
            console.log("NavigationViewBackButton: Back button overlay removed")
        }
    }
}
