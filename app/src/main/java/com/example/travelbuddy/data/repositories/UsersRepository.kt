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
}