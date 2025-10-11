# Travel Planning App

**Travel Planning App** is a Kotlin-based platform that helps users plan, organize, and share trips collaboratively.  
Users can create detailed itineraries, manage events, vote on plans, and receive intelligent suggestions for nearby activities and travel optimizations.

# Installation
To build or run the project, use one of the following tasks:

| Task                                    | Description                                                          |
| -----------------------------------------|---------------------------------------------------------------------- |
| `./gradlew test`                        | Run the tests                                                        |
| `./gradlew build`                       | Build everything                                                     |
| `./gradlew buildFatJar`                 | Build an executable JAR of the server with all dependencies included |
| `./gradlew buildImage`                  | Build the docker image to use with the fat JAR                       |
| `./gradlew publishImageToLocalRegistry` | Publish the docker image locally                                     |
| `./gradlew run`                         | Run the server                                                       |
| `./gradlew runDocker`                   | Run using the local docker image                                     |

If the server starts successfully, you'll see the following output:

```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
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





