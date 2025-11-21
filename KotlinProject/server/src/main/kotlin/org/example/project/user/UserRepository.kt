package org.example.project.user


interface UserRepository {
    suspend fun allUsers(): List<User>
    suspend fun addUser(user: User?): Result<User>
    suspend fun getUserById(userId: Int?): User?
    suspend fun updateUserPassword(userId: Int?, newPassword: String?): Result<Boolean>
    suspend fun deleteUserByUserId(userId: Int?): Result<Boolean>
}