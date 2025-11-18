package org.example.project.model.dataClasses

import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val title: String,
    val duration: Duration,
    val description: String = "",
    val location: String = "",
    val imageUrl: String? = null
)
