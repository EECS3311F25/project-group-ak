package org.example.project.data.repository

import org.example.project.data.remote.RemoteLocationDataSource
import org.example.project.data.remote.LocationSuggestion as RemoteLocationSuggestion
import org.example.project.data.remote.SimpleLocation as RemoteSimpleLocation
import org.example.project.model.dataClasses.Location
import org.example.project.model.dataClasses.LocationSuggestion

class LocationRepository(
    private val remote: RemoteLocationDataSource
) {
    suspend fun suggestLocations(query: String, sessionId: String): List<LocationSuggestion> {
        val dto = remote.suggest(query, sessionId)
        return dto.suggestions.map { it.toDomain() } // DTO → domain
    }

    suspend fun getLocation(mapBoxId: String, sessionId: String): Location {
        val dto = remote.retrieve(mapBoxId, sessionId)
        return dto.toDomain()
    }
}


private fun RemoteLocationSuggestion.toDomain(): LocationSuggestion =
    LocationSuggestion(
        title = name,
        id = mapbox_id
    )

private fun RemoteSimpleLocation.toDomain(): Location =
    Location(
        latitude = latitude,
        longitude = longitude,
        address = address,
        title = title
    )
