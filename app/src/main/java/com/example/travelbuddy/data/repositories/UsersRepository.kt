package com.example.travelbuddy.data.repositories

import com.example.travelbuddy.data.database.User
import com.example.travelbuddy.data.database.UsersDAO
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Hex
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

public class UsersRepository(
    private val dao: UsersDAO
) {
    suspend fun createUser(user: User) = dao.upsert(user)
    suspend fun delete(user: User) = dao.delete(user)

    suspend fun getUserByEmail(email: String) = dao.getUserByEmail(email)

    suspend fun updateUserPin(email: String, pin: String) = dao.updateUserPin(email,pin)
    suspend fun verifyPin(email: String, pin: String) = dao.verifyUserPin(email,pin)

    companion object {
        private const val ITERATIONS = 10000
        private const val KEY_LENGTH = 256

        fun hashPassword(password: String, salt: String): String {
            val factory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val spec = PBEKeySpec(
                password.toCharArray(),
                salt.toByteArray(),
                ITERATIONS,
                KEY_LENGTH
            )
            return Hex.encodeHexString(factory.generateSecret(spec).encoded)
        }

        fun generateSalt(): String {
            val random = SecureRandom()
            val salt = ByteArray(16)
            random.nextBytes(salt)
            return Hex.encodeHexString(salt)
        }
    }

}