package com.example.routes

import org.example.project.location.MapBoxService
import org.example.project.http.HttpClientProvider
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.locationRoutes() {

    val service = MapBoxService(HttpClientProvider.client)

    get("/suggest") {
        val query = call.request.queryParameters["query"]
        val sessionId = call.request.queryParameters["sessionId"]

        if (query.isNullOrBlank()) {
            return@get call.respond(HttpStatusCode.BadRequest, "Missing query")
        }
        if (sessionId.isNullOrBlank()) {
            return@get call.respond(HttpStatusCode.BadRequest, "Missing sessionId")
        }

        val result = service.suggest(query, sessionId)
        call.respond(result)
    }

    get("/retrieve") {
        val mapboxId = call.request.queryParameters["mapboxId"]
        val sessionId = call.request.queryParameters["sessionId"]

        if (mapboxId.isNullOrBlank()) {
            return@get call.respond(HttpStatusCode.BadRequest, "Missing mapboxId")
        }
        if (sessionId.isNullOrBlank()) {
            return@get call.respond(HttpStatusCode.BadRequest, "Missing sessionId")
        }

        val result = service.retrieve(mapboxId, sessionId)
        call.respond(result)
    }
}
