package com.example.travelbuddy.ui.screens.profile

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.travelbuddy.data.repositories.UserSessionRepository
import com.example.travelbuddy.data.repositories.UsersRepository
import com.example.travelbuddy.ui.TravelBuddyRoute
import com.example.travelbuddy.ui.screens.signup.SignUpActions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.travelbuddy.utils.ImageUtils
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

data class ProfileState(
    val name: String = "",
    val email: String = "",
    val phone: String? = "",
    val city: String? = "",
    val bio: String? = "",
    val profileImage: Bitmap? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

interface ProfileActions {
    fun loadUserData()
}

class ProfileViewModel(
    private val usersRepository: UsersRepository,
    private val sessionRepository: UserSessionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    val actions = object : ProfileActions {

        override fun loadUserData() {
            viewModelScope.launch {
                _state.value = _state.value.copy(isLoading = true)
                try {
                    val email = sessionRepository.userEmail.first()
                    if (email == null) {
                        _state.update { it.copy(errorMessage = "No email in session", isLoading = false) }
                        return@launch
                    }
                    val user = usersRepository.getUserByEmail(email)
                    println("user "+ user)
                    if (user != null) {
                        println(user.profilePicture)
                        val profileBitmap = user.profilePicture?.let { ImageUtils.byteArrayToOrientedBitmap(it) }


                        _state.value = _state.value.copy(
                            name = "${user.firstname} ${user.lastname}",
                            email = user.email,
                            phone = user.phoneNumber,
                            city = user.location,
                            profileImage = profileBitmap,
                            isLoading = false
                        )
                    } else {
                        _state.value = _state.value.copy(
                            errorMessage = "User not found",
                            isLoading = false
                        )
                    }
                } catch (e: Exception) {
                    _state.value = _state.value.copy(
                        errorMessage = e.message,
                        isLoading = false
                    )
                }
            }
        }

    }
}
