package org.example.project.user


interface UserRepository {
    suspend fun allUsers(): List<User>
    suspend fun addUser(user: User)
    suspend fun getUserByName(userName: String): User?
    suspend fun updateUserPassword(userName: String?, newPassword: String)
    suspend fun deleteUserByUsername(userName: String?): Boolean
}