package org.example.project.presentation.calendar.navigation

import androidx.compose.runtime.*
import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement

/**
 * Creates a DOM-based bottom sheet that appears above the fixed-position map
 */
@Composable
actual fun RenderBottomSheetOverlay(
    startTitle: String,
    startAddress: String,
    endTitle: String,
    endAddress: String,
    distance: Double,
    drivingDuration: Double,
    walkingDuration: Double,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    onCleanup: () -> Unit
) {
    val sheetId = remember { "bottom-sheet-${(0..999999).random()}" }
    
    LaunchedEffect(sheetId, isExpanded, distance, drivingDuration, walkingDuration) {
        createOrUpdateBottomSheet(
            sheetId = sheetId,
            startTitle = startTitle,
            startAddress = startAddress,
            endTitle = endTitle,
            endAddress = endAddress,
            distance = distance,
            drivingDuration = drivingDuration,
            walkingDuration = walkingDuration,
            isExpanded = isExpanded,
            onExpandToggle = onExpandToggle
        )
    }
    
    DisposableEffect(sheetId) {
        onDispose {
            document.getElementById(sheetId)?.remove()
            onCleanup()
        }
    }
}

private fun createOrUpdateBottomSheet(
    sheetId: String,
    startTitle: String,
    startAddress: String,
    endTitle: String,
    endAddress: String,
    distance: Double,
    drivingDuration: Double,
    walkingDuration: Double,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit
) {
    // Find the ComposeTarget container to get positioning
    val composeTarget = document.getElementById("ComposeTarget") as? HTMLDivElement
    if (composeTarget == null) {
        console.error("Cannot find ComposeTarget element for bottom sheet")
        return
    }
    
    val rect = composeTarget.getBoundingClientRect()
    
    // Check if sheet already exists
    var sheet = document.getElementById(sheetId) as? HTMLDivElement
    
    if (sheet == null) {
        // Create new bottom sheet
        sheet = document.createElement("div") as HTMLDivElement
        sheet.id = sheetId
        document.body?.appendChild(sheet)
    }
    
    // Set styles
    val height = if (isExpanded) "400px" else "120px"
    sheet.style.apply {
        position = "fixed"
        bottom = "0px"
        left = "${rect.left}px"
        width = "${rect.width}px"
        maxWidth = "360px"
        this.height = height
        backgroundColor = "white"
        borderTopLeftRadius = "16px"
        borderTopRightRadius = "16px"
        boxShadow = "0 -4px 12px rgba(0,0,0,0.15)"
        padding = "16px"
        zIndex = "1000"
        transition = "height 0.3s ease-in-out"
        asDynamic().overflow = "hidden"
        asDynamic().boxSizing = "border-box"
    }
    
    // Format distance and durations
    val distanceStr = "${(distance * 10).toInt() / 10.0}"
    val drivingDurationStr = "${drivingDuration.toInt()}"
    val walkingDurationStr = "${walkingDuration.toInt()}"
    
    // Create HTML content
    val expandIcon = if (isExpanded) "&#9660;" else "&#9650;"  // Down arrow : Up arrow
    
    // Ensure Google Material Icons font is loaded
    ensureMaterialIconsLoaded()
    
    sheet.innerHTML = """
        <div style="display: flex; flex-direction: column; align-items: center; height: 100%; gap: 12px;">
            <!-- Expand/Collapse button -->
            <div id="$sheetId-toggle" style="display: flex; justify-content: center; align-items: center; cursor: pointer; padding: 4px;">
                <span style="font-size: 20px; color: #666;">$expandIcon</span>
            </div>
            
            ${if (!isExpanded) """
            <!-- Compact view (only visible when collapsed) -->
            <div style="display: flex; flex-direction: column; align-items: center;">
                <div style="font-size: 16px; font-weight: 500; color: #000;">Route Info</div>
                <div style="font-size: 14px; color: #666;">$distanceStr km • $drivingDurationStr min • $walkingDurationStr min</div>
            </div>
            """ else ""}
            
            ${if (isExpanded) """
            <!-- From-To Section -->
            <div style="display: flex; flex-direction: column; align-items: center; gap: 8px; width: 100%;">
                <div style="display: flex; flex-direction: column; align-items: center;">
                    <div style="font-size: 16px; font-weight: 500; color: #000; text-align: center;">$startTitle</div>
                    <div style="font-size: 12px; color: #666; text-align: center;">$startAddress</div>
                </div>
                <div style="font-size: 24px; color: #666;">&#8595;</div>
                <div style="display: flex; flex-direction: column; align-items: center;">
                    <div style="font-size: 16px; font-weight: 500; color: #000; text-align: center;">$endTitle</div>
                    <div style="font-size: 12px; color: #666; text-align: center;">$endAddress</div>
                </div>
            </div>
            
            <div style="height: 8px;"></div>
            
            <!-- Duration Section -->
            <div style="display: flex; flex-direction: row; justify-content: center; gap: 40px; width: 100%; padding: 16px; background-color: #f5f5f5; border-radius: 8px;">
                <!-- Walking -->
                <div style="display: flex; flex-direction: column; align-items: center; gap: 4px;">
                    <span class="material-icons" style="font-size: 40px; color: #666;">directions_walk</span>
                    <div style="font-size: 18px; font-weight: 500; color: #000;">$walkingDurationStr min</div>
                </div>
                
                <!-- Driving -->
                <div style="display: flex; flex-direction: column; align-items: center; gap: 4px;">
                    <span class="material-icons" style="font-size: 40px; color: #666;">directions_car</span>
                    <div style="font-size: 18px; font-weight: 500; color: #000;">$drivingDurationStr min</div>
                </div>
            </div>
            
            <div style="height: 8px;"></div>
            
            <!-- External Apps Section -->
            <div style="display: flex; flex-direction: row; justify-content: center; gap: 24px; width: 100%;">
                <!-- Google Maps -->
                <div style="display: flex; flex-direction: column; align-items: center; cursor: pointer; padding: 8px;">
                    <div style="width: 48px; height: 48px; background-color: #4285F4; border-radius: 8px; display: flex; align-items: center; justify-content: center;">
                        <span class="material-icons" style="font-size: 28px; color: white;">map</span>
                    </div>
                    <div style="font-size: 11px; color: #666; margin-top: 4px;">Maps</div>
                </div>
                
                <!-- Waze -->
                <div style="display: flex; flex-direction: column; align-items: center; cursor: pointer; padding: 8px;">
                    <div style="width: 48px; height: 48px; background-color: #33CCFF; border-radius: 8px; display: flex; align-items: center; justify-content: center;">
                        <span class="material-icons" style="font-size: 28px; color: white;">navigation</span>
                    </div>
                    <div style="font-size: 11px; color: #666; margin-top: 4px;">Waze</div>
                </div>
                
                <!-- Uber -->
                <div style="display: flex; flex-direction: column; align-items: center; cursor: pointer; padding: 8px;">
                    <div style="width: 48px; height: 48px; background-color: #000000; border-radius: 8px; display: flex; align-items: center; justify-content: center;">
                        <span class="material-icons" style="font-size: 28px; color: white;">local_taxi</span>
                    </div>
                    <div style="font-size: 11px; color: #666; margin-top: 4px;">Uber</div>
                </div>
            </div>
            """ else ""}
        </div>
    """.trimIndent()
    
    // Add click listener to toggle button
    val toggleButton = document.getElementById("$sheetId-toggle")
    toggleButton?.asDynamic().onclick = {
        onExpandToggle()
        null
    }
}

/**
 * Ensures Google Material Icons font is loaded
 */
private fun ensureMaterialIconsLoaded() {
    // Check if already loaded
    val existingLink = document.querySelector("link[href*='material-icons']")
    if (existingLink != null) {
        return
    }
    
    // Load Material Icons CSS
    val link = document.createElement("link") as org.w3c.dom.HTMLLinkElement
    link.rel = "stylesheet"
    link.href = "https://fonts.googleapis.com/icon?family=Material+Icons"
    document.head?.appendChild(link)
}
