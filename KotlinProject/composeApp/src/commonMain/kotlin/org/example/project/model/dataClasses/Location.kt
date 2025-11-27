package org.example.project.model.dataClasses

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null,
    val title: String? = null
)

data class LocationSuggestion(
    val title: String,
    val id: String,
)