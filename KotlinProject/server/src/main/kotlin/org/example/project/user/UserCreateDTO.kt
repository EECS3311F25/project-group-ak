package org.example.project.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class UserCreateDto(
    @SerialName("user_name")
    val userName: String,
    @SerialName("user_email")
    val userEmail: String,
    @SerialName("user_password")
    val userPassword: String
)