package org.example.project.user


interface UserRepository {
    suspend fun allUsers(): List<UserResponseDto>
    suspend fun addUser(userDto: UserCreateDto): Result<UserResponseDto>
    suspend fun getUserById(userId: Int?): UserResponseDto?
    suspend fun updateUserPassword(userId: Int?, newPassword: String?): Result<Boolean>
    suspend fun deleteUserById(userId: Int?): Result<Boolean>
}