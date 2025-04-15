package com.example.travelbuddy.ui.screens.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelbuddy.data.database.User
import com.example.travelbuddy.data.repositories.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SignUpState(
    val firstName: String = "",
    val lastName: String = "",
    val city: String = "",
    val phoneNumber: String = "",
    val bio: String = "",
    val email: String = "",
    val password: String = "",
    val pin: String = ""
) {
    val canSubmit: Boolean
        get() = firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                email.isNotBlank() &&
                password.isNotBlank()
}

interface SignUpActions {
    fun setFirstName(value: String)
    fun setLastName(value: String)
    fun setCity(value: String)
    fun setPhoneNumber(value: String)
    fun setBio(value: String)
    fun setEmail(value: String)
    fun setPassword(value: String)
    fun setPin(value: String)
}

class SignUpViewModel(private val userRepository: UsersRepository) : ViewModel() {
    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    val actions = object : SignUpActions {
        override fun setFirstName(value: String) {
            _state.update { it.copy(firstName = value) }
        }

        override fun setLastName(value: String) {
            _state.update { it.copy(lastName = value) }
        }

        override fun setCity(value: String) {
            _state.update { it.copy(city = value) }
        }

        override fun setPhoneNumber(value: String) {
            _state.update { it.copy(phoneNumber = value) }
        }

        override fun setBio(value: String) {
            _state.update { it.copy(bio = value) }
        }

        override fun setEmail(value: String) {
            _state.update { it.copy(email = value) }
        }

        override fun setPassword(value: String) {
            _state.update { it.copy(password = value) }
        }

        override fun setPin(value: String) {
            _state.update { it.copy(pin = value) }
        }
    }


    fun signUp() {
        viewModelScope.launch {
            try {
                userRepository.getUserByEmail(state.value.email)?.let {
                    // Gestione errore utente esistente
                    return@launch
                }

                val salt = UsersRepository.generateSalt()
                val hashedPassword = UsersRepository.hashPassword(state.value.password, salt)

                val user = User(
                    firstname = state.value.firstName,
                    lastname = state.value.lastName,
                    location = state.value.city,
                    phoneNumber = state.value.phoneNumber,
                    bio = state.value.bio,
                    email = state.value.email,
                    password = hashedPassword,
                    passwordSalt = salt
                )

                userRepository.createUser(user)
            } catch (e: Exception) {

            }
        }
    }

}
