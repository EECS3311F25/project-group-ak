package org.example.project.trip

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.tripRoutes() {

    /* ========== TRIP CRUD (SCRUM-70,71,72,73) ========== */

    get("/trip") {
        val trips = TripRepositoryMock.getAllForUser("kai")
        call.respond(trips)
    }

    // GET /trip/{id}
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

    // POST /trip
    post("/trip") {
        val body = call.receive<TripCreateRequest>()
        val created = TripRepositoryMock.createTrip(body)
        call.respond(HttpStatusCode.Created, created)
    }

    // PUT /trip/{id}
    put("/trip/{id}") {
        val id = call.parameters["id"] ?: return@put call.respond(
            HttpStatusCode.BadRequest, "id is required"
        )
        val body = call.receive<TripUpdateRequest>()
        val updated = TripRepositoryMock.updateTrip(id, body)
        if (updated == null) call.respond(HttpStatusCode.NotFound, "trip not found")
        else call.respond(updated)
    }

    // DELETE /trip/{id}
    delete("/trip/{id}") {
        val id = call.parameters["id"] ?: return@delete call.respond(
            HttpStatusCode.BadRequest, "id is required"
        )
        val ok = TripRepositoryMock.deleteTrip(id)
        if (!ok) call.respond(HttpStatusCode.NotFound, "trip not found")
        else call.respond(HttpStatusCode.NoContent)
    }

    /* ========== EVENT CRUD (SCRUM-59,60,61,62) ========== */

    // GET /trip/{id}/events
    get("/trip/{id}/events") {
        val id = call.parameters["id"] ?: return@get call.respond(
            HttpStatusCode.BadRequest, "id is required"
        )
        val events = TripRepositoryMock.listEvents(id) ?: return@get call.respond(
            HttpStatusCode.NotFound, "trip not found"
        )
        call.respond(events)
    }

    // POST /trip/{id}/events
    post("/trip/{id}/events") {
        val id = call.parameters["id"] ?: return@post call.respond(
            HttpStatusCode.BadRequest, "id is required"
        )
        val body = call.receive<EventCreateRequest>()
        val created = TripRepositoryMock.createEvent(id, body)
            ?: return@post call.respond(HttpStatusCode.NotFound, "trip not found")
        call.respond(HttpStatusCode.Created, created)
    }

    // PUT /trip/{id}/events/{eventId}
    put("/trip/{id}/events/{eventId}") {
        val id = call.parameters["id"] ?: return@put call.respond(
            HttpStatusCode.BadRequest, "id is required"
        )
        val eventId = call.parameters["eventId"] ?: return@put call.respond(
            HttpStatusCode.BadRequest, "eventId is required"
        )
        val body = call.receive<EventUpdateRequest>()
        val updated = TripRepositoryMock.updateEvent(id, eventId, body)
            ?: return@put call.respond(HttpStatusCode.NotFound, "trip or event not found")
        call.respond(updated)
    }

    // DELETE /trip/{id}/events/{eventId}
    delete("/trip/{id}/events/{eventId}") {
        val id = call.parameters["id"] ?: return@delete call.respond(
            HttpStatusCode.BadRequest, "id is required"
        )
        val eventId = call.parameters["eventId"] ?: return@delete call.respond(
            HttpStatusCode.BadRequest, "eventId is required"
        )
        val ok = TripRepositoryMock.deleteEvent(id, eventId)
        if (!ok) call.respond(HttpStatusCode.NotFound, "trip or event not found")
        else call.respond(HttpStatusCode.NoContent)
    }
}