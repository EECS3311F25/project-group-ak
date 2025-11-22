package org.example.project.user

import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import java.util.NoSuchElementException


/**
    References:
    -   https://www.jetbrains.com/help/exposed/migration-guide-1-0-0.html#updated-imports
    -   https://ktor.io/docs/server-integrate-database.html#create-new-repo
    -   Kotlin Exposed docs -> Deep Dive into DAO -> CRUD operations
 */

class PostgresUserRepository: UserRepository {

    override suspend fun allUsers(): List<UserResponseDto> = suspendTransaction {
        UserDAO.all().map { it.toResponseDto() }
    }

    override suspend fun getUserById(userId: Int?): UserResponseDto? = suspendTransaction {
        userId ?: return@suspendTransaction null

        UserDAO.find { UserTable.id eq userId }
            .limit(1)
            .map { it.toResponseDto() }
            .firstOrNull()
    }

    // 3️⃣ Add user, input as UserCreateDto, output as UserResponseDto
    override suspend fun addUser(userDto: UserCreateDto): Result<UserResponseDto> = suspendTransaction {
        UserService.verifyUserRegistration(userDto)
            .mapCatching {
                val newUser = UserDAO.new {
                    userName = userDto.userName
                    userEmail = userDto.userEmail
                    userPassword = userDto.userPassword
                }
                newUser.toResponseDto()
            }
    }

    override suspend fun updateUserPassword(userId: Int?, newPassword: String?): Result<Boolean> = suspendTransaction {

        UserService.verifyUserPassword(newPassword!!)
            .mapCatching {
                val userToUpdate = UserDAO.findSingleByAndUpdate(UserTable.id eq userId!!) {
                    it.userPassword = newPassword
                }
                userToUpdate != null
            }
    }

    override suspend fun deleteUserById(userId: Int?): Result<Boolean> = suspendTransaction {
        val userDeleted = UserTable.deleteWhere {
            UserTable.id eq userId!!
        }
        if (userDeleted != 1) {
            Result.failure<Boolean>(NoSuchElementException("User not found)"))
        }
        else {
            Result.success(true)
        }
    }
}