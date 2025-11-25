package org.example.project.presentation.uishared

import org.example.project.model.dataClasses.Location

/**
 * Extension functions for converting domain models to presentation models
 */

/**
 * Converts a Location to a MapMarker for display on the map, for now it's the same
 */
fun Location.toMapMarker(title: String, description: String? = null) = MapMarker(
    latitude = latitude,
    longitude = longitude,
    title = title,
    description = description ?: address,
    address = address ?: ""
)
