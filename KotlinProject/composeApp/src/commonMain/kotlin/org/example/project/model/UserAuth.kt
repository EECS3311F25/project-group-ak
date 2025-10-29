package org.example.project.model

import kotlinx.serialization.Serializable
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.random.Random

@Serializable
data class UserAuth(
    val id: String = generateId(),
    val username: String,
    val email: String? = null,
    var hashedPassword: String,
    var salt: String = generateSalt(),
    val createdAt: Instant = Clock.System.now(),
    var updatedAt: Instant = createdAt,
    val roles: Set<String> = emptySet(),
    val isActive: Boolean = true
) {
    fun verifyPassword(plain: String, hash: (String) -> String): Boolean =
        hashedPassword == hash(plain + salt)

    fun setNewPassword(plain: String, hash: (String) -> String) {
        salt = generateSalt()
        hashedPassword = hash(plain + salt)
        updatedAt = Clock.System.now()
    }

    companion object {
        fun createWithPlainPassword(
            username: String,
            plain: String,
            email: String? = null,
            roles: Set<String> = emptySet(),
            hash: (String) -> String
        ): UserAuth {
            val salt = generateSalt()
            return UserAuth(
                username = username,
                email = email,
                hashedPassword = hash(plain + salt),
                salt = salt,
                roles = roles
            )
        }
    }
}

private fun generateId(): String =
    List(16) { Random.nextInt(0, 256).toString(16).padStart(2, '0') }.joinToString("")

private fun generateSalt(length: Int = 16): String =
    List(length) { Random.nextInt(0, 256).toString(16).padStart(2, '0') }.joinToString("")