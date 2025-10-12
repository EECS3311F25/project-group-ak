# Navi ~~

**Navi** is a Kotlin-based platform that helps users plan, organize, and share trips collaboratively.  
Users can create detailed itineraries, manage events, vote on plans, and receive intelligent suggestions for nearby activities and travel optimizations.

# Installation

To build and run the development version of the web app, use the run configuration from the run widget
in your IDE's toolbar or run it directly from the terminal:
- for the Wasm target (faster, modern browsers):
  - on macOS/Linux
    ```shell
    ./gradlew :composeApp:wasmJsBrowserDevelopmentRun
    ```
  - on Windows
    ```shell
    .\gradlew.bat :composeApp:wasmJsBrowserDevelopmentRun
    ```
- for the JS target (slower, supports older browsers):
  - on macOS/Linux
    ```shell
    ./gradlew :composeApp:jsBrowserDevelopmentRun
    ```
  - on Windows
    ```shell
    .\gradlew.bat :composeApp:jsBrowserDevelopmentRun
    ```
# Contribution

1.  Clone the repository and create a new branch off of `develop`: 
```
git clone https://github.com/EECS3311F25/project-group-ak.git
cd project-group-ak
git checkout -b name_for_new_branch develop
```
2.  Make your changes and test them thoroughly.
3. Submit a pull request to `develop` with a clear, detailed description of your changes and screenshots if relevant.





