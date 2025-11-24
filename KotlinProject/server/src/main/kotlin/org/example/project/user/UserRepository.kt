package org.example.project.user


interface UserRepository {
    suspend fun allUsers(): List<UserResponse>
    suspend fun addUser(userDto: UserCreateRequest): Result<UserResponse>
    suspend fun getUserById(userId: Int?): UserResponse?
    suspend fun updateUserPassword(userId: Int?, newPassword: String?): Result<Boolean>
    suspend fun deleteUserById(userId: Int?): Result<Boolean>
}