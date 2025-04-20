package com.example.travelbuddy.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.travelbuddy.data.database.User
import com.example.travelbuddy.data.repositories.UserSessionRepository
import com.example.travelbuddy.data.repositories.UsersRepository
import com.example.travelbuddy.ui.TravelBuddyRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditProfileState(
    val firstName: String = "",
    val lastName: String = "",
    val city: String = "",
    val phoneNumber: String = "",
    val bio: String = "",
    val email: String = "",
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
) {
    val canSubmit: Boolean
        get() = firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                email.isNotBlank()
}

interface EditProfileActions {
    fun setFirstName(value: String)
    fun setLastName(value: String)
    fun setCity(value: String)
    fun setPhoneNumber(value: String)
    fun setBio(value: String)
    fun setEmail(value: String)
    fun setPicture(value: ByteArray)
    fun setPictureUri(value: String)
    fun editUser()
    fun showImagePicker(value: Boolean)
    fun showPermissionRationale(value: Boolean)
    fun showCameraPermissionDeniedAlert(value : Boolean)
    fun showCameraScreen(value: Boolean)
    fun setPreviewImageUri(value: String)
    fun showImagePreview(value: Boolean)
}

class EditProfileViewModel(private val userSessionRepository: UserSessionRepository, private val userRepository: UsersRepository) : ViewModel() {
    private val _state = MutableStateFlow(EditProfileState())
    val state = _state.asStateFlow()

    val actions = object : EditProfileActions {
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

        override fun editUser() {
            viewModelScope.launch {
                val current = _state.value

                _state.update { it.copy(isLoading = true, errorMessage = null) }

                try {
                    val currentEmail = userSessionRepository.userEmail.first()
                    if(currentEmail == current.email){

                        userRepository.editUser(
                            currentEmail,
                            null,
                            current.firstName,
                            current.lastName,
                            current.phoneNumber,
                            current.city,
                            current.bio,
                            current.picture
                        )
                    }
                    else{
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
                        if(currentEmail != null){
                            userRepository.editUser(
                                currentEmail,
                                current.email,
                                current.firstName,
                                current.lastName,
                                current.phoneNumber,
                                current.city,
                                current.bio,
                                current.picture
                            )
                        }
                        else{
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = "Session lost, please log out"
                                )
                            }
                            return@launch
                        }

                    }

                    _state.update { it.copy(isLoading = false) }

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
