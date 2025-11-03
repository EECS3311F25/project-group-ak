package org.example.project.trip

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.tripRoutes() {
    route("/trip") {

        get {
            val trips = TripRepositoryMock.getAllForUser("kai")
            call.respond(trips)
        }


        get("/invited") {
            val invited = TripRepositoryMock.getInvited("kai")
            call.respond(invited)
        }


        get("/{id}") {
            val id = call.parameters["id"]
            if (id.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "id is required")
                return@get
            }
            val trip = TripRepositoryMock.getById(id)
            if (trip == null) call.respond(HttpStatusCode.NotFound, "trip not found")
            else call.respond(trip)
        }
    }
}