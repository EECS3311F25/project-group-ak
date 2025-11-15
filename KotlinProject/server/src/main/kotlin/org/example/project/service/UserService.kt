package org.example.project.service

import org.example.project.User
import org.example.project.userModel.UserRepository

/**
 * Service layer for User business logic.
 * Handles validation, authentication, password hashing, and coordinates between routes and repository.
 */
class UserService(private val userRepository: UserRepository) {
    
    /**
     * Get all users.
     */
    suspend fun getAllUsers(): List<User> {
        return userRepository.allUsers()
    }
    
    /**
     * Register a new user with validation.
     */
    suspend fun registerUser(user: User): Result<Unit> {
        // Validation
        if (user.userName.isNullOrBlank()) {
            return Result.failure(IllegalArgumentException("Username cannot be empty"))
        }
        if (user.userEmail.isNullOrBlank()) {
            return Result.failure(IllegalArgumentException("Email cannot be empty"))
        }
        if (user.userPassword.isNullOrBlank()) {
            return Result.failure(IllegalArgumentException("Password cannot be empty"))
        }
        
        // Email validation (basic)
        val email = user.userEmail ?: ""
        if (!email.contains("@")) {
            return Result.failure(IllegalArgumentException("Invalid email format"))
        }
        
        // Password strength validation
        val password = user.userPassword ?: ""
        if (password.length < 8) {
            return Result.failure(IllegalArgumentException("Password must be at least 8 characters"))
        }
        
        // TODO: Hash password before storing
        // val hashedPassword = hashPassword(user.userPassword)
        // val userWithHashedPassword = user.copy(userPassword = hashedPassword)
        
        return try {
            userRepository.addUser(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update user password.
     */
    suspend fun updatePassword(userName: String, newPassword: String): Result<Unit> {
        if (newPassword.length < 8) {
            return Result.failure(IllegalArgumentException("Password must be at least 8 characters"))
        }
        
        // TODO: Hash password before storing
        
        return try {
            userRepository.updateUserPassword(userName, newPassword)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete a user by username.
     */
    suspend fun deleteUser(userName: String): Result<Boolean> {
        return try {
            val deleted = userRepository.deleteUserByUsername(userName)
            if (deleted) {
                Result.success(true)
            } else {
                Result.failure(NoSuchElementException("User $userName not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // TODO: Implement password hashing
    // private fun hashPassword(password: String): String {
    //     // Use BCrypt or similar
    // }
}

