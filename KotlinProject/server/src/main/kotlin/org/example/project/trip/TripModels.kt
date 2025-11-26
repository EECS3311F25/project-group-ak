package org.example.project.trip

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val latitude: Double,
    val longitude: Double,
    val images: List<String> = emptyList()
)