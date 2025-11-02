package org.example.project.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val name: String,
    val pfpUrl: String? = null, // TODO: Create default pfp
)
