package org.example.project.userModel

import org.example.project.User

interface UserRepository {

    //  preferably make these func async (suspend) to help HTTP req's thread handling
    suspend fun allUsers(): List<User>
    suspend fun addUser(user: User)
    suspend fun updateUserPassword(userName: String?, newPassword: String)
    suspend fun deleteUserByUsername(userName: String?): Boolean
}