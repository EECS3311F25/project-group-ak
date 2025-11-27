package org.example.project.presentation.map.uicomponents

import androidx.compose.runtime.*
import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLSelectElement

/**
 * Web-specific day dropdown overlay using DOM manipulation
 * This ensures the dropdown appears on top of the fixed-position map
 */
@Composable
actual fun RenderDayDropdownOverlay(
    days: List<String>,
    selectedDayIndex: Int,
    onDaySelected: (Int) -> Unit,
    onCleanup: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    
    DisposableEffect(days) {
        // Find ComposeTarget to position the dropdown correctly
        val composeTarget = document.getElementById("ComposeTarget") as? HTMLDivElement
        if (composeTarget == null) {
            console.error("MapViewDayDropdown: Cannot find ComposeTarget element")
            return@DisposableEffect onDispose { }
        }
        
        val rect = composeTarget.getBoundingClientRect()
        
        // Create dropdown container
        val dropdownContainer = document.createElement("div") as HTMLDivElement
        dropdownContainer.id = "map-day-dropdown"
        dropdownContainer.style.apply {
            position = "fixed"
            top = "${rect.top + 16}px"
            left = "${rect.left + 16}px" // 16px from left edge of ComposeTarget
            zIndex = "1001" // Above map (1) and bottom sheet (1000)
            minWidth = "120px"
            backgroundColor = "white"
            borderRadius = "8px"
            boxShadow = "0 2px 8px rgba(0, 0, 0, 0.2)"
            cursor = "pointer"
            transition = "all 0.2s ease"
        }
        dropdownContainer.style.asDynamic().overflow = "hidden"
        
        // Create the selected day display (always visible)
        val selectedDisplay = document.createElement("div") as HTMLDivElement
        selectedDisplay.id = "map-day-dropdown-display"
        selectedDisplay.style.apply {
            padding = "12px 16px"
            display = "flex"
            alignItems = "center"
            justifyContent = "space-between"
            fontFamily = "system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif"
            fontSize = "14px"
            fontWeight = "500"
            color = "#000000"
        }
        
        // Function to update the displayed text
        val updateDisplayText: (Int) -> Unit = { index ->
            val selectedText = days.getOrElse(index) { "All Days" }
            selectedDisplay.innerHTML = """
                <span>$selectedText</span>
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg" style="margin-left: 8px;">
                    <path d="M4 6L8 10L12 6" stroke="#000000" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
            """.trimIndent()
        }
        
        // Set initial text
        updateDisplayText(selectedDayIndex)
        
        // Create dropdown menu (hidden by default)
        val dropdownMenu = document.createElement("div") as HTMLDivElement
        dropdownMenu.id = "map-day-dropdown-menu"
        dropdownMenu.style.apply {
            display = "none"
            borderTop = "1px solid #e0e0e0"
            maxHeight = "240px"
        }
        dropdownMenu.style.asDynamic().overflowY = "auto"
        
        // Function to update option backgrounds based on selection
        val updateOptionBackgrounds: (Int) -> Unit = { currentIndex ->
            // Update "All Days" option
            val allDaysOption = document.getElementById("map-day-option-all") as? HTMLDivElement
            allDaysOption?.style?.backgroundColor = if (currentIndex == -1) "#f0f0f0" else "white"
            
            // Update day options
            days.forEachIndexed { index, _ ->
                val option = document.getElementById("map-day-option-$index") as? HTMLDivElement
                option?.style?.backgroundColor = if (index == currentIndex) "#f0f0f0" else "white"
            }
        }
        
        // Add "All Days" option
        val allDaysOption = document.createElement("div") as HTMLDivElement
        allDaysOption.id = "map-day-option-all"
        allDaysOption.textContent = "All Days"
        allDaysOption.style.apply {
            padding = "10px 16px"
            fontFamily = "system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif"
            fontSize = "14px"
            color = "#000000"
            backgroundColor = if (selectedDayIndex == -1) "#f0f0f0" else "white"
            cursor = "pointer"
        }
        allDaysOption.asDynamic().onmouseenter = {
            val currentBg = allDaysOption.style.backgroundColor
            if (currentBg != "rgb(240, 240, 240)") { // Not selected
                allDaysOption.style.backgroundColor = "#f5f5f5"
            }
        }
        allDaysOption.asDynamic().onmouseleave = {
            val currentSelectedIndex = (document.getElementById("map-day-dropdown") as? HTMLDivElement)?.asDynamic()?.selectedIndex as? Int ?: selectedDayIndex
            allDaysOption.style.backgroundColor = if (currentSelectedIndex == -1) "#f0f0f0" else "white"
        }
        allDaysOption.onclick = {
            (document.getElementById("map-day-dropdown") as? HTMLDivElement)?.asDynamic()?.selectedIndex = -1
            onDaySelected(-1)
            updateDisplayText(-1)
            updateOptionBackgrounds(-1)
            dropdownMenu.style.display = "none"
            isExpanded = false
            it.stopPropagation()
        }
        dropdownMenu.appendChild(allDaysOption)
        
        // Add day options
        days.forEachIndexed { index, day ->
            val option = document.createElement("div") as HTMLDivElement
            option.id = "map-day-option-$index"
            option.textContent = day
            option.style.apply {
                padding = "10px 16px"
                fontFamily = "system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif"
                fontSize = "14px"
                color = "#000000"
                backgroundColor = if (index == selectedDayIndex) "#f0f0f0" else "white"
                cursor = "pointer"
            }
            option.asDynamic().onmouseenter = {
                val currentBg = option.style.backgroundColor
                if (currentBg != "rgb(240, 240, 240)") { // Not selected
                    option.style.backgroundColor = "#f5f5f5"
                }
            }
            option.asDynamic().onmouseleave = {
                val currentSelectedIndex = (document.getElementById("map-day-dropdown") as? HTMLDivElement)?.asDynamic()?.selectedIndex as? Int ?: selectedDayIndex
                option.style.backgroundColor = if (index == currentSelectedIndex) "#f0f0f0" else "white"
            }
            option.onclick = {
                (document.getElementById("map-day-dropdown") as? HTMLDivElement)?.asDynamic()?.selectedIndex = index
                onDaySelected(index)
                updateDisplayText(index)
                updateOptionBackgrounds(index)
                dropdownMenu.style.display = "none"
                isExpanded = false
                it.stopPropagation()
            }
            dropdownMenu.appendChild(option)
        }
        
        // Toggle dropdown on click
        selectedDisplay.onclick = {
            isExpanded = !isExpanded
            dropdownMenu.style.display = if (isExpanded) "block" else "none"
            it.stopPropagation()
        }
        
        // Close dropdown when clicking outside
        val closeDropdown = {
            isExpanded = false
            dropdownMenu.style.display = "none"
        }
        
        document.asDynamic().addEventListener("click", closeDropdown)
        
        // Add hover effect to container
        dropdownContainer.asDynamic().onmouseenter = {
            dropdownContainer.style.boxShadow = "0 4px 12px rgba(0, 0, 0, 0.3)"
        }
        dropdownContainer.asDynamic().onmouseleave = {
            dropdownContainer.style.boxShadow = "0 2px 8px rgba(0, 0, 0, 0.2)"
        }
        
        // Store the selected index on the container
        dropdownContainer.asDynamic().selectedIndex = selectedDayIndex
        
        // Append elements
        dropdownContainer.appendChild(selectedDisplay)
        dropdownContainer.appendChild(dropdownMenu)
        document.body?.appendChild(dropdownContainer)
        
        console.log("MapViewDayDropdown: Day dropdown overlay created with ${days.size} days")
        
        onDispose {
            document.asDynamic().removeEventListener("click", closeDropdown)
            dropdownContainer.remove()
            onCleanup()
            console.log("MapViewDayDropdown: Day dropdown overlay removed")
        }
    }
}
