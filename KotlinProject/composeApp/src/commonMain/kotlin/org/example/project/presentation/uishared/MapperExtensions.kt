package org.example.project.presentation.uishared

import kotlinx.datetime.LocalTime
import org.example.project.model.dataClasses.Event
import org.example.project.model.dataClasses.Location

/**
 * Extension functions for converting domain models to presentation models
 */

/**
 * Converts an Event to a MapMarker for display on the map
 * Returns null if the event has no location
 */
fun Event.toMapMarker(): MapMarker? {
    return location?.let { loc ->
        MapMarker(
            latitude = loc.latitude,
            longitude = loc.longitude,
            title = title,
            description = description,
            address = loc.address ?: loc.title ?: "",
            startTime = duration.startTime,
            endTime = duration.endTime
        )
    }
}

/**
 * Converts a Location to a MapMarker for display on the map
 */
fun Location.toMapMarker(
    title: String, 
    description: String? = null,
    startTime: LocalTime? = null,
    endTime: LocalTime? = null
) = MapMarker(
    latitude = latitude,
    longitude = longitude,
    title = title,
    description = description ?: address,
    address = address ?: "",
    startTime = startTime,
    endTime = endTime
)
