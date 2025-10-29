package org.example.project.model

data class User(
    val name: String,
    val email: String,
    val pfpUrl: String? = null, // TODO: Create default pfp
)
