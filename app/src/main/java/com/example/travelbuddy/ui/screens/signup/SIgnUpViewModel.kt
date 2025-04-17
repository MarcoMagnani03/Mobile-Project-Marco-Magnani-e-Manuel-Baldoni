package com.example.travelbuddy.ui.screens.signup

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelbuddy.data.repositories.UsersRepository
import com.example.travelbuddy.utils.CameraUtils
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
    val permissionCameraDeniedVisible: Boolean = false
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
    fun setPicture(value: ByteArray)
    fun setPictureUri(value: String)
    fun signUp()
    fun openCamera(context: Context)
    fun showImagePicker(value: Boolean)
    fun showPermissionRationale(value: Boolean)
    fun showCameraPermissionDeniedAlert(value : Boolean)
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

        override fun setPicture(value: ByteArray) {
            _state.update { it.copy(picture = value) }
        }

        override fun setPictureUri(value: String) {
            _state.update { it.copy(profileImageUri = value) }
        }

        override fun signUp() {
            viewModelScope.launch {
                _state.value = _state.value.copy(isLoading = true)
                try {
                    userRepository.getUserByEmail(state.value.email)?.let {
                        return@launch
                    }

                    userRepository.registerUser(state.value.email, state.value.password, state.value.firstName, state.value.lastName, state.value.phoneNumber, state.value.city,
                        state.value.bio, state.value.picture)

                    _state.value = _state.value.copy(
                        isLoading = false,
                    )
                } catch (e: Exception) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Sign failed"
                    )
                }
            }
        }

        override fun openCamera(context: Context) {
            viewModelScope.launch {
                try {
                    CameraUtils.takePhoto(
                        context = context,
                        onImageCaptured = { uri ->
                            _state.value = _state.value.copy(
                                profileImageUri = uri.toString(),
                                isLoading = false
                            )
                        },
                        onError = { error ->
                            _state.value = _state.value.copy(
                                errorMessage = error.message ?: "Errore fotocamera",
                                isLoading = false
                            )
                        }
                    )
                } catch (e: SecurityException) {
                    _state.value = _state.value.copy(
                        errorMessage = "Permesso fotocamera negato",
                        isLoading = false
                    )
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


    fun checkAndRequestPermissions(
        context: Context,
        permissionHandler: MultiplePermissionHandler
    ) {
        if (permissionHandler.statuses.all { it.value.isGranted }) {
            actions.showImagePicker(true)
        } else {
            permissionHandler.launchPermissionRequest()
        }
    }
}
