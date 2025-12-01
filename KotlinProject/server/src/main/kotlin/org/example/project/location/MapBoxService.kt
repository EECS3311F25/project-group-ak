package org.example.project.location

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import java.nio.file.Files
import java.nio.file.Paths
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class MapBoxService(
    private val client: HttpClient
) {
    // Lazily read token so creating MapBoxService does not fail during app/test startup
    private val token: String by lazy {
        System.getenv("MAPBOX_ACCESS_TOKEN")
            ?: System.getProperty("MAPBOX_ACCESS_TOKEN")
            ?: readTokenFromDotEnv()
            ?: error("MAPBOX_ACCESS_TOKEN is not set")
    }

    private val baseUrl = "https://api.mapbox.com/search/searchbox/v1"

    // GET /suggest
    suspend fun suggest(query: String, sessionId: String): LocationSuggestResponse {
        return client.get("$baseUrl/suggest") {
            // Mapbox param is "q", not "query"
            parameter("q", query)
            parameter("access_token", token)
            parameter("limit", 5)
            parameter("session_token", sessionId)
            parameter("language", "english")
        }.body()
    }

    // GET /retrieve → flattened to SimpleLocation
    suspend fun retrieve(mapBoxId: String, sessionId: String): SimpleLocation {
        val raw: RetrieveResponse = client.get("$baseUrl/retrieve/$mapBoxId") {
            parameter("access_token", token)
            parameter("session_token", sessionId)
        }.body()

        val feature = raw.features.firstOrNull()
            ?: error("No features returned for id $mapBoxId")

        val coords = feature.geometry?.coordinates
            ?: error("No coordinates returned for id $mapBoxId")

        val lon = coords.getOrNull(0) ?: error("Missing longitude")
        val lat = coords.getOrNull(1) ?: error("Missing latitude")
        val name = feature.name.orEmpty()
        val address = feature.properties?.full_address

        return SimpleLocation(
            title = name,
            latitude = lat,
            longitude = lon,
            address = address
        )
    }

    private fun readTokenFromDotEnv(): String? {
        val candidates = listOf(
            Paths.get(".env"),
            Paths.get("..", ".env"),
            Paths.get("..", "..", ".env")
        )
        for (path in candidates) {
            if (Files.exists(path)) {
                println("MapBoxService: trying token from ${path.toAbsolutePath()}")
                Files.readAllLines(path)
                    .map { it.trim() }
                    .firstOrNull { it.startsWith("MAPBOX_ACCESS_TOKEN=") }
                    ?.substringAfter("=")
                    ?.trim('"', ' ')
                    ?.takeIf { it.isNotBlank() }
                    ?.let { return it }
            }
        }
        println("MapBoxService: MAPBOX_ACCESS_TOKEN not found in ${candidates.joinToString()}")
        return null
    }
}

@Serializable
data class LocationSuggestResponse(
    val suggestions: List<LocationSuggestion>
)

@Serializable
data class LocationSuggestion(
    @SerialName("name")
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

@Serializable
data class RetrieveResponse(
    val features: List<RetrieveFeature>
)

@Serializable
data class RetrieveFeature(
    val name: String? = null,
    val geometry: Geometry? = null,
    val properties: Properties? = null
)

@Serializable
data class Geometry(
    val coordinates: List<Double> // [lon, lat]
)

@Serializable
data class Properties(
    val full_address: String? = null
)
