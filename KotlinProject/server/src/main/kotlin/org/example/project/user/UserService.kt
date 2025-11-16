package org.example.project.user


/**
    Utility class for User account services' business logic
    *   Handles e.g validation, authentication, password hashing, and coordinates between routes and repository.
 */
object UserService {

    /**
        Validating an account creation
     */
    fun verifyUserRegistration(user: User): Result<Unit> {
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

        //  TODO: require email format to contain a valid mail server and domain
        if (!user.userEmail.contains("@")) {
            return Result.failure(IllegalArgumentException("Invalid email format"))
        }

        //  Password strength validation
        verifyUserPassword(user.userPassword)

        // TODO: Hash password before storing
        // val hashedPassword = hashPassword(user.userPassword)
        // val userWithHashedPassword = user.copy(userPassword = hashedPassword)

        return Result.success(Unit)
    }

    /**
     * Update user password.
     */
    fun verifyUserPassword(newPassword: String): Result<Unit> {
        if (newPassword.length < 8) {
            return Result.failure(IllegalArgumentException("Password must be at least 8 characters"))
        }
        return Result.success(Unit)
    }

    //  TODO: implement password hashing
}