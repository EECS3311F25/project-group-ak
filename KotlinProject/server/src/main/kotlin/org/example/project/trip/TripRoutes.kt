package org.example.project.trip

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.tripRoutes() {

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
        if (trip == null) call.respond(HttpStatusCode.NotFound, "trip not found")
        else call.respond(trip)
    }

    // ======= EVENT ROUTES (SCRUM-59/60/61/62) =======

    // SCRUM-59: GET EVENT
    get("/trip/{tripId}/events/{eventId}") {
        val tripId = call.parameters["tripId"]
        val eventId = call.parameters["eventId"]
        if (tripId.isNullOrBlank() || eventId.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest, "tripId & eventId are required")
            return@get
        }
        val event = TripRepositoryMock.getEvent(tripId, eventId)
        if (event == null) call.respond(HttpStatusCode.NotFound, "event not found")
        else call.respond(event)
    }

    // SCRUM-60: CREATE NEW EVENT
    post("/trip/{tripId}/events") {
        val tripId = call.parameters["tripId"]
        if (tripId.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest, "tripId is required")
            return@post
        }
        val newEvent = try {
            call.receive<Event>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, "invalid body: ${e.message}")
            return@post
        }
        val created = TripRepositoryMock.addEvent(tripId, newEvent)
        if (created == null) call.respond(HttpStatusCode.NotFound, "trip not found")
        else call.respond(HttpStatusCode.Created, created)
    }

    // SCRUM-62: UPDATE EVENT
    put("/trip/{tripId}/events/{eventId}") {
        val tripId = call.parameters["tripId"]
        val eventId = call.parameters["eventId"]
        if (tripId.isNullOrBlank() || eventId.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest, "tripId & eventId are required")
            return@put
        }
        val body = try {
            call.receive<Event>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, "invalid body: ${e.message}")
            return@put
        }
        val updated = TripRepositoryMock.updateEvent(tripId, eventId, body)
        if (updated == null) call.respond(HttpStatusCode.NotFound, "event not found")
        else call.respond(HttpStatusCode.OK, updated)
    }

    // SCRUM-61: DELETE EVENT
    delete("/trip/{tripId}/events/{eventId}") {
        val tripId = call.parameters["tripId"]
        val eventId = call.parameters["eventId"]
        if (tripId.isNullOrBlank() || eventId.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest, "tripId & eventId are required")
            return@delete
        }
        val ok = TripRepositoryMock.deleteEvent(tripId, eventId)
        if (!ok) call.respond(HttpStatusCode.NotFound, "event not found")
        else call.respond(HttpStatusCode.NoContent)
    }
}