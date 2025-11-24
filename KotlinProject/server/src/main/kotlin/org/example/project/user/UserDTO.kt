package org.example.project.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class UserCreateRequest(
    @SerialName("user_name")
    val userName: String,
    @SerialName("user_email")
    val userEmail: String,
    @SerialName("user_password")
    val userPassword: String
)

fun UserCreateRequest.toDao(): UserDAO = UserDAO.new {
    userName = this@toDao.userName
    userEmail = this@toDao.userEmail
    userPassword = this@toDao.userPassword
}

@Serializable
data class UserResponse(
    val id: Int,
    @SerialName("user_name")
    val userName: String,
    @SerialName("user_email")
    val userEmail: String
)

@Serializable
data class UserRetrieveResponse(
    val message: String,
    val data: UserResponse
)