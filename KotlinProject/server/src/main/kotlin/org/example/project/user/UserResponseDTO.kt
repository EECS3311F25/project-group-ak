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

fun UserCreateDto.toDao(): UserDAO = UserDAO.new {
    userName = this@toDao.userName
    userEmail = this@toDao.userEmail
    userPassword = this@toDao.userPassword
}