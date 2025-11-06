package org.example.project

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.project.trip.TripRepositoryMock

fun main() {
    embeddedServer(
        Netty,
        port = SERVER_PORT,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)

    //  TODO: initialize DB connection (to then do stuff)
    //  for each endpoint, it basically queries the DB, to then get stuff from the DB
    //  the stuff you retrieved from the DB goes into an HTTP response that gets sent from back -> front
}

fun Application.module() {
    routing {
        // endpoint test cũ
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