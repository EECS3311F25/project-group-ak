# Navi

**Navi** is a Kotlin-based platform that helps users plan, organize, and share trips collaboratively.  
Users can create detailed itineraries, manage events, vote on plans, and receive intelligent suggestions for nearby activities and travel optimizations.

# Installation

KMP allows Navi to work on multiple platforms, the currently working one with Map feature is the JS Target.

### Requirements

- MapBox API key in .env
- Claude API key in .env
- Docker
- Java 21

### Run PostgreSQL in Docker 
  - On macOS/Linux
    under KotlinProject/
    ```shell
    docker compose up -d
    ```

### Run Ktor Backend
  - On macOS/Linux
    under KotlinProject/
    ```shell
    ./gradlew :server:run
    ```

### Run Python Server

    - How to get the the API and where to insert it
      1. The API key can generated from any LLM's website, it can be paid or free depending on the LLM's policy
          Example : Anthropic, OpenAI, Ollama, Google
      2. Any LLM API key can be used, it should be in working state
      3. Insert your API in .env file of python-ai-service directory, give it a name and store it
          - Example : Google_API_KEY  = sk-fjnsjf-fsbfbsjn-erijreijrn-fsnfj38349-jfsi24
      4. To run : it should be loaded inside ai_service.py file
          - load_dotenv() : loads the .env file and using os.getenv(API_Name) will load it
            - api_key = os.getenv("ANTHROPIC_API_KEY")
          
  Next Steps :

    1. Activate Python Environment
    2. Run the file
    
  - On macOS/Linux
    under python-ai-service/
    ```shell
    source .venv/bin/activate
    python3 ai_service.py
    ```
    
    
    

### Build and Run JS Target
  - on macOS/Linux
    ```shell
    ./gradlew :composeApp:jsBrowserDevelopmentRun
    ```
  - on Windows
    ```shell
    .\gradlew.bat :composeApp:jsBrowserDevelopmentRun
    ```

# Contribution

1.  Clone the repository and create a new branch off of `dev-sprint-n` , n being the sprint number: 
```
git clone https://github.com/EECS3311F25/project-group-ak.git
cd project-group-ak
git checkout -b name_for_new_branch develop
```
2.  Make your changes and test them thoroughly.
3. Submit a pull request to `dev-sprint-n` with a clear, detailed description of your changes and screenshots if relevant.

# Communication

Our Jira Board: https://team-ak.atlassian.net/jira/software/projects/SCRUM/boards/1?atlOrigin=eyJpIjoiMGI1NWU0N2ZmYzU1NDRjYWFiMzllYzEzYjIxMzYzMGUiLCJwIjoiaiJ9
Our Discord: https://discord.gg/B75MfygcSU





