package org.example.project.plugins

import io.ktor.http.*
import io.ktor.serialization.JsonConvertException
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.project.Greeting

import org.example.project.trip.TripRepositoryMock
import org.example.project.user.User
import org.example.project.user.UserRepository


//  TODO: write HTTPS routing in this module
//  example: https://github.com/ktorio/ktor-documentation/blob/3.3.2/codeSnippets/snippets/tutorial-server-db-integration/src/main/kotlin/com/example/Serialization.kt


fun Application.configureSerialization(repository: UserRepository) {
    install(ContentNegotiation) {
        json()
    }

    //  TODO: implement unusual input handling

    //  user account routing
    routing {

        //  get user account entry by username (to check password against)
        get("/userByName/{userName}") {
            val userName = call.parameters["userName"]
            if (userName == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val user = repository.getUserByName(userName)
            if (user == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            call.respond(user)
        }

        //  create user account
        //  TODO: determine the HTTPS request format (there will be paths for creating trips / events too)
        //  -   initial idea: if creating user's path is ".../{userName}", then creating trip's path can be ".../{userName}/{tripName}"
        post {
            try {
                val user = call.receive<User>()
                repository.addUser(user)
                call.respond(HttpStatusCode.NoContent)
            }   catch (ex: IllegalStateException) {
                call.respond(HttpStatusCode.BadRequest)
            }   catch (ex: JsonConvertException) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        //  TODO: implement path for deleting user account (later sprint - coordinate w/ frontend devs)
        delete("/{userName}") {
            val userName = call.parameters["userName"]
            if (userName == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            if (repository.deleteUserByUsername(userName)) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        //  TODO: update user password (later sprint - coordinate w/ frontend devs)



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