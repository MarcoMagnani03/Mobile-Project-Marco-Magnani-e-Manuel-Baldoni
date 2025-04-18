package com.example.travelbuddy.ui.screens.signup

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelbuddy.data.repositories.UsersRepository
import com.example.travelbuddy.utils.ImageUtils
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
    val showImagePreview: Boolean = false
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
    fun showImagePicker(value: Boolean)
    fun showPermissionRationale(value: Boolean)
    fun showCameraPermissionDeniedAlert(value : Boolean)
    fun showCameraScreen(value: Boolean)
    fun setPreviewImageUri(value: String)
    fun showImagePreview(value: Boolean)
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

        override fun showCameraScreen(value: Boolean) {
            _state.update { it.copy(showCameraScreen = value) }
        }

        override fun setPreviewImageUri(value:String){
            _state.update { it.copy(previewImageUri = value) }
        }

        override fun showImagePreview(value: Boolean) {
            _state.update { it.copy(showImagePreview = value) }
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
