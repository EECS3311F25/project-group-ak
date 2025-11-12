package org.example.project.dto

import kotlinx.serialization.Serializable

/**
 * Data Transfer Objects for User API requests and responses.
 */

/**
 * DTO for user registration request.
 */
@Serializable
data class UserRegisterRequest(
    val userName: String,
    val userEmail: String,
    val userPassword: String
)

/**
 * DTO for user response (excludes password).
 */
@Serializable
data class UserResponse(
    val userName: String,
    val userEmail: String
)

/**
 * DTO for password update request.
 */
@Serializable
data class UpdatePasswordRequest(
    val userName: String,
    val newPassword: String
)

/**
 * Generic success/error response.
 */
@Serializable
data class ApiResponse(
    val success: Boolean,
    val message: String? = null
)

