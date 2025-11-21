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

    override suspend fun allUsers(): List<User> = suspendTransaction {
        UserDAO.all().map(::daoToUserModel)
    }

    override suspend fun getUserById(userId: Int?): User? = suspendTransaction {
        UserDAO
            .find { (UserTable.id eq userId!!) }
            .limit(1)
            .map(::daoToUserModel)
            .firstOrNull()
    }

    override suspend fun addUser(user: User?): Result<User> = suspendTransaction {
        /**
         *  check user account details' validity (e.g appropriate password strength)
         */
        UserService.verifyUserRegistration(user!!)
            .mapCatching {
                val newUser = UserDAO.new {
                    userName = user.userName!!
                    userEmail = user.userEmail!!
                    userPassword = user.userPassword!!
                }
                daoToUserModel(newUser)
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

    override suspend fun deleteUserByUserId(userId: Int?): Result<Boolean> = suspendTransaction {
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