package com.example.travelbuddy.utils

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Hex
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher {
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

    fun verifyPassword(plainPassword: String, hashedPassword: String, saltBase64: String): Boolean {
        val inputHashed = hashPassword(plainPassword, saltBase64)
        return inputHashed == hashedPassword
    }
}