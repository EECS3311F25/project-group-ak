# AI Trip Summary Implementation Plan

## Overview
Generate AI-powered summaries for trips based on trip data (title, dates, events, participants, location).

## Architecture Flow

```
┌─────────────────────────────────────────────────────────────┐
│                    COMPOSE FRONTEND                          │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ TripView → TripSummarySection                        │   │
│  │   ↓                                                   │   │
│  │ TripViewModel.generateSummary()                      │   │
│  │   ↓                                                   │   │
│  │ TripApiService.getTripSummary(tripId)                │   │
│  └───────────────────────┬──────────────────────────────┘   │
└──────────────────────────┼──────────────────────────────────┘
                           │ HTTP POST /api/trips/{id}/summary
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    KTOR BACKEND                             │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ AISummaryRoutes.kt                                   │   │
│  │   ↓                                                   │   │
│  │ AISummaryService.generateSummary(trip)               │   │
│  │   ↓                                                   │   │
│  │ Format prompt → Call AI API → Return summary         │   │
│  └──────────────────────────────────────────────────────┘   │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    AI SERVICE (OpenAI/Gemini/etc)          │
└─────────────────────────────────────────────────────────────┘
```

## Implementation Checklist

### Phase 1: Backend Setup (Ktor Server)

#### 1.1 Add AI Client Dependencies
**File:** `server/build.gradle.kts`
- Add Ktor Client for making HTTP requests to AI API
- Add Kotlinx Serialization (if not already present)

```kotlin
implementation("io.ktor:ktor-client-core:2.3.5")
implementation("io.ktor:ktor-client-cio:2.3.5")
implementation("io.ktor:ktor-client-content-negotiation:2.3.5")
implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.5")
```

#### 1.2 Create AI Service Configuration
**File:** `server/src/main/kotlin/org/example/project/config/AIConfig.kt`
- Load AI API key from environment variables
- Configure API endpoint (e.g., OpenAI, Gemini)
- Set default model and parameters

```kotlin
data class AIConfig(
    val apiKey: String = System.getenv("AI_API_KEY") ?: "",
    val apiUrl: String = System.getenv("AI_API_URL") ?: "https://api.openai.com/v1/chat/completions",
    val model: String = System.getenv("AI_MODEL") ?: "gpt-3.5-turbo"
)
```

#### 1.3 Create AI Service
**File:** `server/src/main/kotlin/org/example/project/service/AISummaryService.kt`
- Format trip data into a prompt
- Make HTTP request to AI API
- Parse response and extract summary
- Handle errors and rate limits

**Key Functions:**
- `generateSummary(trip: Trip): String`
- `formatPrompt(trip: Trip): String`
- `callAIService(prompt: String): String`

#### 1.4 Create DTOs
**File:** `server/src/main/kotlin/org/example/project/dto/TripSummaryDto.kt`
```kotlin
@Serializable
data class TripSummaryRequest(
    val tripId: String
)

@Serializable
data class TripSummaryResponse(
    val summary: String,
    val generatedAt: String
)
```

#### 1.5 Create Routes
**File:** `server/src/main/kotlin/org/example/project/routes/AISummaryRoutes.kt`
- `POST /api/trips/{tripId}/summary` - Generate summary for a trip
- Handle authentication (if needed)
- Error handling and status codes

#### 1.6 Register Routes
**File:** `server/src/main/kotlin/org/example/project/Routing.kt` or `Application.kt`
- Add `aiSummaryRoutes()` to routing configuration

### Phase 2: Frontend Setup (Compose)

#### 2.1 Create HTTP Client Factory
**File:** `composeApp/src/commonMain/kotlin/org/example/project/data/api/HttpClientFactory.kt`
- Create singleton HttpClient instance
- Configure base URL, serialization, logging
- Set up error handling

```kotlin
fun createHttpClient(): HttpClient {
    return HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        defaultRequest {
            url("http://localhost:8080")
        }
    }
}
```

#### 2.2 Create API Service
**File:** `composeApp/src/commonMain/kotlin/org/example/project/data/api/TripApiService.kt`
- Function to call summary endpoint
- Handle network errors
- Return summary string or error

```kotlin
interface TripApiService {
    suspend fun generateTripSummary(tripId: String): Result<String>
}
```

#### 2.3 Create ViewModel (if not exists)
**File:** `composeApp/src/commonMain/kotlin/org/example/project/viewModel/TripViewModel.kt`
- State management for summary (loading, success, error)
- Function to trigger summary generation
- Store generated summary

```kotlin
class TripViewModel(
    private val tripApiService: TripApiService
) : ViewModel() {
    var summaryState by mutableStateOf<SummaryState>(SummaryState.Idle)
        private set
    
    fun generateSummary(tripId: String) {
        // Call API and update state
    }
}

sealed class SummaryState {
    object Idle : SummaryState()
    object Loading : SummaryState()
    data class Success(val summary: String) : SummaryState()
    data class Error(val message: String) : SummaryState()
}
```

#### 2.4 Update TripSummary Component
**File:** `composeApp/src/commonMain/kotlin/org/example/project/view/TripView/TripViewComponents/TripSummary.kt`
- Add loading indicator
- Show error message if generation fails
- Add "Generate Summary" button (optional)
- Display AI-generated summary

#### 2.5 Integrate in TripView
**File:** `composeApp/src/commonMain/kotlin/org/example/project/view/TripView/TripView.kt`
- Pass ViewModel to TripSummarySection
- Trigger summary generation on trip load (or on button click)

### Phase 3: Testing & Error Handling

#### 3.1 Backend Tests
- Test AI service with mock responses
- Test error handling (API failures, rate limits)
- Test prompt formatting

#### 3.2 Frontend Tests
- Test API service calls
- Test ViewModel state changes
- Test UI error states

#### 3.3 Error Scenarios
- Network failures → Show retry option
- AI API errors → Show fallback message
- Invalid trip data → Handle gracefully
- Timeout → Show timeout message

### Phase 4: Optional Enhancements

#### 4.1 Caching
- Store generated summaries in database
- Check if summary exists before generating
- Allow regeneration with timestamp

#### 4.2 Prompt Engineering
- Refine prompt for better summaries
- Include trip context (season, location type, etc.)
- Make summaries concise and engaging

#### 4.3 UI Polish
- Add animation for loading state
- Add "Regenerate" button
- Show generation timestamp
- Add character limit/truncation

## File Structure

```
KotlinProject/
├── server/
│   └── src/main/kotlin/org/example/project/
│       ├── config/
│       │   └── AIConfig.kt                    [NEW]
│       ├── service/
│       │   └── AISummaryService.kt            [NEW]
│       ├── dto/
│       │   └── TripSummaryDto.kt              [NEW]
│       ├── routes/
│       │   └── AISummaryRoutes.kt             [NEW]
│       └── Routing.kt                         [MODIFY]
│
└── composeApp/
    └── src/commonMain/kotlin/org/example/project/
        ├── data/
        │   └── api/
        │       ├── HttpClientFactory.kt        [NEW]
        │       └── TripApiService.kt           [NEW]
        ├── viewModel/
        │   └── TripViewModel.kt                [NEW or MODIFY]
        └── view/TripView/TripViewComponents/
            └── TripSummary.kt                 [MODIFY]
```

## Environment Variables

Add to your environment or `.env` file:
```bash
# AI Service Configuration
AI_API_KEY=your_api_key_here
AI_API_URL=https://api.openai.com/v1/chat/completions  # or Gemini URL
AI_MODEL=gpt-3.5-turbo  # or gemini-pro, etc.
```

## Example Prompt Format

```
Generate a brief, engaging summary (2-3 sentences) for this trip:

Title: Mountain Retreat
Dates: September 5-12, 2025
Location: Mountain Resort
Participants: Charlie, Diana

Events:
- Day 1: Arrival & Check-in (15:00 - 17:00)
- Day 2: Hiking Trail (09:00 - 14:00)
- Day 3: Mountain Viewpoint (10:00 - 12:00)

Make it sound exciting and capture the essence of the trip.
```

## Next Steps

1. **Choose AI Provider**: Decide on OpenAI, Gemini, or another service
2. **Get API Key**: Sign up and obtain API credentials
3. **Start with Backend**: Implement AI service and routes first
4. **Test Backend**: Use curl/Postman to test the endpoint
5. **Implement Frontend**: Connect UI to backend API
6. **Test End-to-End**: Verify full flow works
7. **Add Error Handling**: Handle edge cases
8. **Polish UI**: Add loading states and animations

## Estimated Time

- Backend Implementation: 4-6 hours
- Frontend Implementation: 3-4 hours
- Testing & Debugging: 2-3 hours
- **Total: ~9-13 hours**

