package org.example.project.model

import kotlinx.datetime.LocalDate

data class Trip(
    val title: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val description: String = "",
    val location: String = "", // TODO: Create Location data class
    val users: List<User>,
    val events: List<Event> = emptyList(),
)
