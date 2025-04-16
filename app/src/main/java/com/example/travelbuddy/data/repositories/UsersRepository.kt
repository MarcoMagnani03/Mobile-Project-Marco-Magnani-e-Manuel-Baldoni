package com.example.travelbuddy.data.repositories

import com.example.travelbuddy.data.database.User
import com.example.travelbuddy.data.database.UsersDAO
import com.example.travelbuddy.utils.PasswordHasher
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Hex
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class UsersRepository(
    private val dao: UsersDAO
) {
    suspend fun delete(user: User) = dao.delete(user)

    suspend fun getUserByEmail(email: String) = dao.getUserByEmail(email)

    // TODO: Spotare business logic all'interno del repository
    suspend fun signInWithEmailAndPassword(email: String, password: String) = dao.loginUser(email,password)

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

}