package org.example.project.userModel

import org.example.project.User
import org.example.project.db.UserDAO
import org.example.project.db.UserTable
import org.example.project.db.suspendTransaction
import org.example.project.db.daoToModel

import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere

/*  References:
-   https://www.jetbrains.com/help/exposed/migration-guide-1-0-0.html#updated-imports
-   https://ktor.io/docs/server-integrate-database.html#create-new-repo
-   Kotlin Exposed docs -> Deep Dive into DAO -> CRUD operations
 */


class PostgresUserRepository: UserRepository {
    override suspend fun allUsers(): List<User> = suspendTransaction {
        UserDAO.all().map(::daoToModel)
    }

    override suspend fun addUser(user: User): Unit = suspendTransaction {
        UserDAO.new {
            userName = user.userName!!
            userEmail = user.userEmail!!
            userPassword = user.userPassword!!
        }
    }

    override suspend fun updateUserPassword(userName: String?, newPassword: String) {
        val updatedUser = UserDAO.findSingleByAndUpdate(UserTable.userName eq userName!!) {
            it.userPassword = newPassword
        }
    }

    override suspend fun deleteUserByUsername(userName: String?): Boolean = suspendTransaction {
        val userDeleted = UserTable.deleteWhere {
            UserTable.userName eq userName!!
        }
        userDeleted == 1
    }
}