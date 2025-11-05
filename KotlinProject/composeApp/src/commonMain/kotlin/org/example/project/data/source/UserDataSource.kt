package org.example.project.data.source

import org.example.project.model.User

interface UserDataSource {
    suspend fun getCurrentUser(): User
    suspend fun getAllUsers(): List<User>
    suspend fun getUserById(id: String): User?
}