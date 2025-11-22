package org.example.project.model.dataClasses

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class Event(
    // id is 6 digits string of numbers based on time now
    val id: String = (Clock.System.now().toEpochMilliseconds() % 1000000).toString(),
    val title: String,
    val duration: Duration,
    val description: String = "",
    val location: String = "",
    val imageUrl: String? = null
)
