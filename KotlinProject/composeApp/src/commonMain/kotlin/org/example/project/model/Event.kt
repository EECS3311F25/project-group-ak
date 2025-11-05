package org.example.project.model

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate

@Serializable
data class Event(
    val title: String,
    val duration: Duration,
    val description: String = "",
    val location: String = "",
)
