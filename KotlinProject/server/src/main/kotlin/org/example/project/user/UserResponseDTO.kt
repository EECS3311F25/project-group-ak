package org.example.project.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class UserResponseDto(
    val id: Int,
    @SerialName("user_name")
    val userName: String,
    @SerialName("user_email")
    val userEmail: String
)

@Serializable
data class UserRetrieveResponse(
    val message: String,
    val data: UserResponseDto
)
