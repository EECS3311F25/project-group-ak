package org.example.project.data.remote

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Server DTOS
@Serializable
data class LocationSuggestResponse(
    val suggestions: List<LocationSuggestion>
)

@Serializable
data class LocationSuggestion(
    val name: String,
    val mapbox_id: String
)

@Serializable
data class SimpleLocation(
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    @SerialName("name")
    val title: String?,
)

private const val SERVER_BASE_URL = "http://localhost:8080"

class RemoteLocationDataSource(
    private val client: HttpClient = HttpClientProvider.client
) {

    suspend fun suggest(query: String, sessionId: String): LocationSuggestResponse {
        return client.get("$SERVER_BASE_URL/suggest") {
            parameter("query", query)
            parameter("sessionId", sessionId)
        }.body()
    }

    suspend fun retrieve(mapboxId: String, sessionId: String): SimpleLocation {
        return client.get("$SERVER_BASE_URL/retrieve") {
            parameter("mapboxId", mapboxId)
            parameter("sessionId", sessionId)
        }.body()
    }
}