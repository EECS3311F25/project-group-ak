package org.example.project.user

import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere


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

    override suspend fun getUserByName(userName: String?): User? = suspendTransaction {
        UserDAO
            .find { (UserTable.userName eq userName!!) }
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

    override suspend fun updateUserPassword(userName: String?, newPassword: String?): Result<Boolean> = suspendTransaction {

        UserService.verifyUserPassword(newPassword!!)
            .mapCatching {
                val userToUpdate = UserDAO.findSingleByAndUpdate(UserTable.userName eq userName!!) {
                    it.userPassword = newPassword
                }
                userToUpdate != null
            }
    }

    override suspend fun deleteUserByUsername(userName: String?): Result<Boolean> = runCatching {
        suspendTransaction {
            val userDeleted = UserTable.deleteWhere {
                UserTable.userName eq userName!!
            }
            userDeleted == 1
        }
    }
}