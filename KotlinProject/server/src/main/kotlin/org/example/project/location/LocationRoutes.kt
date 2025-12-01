package com.example.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.uri
import org.example.project.http.HttpClientProvider
import org.example.project.location.MapBoxService

fun Route.locationRoutes() {

    val service = MapBoxService(HttpClientProvider.client)

    get("/suggest") {
        println("LocationRoutes: /suggest uri=${call.request.uri}")
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

suspend fun respondRetrieve(call: ApplicationCall, mapboxId: String?, sessionId: String?) {
        println("LocationRoutes: /retrieve uri=${call.request.uri} mapboxId=$mapboxId sessionId=$sessionId")

        if (mapboxId.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest, "Missing mapboxId")
            return
        }
        if (sessionId.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest, "Missing sessionId")
            return
        }

        val result = service.retrieve(mapboxId, sessionId)
        call.respond(result)
    }

    // Query param form: /retrieve?mapboxId=...&sessionId=...
    get("/retrieve") {
        respondRetrieve(
            call = call,
            mapboxId = call.request.queryParameters["mapboxId"],
            sessionId = call.request.queryParameters["sessionId"]
        )
    }

    // Path param form: /retrieve/{mapboxId}?sessionId=...
    get("/retrieve/{mapboxId}") {
        respondRetrieve(
            call = call,
            mapboxId = call.parameters["mapboxId"],
            sessionId = call.request.queryParameters["sessionId"]
        )
    }
}
