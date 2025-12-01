# AI Summary - Quick Reference for Demo



```bash
# Terminal 1: Database
docker-compose up -d
# OR: docker run -d -p 5432:5432 -e POSTGRES_PASSWORD=postgres postgres:15

# Terminal 2: Python AI Service
cd python-ai-service
python ai_service.py
# Should see: "Starting AI service on http://0.0.0.0:5001"

# Terminal 3: Kotlin Backend
cd KotlinProject
./gradlew :server:run
# Should see: "Application started" on port 8080

# Terminal 4: Frontend (Web)
cd KotlinProject
./gradlew :composeApp:jsBrowserDevelopmentRun
# Opens browser at http://localhost:8081
```



```
User clicks "Generate"
    ↓
Frontend (TripViewModel)
    ↓ HTTP POST /api/trips/{id}/summary
Kotlin Backend (AISummaryRoutes)
    ↓ Fetches from database
TripRepositoryImpl → PostgreSQL
    ↓ Returns Trip with events
AISummaryService
    ↓ HTTP POST to Python
Python Flask (ai_service.py)
    ↓ format_prompt()
Formatted Prompt
    ↓ API call
Claude AI (Anthropic)
    ↓ Generated Summary
Response flows back ↑
    ↓
Displayed in UI
```

