package org.example.project

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

import org.example.project.trip.TripRepositoryMock
import org.example.project.userModel.UserRepository


//  TODO: write HTTPS routing in this module
//  example: https://github.com/ktorio/ktor-documentation/blob/3.3.2/codeSnippets/snippets/tutorial-server-db-integration/src/main/kotlin/com/example/Serialization.kt


fun Application.configureSerialization(repository: UserRepository) {
    
    install(ContentNegotiation) {
        json()
    }
    //  TODO: implement unusual input handling
    //  user account routing

    routing {
        route("/users") {
            get {
                val users = repository.allUsers()
                call.respond(users)
            }
            post {
                val user = call.receive<User>()
                repository.addUser(user)
                call.respond(HttpStatusCode.NoContent)
            }
            delete("/{userName}") {
                val userName = call.parameters["userName"]
                if (repository.deleteUserByUsername(userName)) {
                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }


        //  default sample endpoint
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }

        get("/trip") {
            val trips = TripRepositoryMock.getAllForUser("kai")
            call.respond(trips)

        }


        get("/trip/invited") {
            val invited = TripRepositoryMock.getInvited("kai")
            call.respond(invited)
        }

        get("/trip/{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "id is required")
                return@get
            }

            val trip = TripRepositoryMock.getById(id)
            if (trip == null) {
                call.respond(HttpStatusCode.NotFound, "trip not found")
            } else {
                call.respond(trip)
            }
        }
    }

}
