package com.example.travelbuddy.ui.screens.signup

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.travelbuddy.data.repositories.UsersRepository
import com.example.travelbuddy.ui.TravelBuddyRoute
import com.example.travelbuddy.utils.MultiplePermissionHandler
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
    val picture: ByteArray? = null,
    val profileImageUri: String ="",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showImagePicker: Boolean = false,
    val permissionRationaleVisible: Boolean = false,
    val permissionCameraDeniedVisible: Boolean = false,
    val showCameraScreen: Boolean =false,
    val previewImageUri: String? = null,
    val showImagePreview: Boolean = false,
    val confirmPassword: String = "",
) {
    val canSubmit: Boolean
        get() = firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                email.isNotBlank() &&
                password.isNotBlank() &&
                confirmPassword.isNotBlank() &&
                password == confirmPassword
}

interface SignUpActions {
    fun setFirstName(value: String)
    fun setLastName(value: String)
    fun setCity(value: String)
    fun setPhoneNumber(value: String)
    fun setBio(value: String)
    fun setEmail(value: String)
    fun setPassword(value: String)
    fun setPicture(value: ByteArray)
    fun setPictureUri(value: String)
    fun signUp(navController: NavController)
    fun showImagePicker(value: Boolean)
    fun showPermissionRationale(value: Boolean)
    fun showCameraPermissionDeniedAlert(value : Boolean)
    fun showCameraScreen(value: Boolean)
    fun setPreviewImageUri(value: String)
    fun showImagePreview(value: Boolean)
    fun setConfirmPassword(value: String)
}

class SignUpViewModel(private val userRepository: UsersRepository
) : ViewModel() {
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
            _state.update {
                it.copy(
                    email = value,
                    errorMessage = if (it.errorMessage == "Email already registered") null else it.errorMessage
                )
            }
        }

        override fun setPassword(value: String) {
            _state.update { it.copy(password = value) }
        }

        override fun setPicture(value: ByteArray) {
            _state.update { it.copy(picture = value) }
        }

        override fun setPictureUri(value: String) {
            _state.update { it.copy(profileImageUri = value) }
        }

        override fun showCameraScreen(value: Boolean) {
            _state.update { it.copy(showCameraScreen = value) }
        }

        override fun setPreviewImageUri(value:String){
            _state.update { it.copy(previewImageUri = value) }
        }

        override fun showImagePreview(value: Boolean) {
            _state.update { it.copy(showImagePreview = value) }
        }

        override fun setConfirmPassword(value: String) {
            _state.update {
                val updated = it.copy(confirmPassword = value)
                updated.copy(errorMessage = null)
            }
        }

        override fun signUp(navController: NavController) {
            viewModelScope.launch {
                val current = _state.value

                if (current.password != current.confirmPassword) {
                    _state.update {
                        it.copy(errorMessage = "The passwords do not match")
                    }
                    return@launch
                }

                _state.update { it.copy(isLoading = true, errorMessage = null) }

                try {
                    val existingUser = userRepository.getUserByEmail(current.email)
                    if (existingUser != null) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Email already registered"
                            )
                        }
                        return@launch
                    }

                    userRepository.registerUser(
                        current.email,
                        current.password,
                        current.firstName,
                        current.lastName,
                        current.phoneNumber,
                        current.city,
                        current.bio,
                        current.picture
                    )

                    _state.update { it.copy(isLoading = false) }

                    navController.navigate(TravelBuddyRoute.Login) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Sign up failed"
                        )
                    }
                }
            }
        }


        override fun showImagePicker(value: Boolean) {
            _state.update { it.copy(showImagePicker = value) }
        }

        override fun showPermissionRationale(value: Boolean) {
            _state.update { it.copy(permissionRationaleVisible = value) }
        }

        override fun showCameraPermissionDeniedAlert(value: Boolean) {
            _state.update { it.copy(permissionCameraDeniedVisible = value) }
        }
    }
}
