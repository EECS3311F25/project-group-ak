package org.example.project.model.dataClasses

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Trip(
    val id: String,
    val title: String,
    val duration: Duration,

    val description: String = "",
    val location: String = "",

    val users: List<User>,
    val events: List<Event> = emptyList(),

    val imageHeaderUrl: String? = null,
    val createdDate: LocalDate
)