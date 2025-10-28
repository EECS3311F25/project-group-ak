package org.example.project.model

import kotlinx.datetime.LocalDate

data class Event(
    val title: String,
    val date: LocalDate,
    val description: String = "",
    val location: String = "",
)
