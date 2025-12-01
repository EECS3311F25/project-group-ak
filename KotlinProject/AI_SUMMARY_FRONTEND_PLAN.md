# AI Summary Frontend Integration Plan

## Overview
This document outlines the complete plan for integrating the AI-generated trip summary feature into the **Trip Summary** section of the TripView screen.

---

## Current State Analysis

### Existing Components
- **TripSummarySection** (`uicomponents/TripSummary.kt`): Currently displays `trip.description` in a dark grey card
- **TripView** (`TripView.kt`): Renders the trip screen with Header, Members, Summary, and Events
- **TripViewModel** (`TripViewModel.kt`): Manages trip state but has no AI summary functionality
- **Dependencies**: `ktor-client-cio` is already included, but `ktor-client-content-negotiation` is missing

### Current Flow
```
TripView → TripSummarySection(tripData.description)
         ↓
    Static text display
```

---

## Target State

### Desired Behavior
1. **On Trip View Load**: 
   - Show existing `trip.description` if available
   - Optionally auto-generate AI summary (or show button to generate)

2. **Generate Summary Button**:
   - User clicks "Generate AI Summary" button
   - Show loading state (spinner)
   - Call backend API: `POST /api/trips/{tripId}/summary`
   - Display generated summary
   - Handle errors gracefully

3. **UI States**:
   - **Empty**: Show placeholder or existing description
   - **Loading**: Show spinner + "Generating summary..."
   - **Success**: Show AI-generated summary
   - **Error**: Show error message + retry button

---

## Implementation Plan

### Phase 1: Dependencies & Infrastructure

#### 1.1 Update `composeApp/build.gradle.kts`
**Location**: `composeApp/build.gradle.kts` → `commonMain.dependencies`

**Add**:
```kotlin
implementation("io.ktor:ktor-client-content-negotiation:3.3.0")
```

**Why**: Needed for JSON serialization in HTTP client

---

### Phase 2: Data Layer

#### 2.1 Create HTTP Client Factory
**File**: `composeApp/src/commonMain/kotlin/org/example/project/data/api/HttpClientFactory.kt`

**Purpose**: Singleton HTTP client configured for API calls

**Code**:
```kotlin
package org.example.project.data.api

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object HttpClientFactory {
    private val baseUrl = "http://localhost:8080" // TODO: Move to config
    
    fun create(): HttpClient {
        return HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = false
                })
            }
            engine {
                // Configure timeouts if needed
            }
        }
    }
}
```

---

#### 2.2 Create DTOs (Data Transfer Objects)
**File**: `composeApp/src/commonMain/kotlin/org/example/project/data/api/dto/TripSummaryDto.kt`

**Purpose**: Match backend DTOs for request/response

**Code**:
```kotlin
package org.example.project.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class TripSummaryResponse(
    val summary: String,
    val generatedAt: String
)

@Serializable
data class ErrorResponse(
    val error: String,
    val message: String
)
```

---

#### 2.3 Create API Service
**File**: `composeApp/src/commonMain/kotlin/org/example/project/data/api/TripApiService.kt`

**Purpose**: Encapsulate API calls to backend

**Code**:
```kotlin
package org.example.project.data.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.example.project.data.api.dto.TripSummaryResponse

class TripApiService(
    private val client: HttpClient,
    private val baseUrl: String = "http://localhost:8080"
) {
    suspend fun generateTripSummary(tripId: String): Result<TripSummaryResponse> {
        return try {
            val response = client.post("$baseUrl/api/trips/$tripId/summary") {
                contentType(ContentType.Application.Json)
            }.body<TripSummaryResponse>()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

---

### Phase 3: ViewModel Layer

#### 3.1 Update TripViewModel
**File**: `composeApp/src/commonMain/kotlin/org/example/project/presentation/trip/TripViewModel.kt`

**Add**:
- AI summary state (loading, success, error)
- Function to generate summary
- Function to clear summary

**New State**:
```kotlin
// Add to TripViewModel class
private val _aiSummary = MutableStateFlow<String?>(null)
val aiSummary: StateFlow<String?> = _aiSummary.asStateFlow()

private val _isGeneratingSummary = MutableStateFlow(false)
val isGeneratingSummary: StateFlow<Boolean> = _isGeneratingSummary.asStateFlow()

private val _summaryError = MutableStateFlow<String?>(null)
val summaryError: StateFlow<String?> = _summaryError.asStateFlow()
```

**New Functions**:
```kotlin
fun generateAISummary() {
    viewModelScope.launch {
        val currentTrip = _trip.value ?: return@launch
        _isGeneratingSummary.value = true
        _summaryError.value = null
        
        // TODO: Inject TripApiService
        val apiService = TripApiService(HttpClientFactory.create())
        val result = apiService.generateTripSummary(currentTrip.id)
        
        _isGeneratingSummary.value = false
        
        result.onSuccess { response ->
            _aiSummary.value = response.summary
        }.onFailure { error ->
            _summaryError.value = error.message ?: "Failed to generate summary"
        }
    }
}

fun clearAISummary() {
    _aiSummary.value = null
    _summaryError.value = null
}
```

**Note**: You'll need to inject `TripApiService` via constructor for better testability.

---

### Phase 4: UI Layer

#### 4.1 Update TripSummarySection Component
**File**: `composeApp/src/commonMain/kotlin/org/example/project/presentation/trip/uicomponents/TripSummary.kt`

**New Signature**:
```kotlin
@Composable
fun TripSummarySection(
    tripSummary: String? = null,  // Existing description or AI summary
    aiSummary: String? = null,      // AI-generated summary
    isGenerating: Boolean = false,   // Loading state
    error: String? = null,          // Error message
    onGenerateClick: () -> Unit = {}, // Generate button callback
    onRetryClick: () -> Unit = {}     // Retry button callback
)
```

**UI States to Handle**:
1. **Default/Empty**: Show `tripSummary` if available, or placeholder
2. **Loading**: Show spinner + "Generating AI summary..."
3. **Success**: Show `aiSummary` with a subtle indicator it's AI-generated
4. **Error**: Show error message + retry button
5. **Generate Button**: Show button to trigger generation (if no summary exists)

**Enhanced Component Structure**:
```kotlin
@Composable
fun TripSummarySection(
    tripSummary: String? = null,
    aiSummary: String? = null,
    isGenerating: Boolean = false,
    error: String? = null,
    onGenerateClick: () -> Unit = {},
    onRetryClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFF212121))
            .padding(16.dp)
    ) {
        // Header with icon and title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Trip Summary",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Generate/Retry button (if needed)
            if (!isGenerating && aiSummary == null && error != null) {
                TextButton(onClick = onRetryClick) {
                    Text("Retry", color = Color.White)
                }
            } else if (!isGenerating && aiSummary == null && tripSummary.isNullOrBlank()) {
                TextButton(onClick = onGenerateClick) {
                    Text("Generate", color = Color.White)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Content area
        when {
            isGenerating -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Generating AI summary...",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            error != null -> {
                Text(
                    text = "Error: $error",
                    color = Color(0xFFFF6B6B),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            aiSummary != null -> {
                Text(
                    text = aiSummary,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
                // Optional: Add "AI Generated" badge
            }
            !tripSummary.isNullOrBlank() -> {
                Text(
                    text = tripSummary,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            else -> {
                Text(
                    text = "No summary available. Click 'Generate' to create an AI summary.",
                    color = Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}
```

**Additional Imports Needed**:
```kotlin
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.FontStyle
```

---

#### 4.2 Update TripView
**File**: `composeApp/src/commonMain/kotlin/org/example/project/presentation/trip/TripView.kt`

**Change Line 77**:
```kotlin
// OLD:
item { TripSummarySection(tripData.description) }

// NEW:
item {
    val aiSummary by viewModel.aiSummary.collectAsState()
    val isGenerating by viewModel.isGeneratingSummary.collectAsState()
    val summaryError by viewModel.summaryError.collectAsState()
    
    TripSummarySection(
        tripSummary = tripData.description,
        aiSummary = aiSummary,
        isGenerating = isGenerating,
        error = summaryError,
        onGenerateClick = { viewModel.generateAISummary() },
        onRetryClick = { viewModel.generateAISummary() }
    )
}
```

**Additional Imports**:
```kotlin
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
```

---

### Phase 5: Dependency Injection (Optional but Recommended)

#### 5.1 Create Service Locator or DI Container
**File**: `composeApp/src/commonMain/kotlin/org/example/project/di/ServiceLocator.kt`

**Purpose**: Centralize service creation

**Code**:
```kotlin
package org.example.project.di

import org.example.project.data.api.HttpClientFactory
import org.example.project.data.api.TripApiService

object ServiceLocator {
    private val httpClient = HttpClientFactory.create()
    val tripApiService: TripApiService = TripApiService(httpClient)
}
```

**Update TripViewModel Constructor**:
```kotlin
class TripViewModel(
    private val tripId: String,
    private val tripRepository: TripRepository,
    private val tripApiService: TripApiService = ServiceLocator.tripApiService
) : ViewModel() {
    // ... rest of the code
}
```

---

## Integration Steps Summary

1. ✅ **Add Dependency**: `ktor-client-content-negotiation` to `build.gradle.kts`
2. ✅ **Create HttpClientFactory**: Singleton HTTP client
3. ✅ **Create DTOs**: Match backend response structure
4. ✅ **Create TripApiService**: API call wrapper
5. ✅ **Update TripViewModel**: Add AI summary state and functions
6. ✅ **Update TripSummarySection**: Handle all UI states
7. ✅ **Update TripView**: Connect ViewModel to UI
8. ✅ **Test**: Verify loading, success, and error states

---

## UI/UX Considerations

### User Experience Flow
1. **First Visit**: User sees existing description (if any) or placeholder
2. **Generate Action**: User clicks "Generate" → Loading spinner appears
3. **Success**: AI summary replaces/updates the content
4. **Error**: Error message with retry option
5. **Subsequent Visits**: Could cache summary (future enhancement)

### Visual Design
- **Loading State**: Subtle spinner + text (non-intrusive)
- **Error State**: Red text + retry button
- **AI Badge**: Optional "✨ AI Generated" indicator
- **Button Placement**: Top-right of card header (as shown in your design)

---

## Testing Checklist

- [ ] HTTP client correctly configured
- [ ] API service calls backend endpoint
- [ ] Loading state displays correctly
- [ ] Success state shows AI summary
- [ ] Error state shows error message
- [ ] Retry button works
- [ ] Generate button triggers API call
- [ ] ViewModel state updates correctly
- [ ] UI updates reactively via StateFlow

---

## Future Enhancements (Phase 4 - Optional)

1. **Caching**: Store generated summaries in local storage
2. **Auto-generate**: Automatically generate on trip creation
3. **Regenerate**: Allow users to regenerate with different prompts
4. **Edit Summary**: Allow manual editing after AI generation
5. **Share Summary**: Share AI summary via share sheet
6. **Offline Support**: Cache summaries for offline viewing

---

## Notes

- **Base URL**: Currently hardcoded to `localhost:8080`. Consider moving to config file for different environments (dev/staging/prod)
- **Error Handling**: Consider specific error types (network, server, AI service unavailable)
- **Performance**: Consider debouncing if auto-generate is implemented
- **Accessibility**: Ensure loading/error states are accessible

---

## Code Files to Create/Modify

### New Files:
1. `composeApp/src/commonMain/kotlin/org/example/project/data/api/HttpClientFactory.kt`
2. `composeApp/src/commonMain/kotlin/org/example/project/data/api/dto/TripSummaryDto.kt`
3. `composeApp/src/commonMain/kotlin/org/example/project/data/api/TripApiService.kt`
4. `composeApp/src/commonMain/kotlin/org/example/project/di/ServiceLocator.kt` (optional)

### Modified Files:
1. `composeApp/build.gradle.kts` - Add dependency
2. `composeApp/src/commonMain/kotlin/org/example/project/presentation/trip/TripViewModel.kt` - Add AI summary state
3. `composeApp/src/commonMain/kotlin/org/example/project/presentation/trip/uicomponents/TripSummary.kt` - Enhanced UI
4. `composeApp/src/commonMain/kotlin/org/example/project/presentation/trip/TripView.kt` - Connect ViewModel

---

**Ready to implement?** Start with Phase 1 (dependencies) and work through each phase sequentially. Each phase builds on the previous one.

