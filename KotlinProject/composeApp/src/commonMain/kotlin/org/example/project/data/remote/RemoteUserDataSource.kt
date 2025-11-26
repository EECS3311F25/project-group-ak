package org.example.project.data.remote

import io.ktor.client.request.*
import io.ktor.client.call.*
import org.example.project.data.source.UserDataSource
import org.example.project.model.dataClasses.User

class RemoteUserDataSource : UserDataSource {
    private val apiBaseUrl = "http://localhost:8080"

    // Set this when login/signup is successful
    // Mock ID = 000001 TODO: change into null
    var currentUserId: String? = "000001"
        private set

    fun setCurrentUser(userId: String) {
        currentUserId = userId
    }

    fun clearCurrentUser() {
        currentUserId = null
    }

    override suspend fun getCurrentUser(): User {
        val userId = currentUserId ?: throw IllegalStateException("No user is logged in")
        return getUserById(userId) ?: throw IllegalStateException("Current user not found")
    }

    override suspend fun getAllUsers(): List<User> {
        TODO("Implement remote getAllUsers")
    }

    override suspend fun getUserById(id: String): User? {
        return HttpClientProvider.client.get("$apiBaseUrl/user/$id").body()
    }
}
