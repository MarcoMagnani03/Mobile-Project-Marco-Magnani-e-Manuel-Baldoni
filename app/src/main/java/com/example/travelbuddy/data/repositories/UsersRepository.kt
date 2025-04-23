package com.example.travelbuddy.data.repositories

import com.example.travelbuddy.data.database.User
import com.example.travelbuddy.data.database.UserWithTrips
import com.example.travelbuddy.data.database.UsersDAO
import com.example.travelbuddy.utils.PasswordHasher

class UsersRepository(
    private val dao: UsersDAO
) {

    suspend fun delete(user: User) = dao.delete(user)

    suspend fun getUserByEmail(email: String) = dao.getUserByEmail(email)

    suspend fun loginUser(email: String, password: String): Boolean {
        val user = dao.getUserByEmail(email) ?: return false
        val hashedInput = PasswordHasher.hashPassword(password, user.passwordSalt)
        return hashedInput == user.password
    }
    
    suspend fun signInWithEmailAndPassword(email: String, password: String) =
        loginUser(email, password)

    suspend fun updateUserPin(email: String, pin: String) = dao.updateUserPin(email,pin)
    suspend fun verifyPin(email: String, pin: String) = dao.verifyUserPin(email,pin)

    suspend fun registerUser(email: String,
                             plainPassword: String,
                             firstName: String,
                             lastName: String,
                             phoneNumber: String?,
                             location: String? = null,
                             bio: String? = null,
                             profilePicture: ByteArray? = null
    ) {
        val salt = PasswordHasher.generateSalt()
        val hashedPassword = PasswordHasher.hashPassword(plainPassword, salt)
        val user = User(
            email = email,
            password = hashedPassword,
            passwordSalt = salt,
            pin = null,
            firstname = firstName,
            lastname = lastName,
            phoneNumber = phoneNumber,
            location = location,
            bio = bio,
            profilePicture = profilePicture
        )
        dao.upsert(user)
    }

    suspend fun hasPin(email: String): Boolean = dao.hasPin(email)

    suspend fun getUserWithTrips(email: String): UserWithTrips? = dao.getUserWithTrips(email)

    suspend fun editUser(
        oldEmail: String,
        newEmail: String?,
        firstName: String,
        lastName: String,
        phoneNumber: String?,
        location: String?,
        bio: String?,
        profilePicture: ByteArray?
    ) {
        val existingUser = dao.getUserByEmail(oldEmail) ?: return

        val updatedUser = existingUser.copy(
            email = newEmail ?: oldEmail,
            firstname = firstName,
            lastname = lastName,
            phoneNumber = phoneNumber,
            location = location,
            bio = bio,
            profilePicture = profilePicture
        )

        if (newEmail == null) {
            dao.upsert(updatedUser)
        } else {
            dao.modifyUserAndUpdateEmail(existingUser, newEmail)
        }
    }

    suspend fun changePassword(
        email: String,
        oldPlain: String,
        newPlain: String
    ): Result<Unit> {
        val user = dao.getUserByEmail(email)
            ?: return Result.failure(Exception("User not found"))

        val validOldPassword = PasswordHasher.verifyPassword(
            plainPassword = oldPlain,
            hashedPassword = user.password,
            saltBase64 = user.passwordSalt
        )

        if (!validOldPassword) {
            return Result.failure(Exception("Old password is incorrect"))
        }

        val newSalt = PasswordHasher.generateSalt()
        val newHashedPassword = PasswordHasher.hashPassword(newPlain, newSalt)

        val updatedUser = user.copy(
            password = newHashedPassword,
            passwordSalt = newSalt
        )

        dao.upsert(updatedUser)
        return Result.success(Unit)
    }

    suspend fun setPin(
        email: String,
        oldPin: String?,
        newPin: String
    ): Result<Unit> {
        val user = dao.getUserByEmail(email)
            ?: return Result.failure(Exception("User not found"))

        if (user.pin != null && user.pin != oldPin) {
            return Result.failure(Exception("Old PIN is incorrect"))
        }

        val updatedUser = user.copy(pin = newPin)
        dao.upsert(updatedUser)

        return Result.success(Unit)
    }


}