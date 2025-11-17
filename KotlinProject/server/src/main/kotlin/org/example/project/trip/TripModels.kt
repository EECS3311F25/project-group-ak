package org.example.project.trip

import kotlinx.serialization.Serializable

@Serializable
data class Duration(
    val start: String,   
    val end: String
)

@Serializable
data class Location(
    val latitude: Double,
    val longitude: Double,
    val images: List<String> = emptyList()
)

@Serializable
data class Event(
    val id: String,
    val title: String,
    val description: String? = null,
    val location: Location? = null,
    val duration: Duration
)

@Serializable
data class Trip(
    val id: String,
    val name: String,
    val owner: String,           
    val users: List<String>,     
    val events: List<Event> = emptyList(),
    val duration: Duration
)


@Serializable
data class TripCreateRequest(
    val name: String,
    val owner: String,
    val users: List<String> = emptyList(),
    val duration: Duration
)

@Serializable
data class TripUpdateRequest(
    val name: String? = null,
    val users: List<String>? = null,
    val duration: Duration? = null
)

// Event
@Serializable
data class EventCreateRequest(
    val title: String,
    val description: String? = null,
    val location: Location? = null,
    val duration: Duration
)

@Serializable
data class EventUpdateRequest(
    val title: String? = null,
    val description: String? = null,
    val location: Location? = null,
    val duration: Duration? = null
)