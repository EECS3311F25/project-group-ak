package org.example.project.data.remote

import io.ktor.client.request.*
import io.ktor.client.call.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.project.data.source.UserDataSource
import org.example.project.model.dataClasses.User

// Server DTOs
@Serializable
private data class UserResponse(
    val id: Int,  // Server uses "id", not "user_id"
    @SerialName("user_name") val userName: String,
    @SerialName("user_email") val userEmail: String
)

@Serializable
private data class UserCreateRequest(
    @SerialName("user_name") val userName: String,
    @SerialName("user_email") val userEmail: String,
    @SerialName("user_password") val userPassword: String
)

@Serializable
private data class UserRetrieveResponse(
    val message: String,
    val data: UserResponse
)

@Serializable
private data class PasswordUpdateRequest(
    @SerialName("user_password") val userPassword: String
)

class RemoteUserDataSource : UserDataSource {
    private val apiBaseUrl = "http://localhost:8080"

    // Default demo user ID
    var currentUserId: Int = 1
        private set

    fun setCurrentUser(userId: Int) {
        currentUserId = userId
    }

    fun clearCurrentUser() {
        currentUserId = 1 // Reset to demo user
    }

    override suspend fun getCurrentUser(): User {
        println("RemoteUserDataSource: Getting current user with ID: $currentUserId")
        val user = getUserById(currentUserId.toString()) 
            ?: throw IllegalStateException("Current user not found with ID: $currentUserId")
        println("RemoteUserDataSource: Successfully loaded user: ${user.name} (${user.email})")
        return user
    }

    override suspend fun getAllUsers(): List<User> {
        TODO("Implement remote getAllUsers - no server endpoint available")
    }

    override suspend fun getUserById(id: String): User? {
        return try {
            println("RemoteUserDataSource: Fetching user with ID: $id from $apiBaseUrl/user/$id")
            val httpResponse = HttpClientProvider.client.get("$apiBaseUrl/user/$id")
            println("RemoteUserDataSource: HTTP Status: ${httpResponse.status}")
            println("RemoteUserDataSource: Response body type: ${httpResponse.body<String>()}")
            
            // Re-fetch to parse as JSON
            val response: UserRetrieveResponse = HttpClientProvider.client.get("$apiBaseUrl/user/$id").body()
            val user = response.data.toUser()
            println("RemoteUserDataSource: Got user response: ${user.name} (${user.email})")
            user
        } catch (e: Exception) {
            println("RemoteUserDataSource: Error fetching user: ${e.message}")
            println("RemoteUserDataSource: Error type: ${e::class.simpleName}")
            e.printStackTrace()
            null
        }
    }

    suspend fun registerUser(name: String, email: String, password: String): User? {
        return try {
            val request = UserCreateRequest(
                userName = name,
                userEmail = email,
                userPassword = password
            )
            val response: UserRetrieveResponse = HttpClientProvider.client.post("$apiBaseUrl/user/register") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
            response.data.toUser()
        } catch (e: Exception) {
            println("Error registering user: ${e.message}")
            null
        }
    }

    suspend fun updatePassword(userId: Int, newPassword: String): User? {
        return try {
            val request = PasswordUpdateRequest(userPassword = newPassword)
            val response: UserRetrieveResponse = HttpClientProvider.client.put("$apiBaseUrl/user/$userId") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
            response.data.toUser()
        } catch (e: Exception) {
            println("Error updating password: ${e.message}")
            null
        }
    }

    suspend fun deleteUser(userId: Int): Boolean {
        return try {
            HttpClientProvider.client.delete("$apiBaseUrl/user/$userId/delete")
            true
        } catch (e: Exception) {
            println("Error deleting user: ${e.message}")
            false
        }
    }

    // Helper function to convert server DTO to domain model
    private fun UserResponse.toUser(): User {
        return User(
            id = this.id,
            name = this.userName,
            email = this.userEmail,
            pfpUrl = null
        )
    }
}
