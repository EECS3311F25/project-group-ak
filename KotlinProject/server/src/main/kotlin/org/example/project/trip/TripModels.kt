package org.example.project.trip

@kotlinx.serialization.Serializable
data class Duration(
    val start: String, // ISO string "2025-11-03T10:00:00"
    val end: String
)

@kotlinx.serialization.Serializable
data class Location(
    val latitude: Double,
    val longitude: Double
)

@kotlinx.serialization.Serializable
data class Event(
    val id: String,
    val title: String,
    val description: String,
    val location: Location,
    val duration: Duration
)

@kotlinx.serialization.Serializable
data class Trip(
    val id: String,
    val name: String,
    val owner: String,
    val users: MutableList<String>,
    val events: MutableList<Event>,
    val duration: Duration
)