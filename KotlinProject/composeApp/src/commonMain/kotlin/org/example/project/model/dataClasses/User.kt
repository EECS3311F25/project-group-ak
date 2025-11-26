package org.example.project.model.dataClasses

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val pfpUrl: String? = null, // TODO: Create default pfp
)
