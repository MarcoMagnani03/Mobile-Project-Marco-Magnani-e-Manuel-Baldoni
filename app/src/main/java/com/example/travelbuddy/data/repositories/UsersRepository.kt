package com.example.travelbuddy.data.repositories

import com.example.travelbuddy.data.database.User
import com.example.travelbuddy.data.database.UserWithTrips
import com.example.travelbuddy.data.database.UsersDAO
import com.example.travelbuddy.utils.ImageUtils
import com.example.travelbuddy.utils.PasswordHasher
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UsersRepository(
    private val dao: UsersDAO
) {

    suspend fun delete(user: User) = dao.delete(user)

    suspend fun getUserByEmail(email: String) = dao.getUserByEmail(email)

    suspend fun loginUser(email: String, password: String): Boolean {
        val user = dao.getUserByEmail(email) ?: return false
        var hashedInput = ""
        val passwordSalt = user.passwordSalt
        if(!passwordSalt.isNullOrBlank()){
            hashedInput = PasswordHasher.hashPassword(password, passwordSalt)
        }

        return hashedInput == user.password
    }
    
    suspend fun signInWithEmailAndPassword(email: String, password: String) =
        loginUser(email, password)

    suspend fun updateUserPin(email: String, plainPin: String) {
        val salt = PasswordHasher.generateSalt()
        val hashedPin = PasswordHasher.hashPassword(plainPin, salt)

        dao.updateUserPin(
            email = email,
            pin = hashedPin,
            pinSalt = salt
        )
    }

    suspend fun verifyPin(email: String, plainPin: String): Boolean {
        val pinData = dao.getUserPinAndSalt(email) ?: return false

        return PasswordHasher.verifyPassword(
            plainPassword = plainPin,
            hashedPassword = pinData.pin,
            saltBase64 = pinData.pinSalt
        )
    }

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

        var validOldPassword=false

        val password = user.password
        val passwordSalt = user.passwordSalt

        if (!password.isNullOrBlank() && !passwordSalt.isNullOrBlank()) {
            validOldPassword = PasswordHasher.verifyPassword(
                plainPassword = oldPlain,
                hashedPassword = password,
                saltBase64 = passwordSalt
            )
        }

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
        oldPin: String,
        newPin: String
    ): Result<Unit> {
        val user = dao.getUserByEmail(email)
            ?: return Result.failure(Exception("User not found"))

        var validOldPin =false;

        val pin = user.pin
        val pinSalt = user.pinSalt

        if (!pin.isNullOrBlank() && !pinSalt.isNullOrBlank()) {
            validOldPin = PasswordHasher.verifyPassword(
                plainPassword = oldPin,
                hashedPassword = pin,
                saltBase64 = pinSalt
            )
        }


        if (!validOldPin) {
            return Result.failure(Exception("Old pin is incorrect"))
        }

        val newSalt = PasswordHasher.generateSalt()
        val newHashedPin = PasswordHasher.hashPassword(newPin, newSalt)

        val updatedUser = user.copy(
            pin = newHashedPin,
            pinSalt = newSalt
        )
        dao.upsert(updatedUser)

        return Result.success(Unit)
    }

    suspend fun handleGoogleSignIn(account: GoogleSignInAccount): String? {
        val email = account.email ?: return null

        val user = dao.getUserByEmail(email)
        if (user == null) {
            val nameParts = account.displayName?.split(" ") ?: listOf("", "")
            val firstname = nameParts.getOrNull(0) ?: ""
            val lastname = nameParts.getOrNull(1) ?: ""

            val newUser = User(
                email = email,
                password = null,
                passwordSalt = null,
                pin = null,
                pinSalt = null,
                firstname = firstname,
                lastname = lastname,
                phoneNumber = null,
                location = null,
                bio = null,
                profilePicture = null
            )
            dao.upsert(newUser)
        }

        return email
    }

    suspend fun checkUserExists(email: String): Boolean {
        return withContext(Dispatchers.IO) {
            dao.getUserByEmail(email) != null
        }
    }
}