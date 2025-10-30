package org.example.project.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Trip(
    val title: String,
    val duration: Duration,
    val description: String = "",
    val location: String = "", // TODO: Create Location data class
    val users: List<User>,
    val events: List<Event> = emptyList(),
)
