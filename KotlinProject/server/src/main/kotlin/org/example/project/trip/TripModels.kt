package org.example.project.trip

data class Duration(
    val start: String,   
    val end: String     
)

data class Location(
    val latitude: Double,
    val longitude: Double,
    val images: List<String> = emptyList()
)

data class Event(
    val id: String,
    val title: String,
    val description: String,
    val location: Location,
    val duration: Duration
)

data class Trip(
    val id: String,
    val name: String,
    val owner: String,
    val users: List<String>,     
    val events: List<Event>,
    val duration: Duration
)