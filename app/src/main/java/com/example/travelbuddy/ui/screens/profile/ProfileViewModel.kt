package com.example.travelbuddy.ui.screens.profile

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.travelbuddy.data.repositories.UserSessionRepository
import com.example.travelbuddy.data.repositories.UsersRepository
import com.example.travelbuddy.ui.TravelBuddyRoute
import com.example.travelbuddy.utils.ImageUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val city: String = "",
    val bio: String = "",
    val picture: ByteArray? = null,
    val profileBitmap: Bitmap? = null,
    val profileImageUri: String ="",

    // UI controls
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showImagePicker: Boolean = false,
    val permissionRationaleVisible: Boolean = false,
    val permissionCameraDeniedVisible: Boolean = false,
    val showCameraScreen: Boolean = false,
    val previewImageUri: String? = null,
    val showImagePreview: Boolean = false
) {
    val name get() = "$firstName $lastName"
    val canSubmit: Boolean get() = firstName.isNotBlank() && lastName.isNotBlank() && email.isNotBlank()
}

interface ProfileActions {
    fun loadUserData()
    fun logout(navController: NavController)
    fun editProfile(navController: NavController)
    fun submitProfileChanges(navController: NavController)
    fun updateFieldFor(field: ProfileField): (String) -> Unit
    fun setPicture(value: ByteArray)
    fun setPictureUri(value: String)
    fun setPreviewImageUri(value: String)
    fun toggleUIControl(control: ProfileControl, value: Boolean)
}

enum class ProfileField { FirstName, LastName, Email, Phone, City, Bio }
enum class ProfileControl { ImagePicker, CameraRationale, CameraDenied, CameraScreen, ImagePreview }

class ProfileViewModel(
    private val usersRepository: UsersRepository,
    private val sessionRepository: UserSessionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    val actions = object : ProfileActions {

        override fun loadUserData() {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }
                try {
                    val email = sessionRepository.userEmail.first()
                    if(email != null){
                        val user = usersRepository.getUserByEmail(email)
                        if (user != null) {
                            _state.update {
                                it.copy(
                                    firstName = user.firstname,
                                    lastName = user.lastname,
                                    email = user.email,
                                    phoneNumber = user.phoneNumber.orEmpty(),
                                    city = user.location.orEmpty(),
                                    bio = user.bio.orEmpty(),
                                    picture = user.profilePicture,
                                    profileBitmap = user.profilePicture?.let { pic -> ImageUtils.byteArrayToOrientedBitmap(pic) },
                                    isLoading = false
                                )
                            }
                        } else {
                            _state.update { it.copy(errorMessage = "User not found", isLoading = false) }
                        }
                    }
                    else {
                        _state.update { it.copy(errorMessage = "Email is not saved", isLoading = false) }
                    }
                } catch (e: Exception) {
                    _state.update { it.copy(errorMessage = e.message, isLoading = false) }
                }
            }
        }

        override fun logout(navController: NavController) {
            viewModelScope.launch {
                sessionRepository.clearAllSessionData()
                navController.navigate(TravelBuddyRoute.Login) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }

        override fun editProfile(navController: NavController) {
            navController.navigate(TravelBuddyRoute.EditProfile)
        }

        override fun submitProfileChanges(navController: NavController) {
            viewModelScope.launch {
                val current = _state.value
                _state.update { it.copy(isLoading = true, errorMessage = null) }

                try {
                    val currentEmail = sessionRepository.userEmail.first()

                    if (currentEmail == current.email) {
                        usersRepository.editUser(
                            oldEmail = currentEmail,
                            newEmail = null,
                            firstName = current.firstName,
                            lastName = current.lastName,
                            phoneNumber = current.phoneNumber,
                            location = current.city,
                            bio = current.bio,
                            profilePicture = current.picture
                        )
                    } else {
                        val existingUser = usersRepository.getUserByEmail(current.email)
                        if (existingUser != null) {
                            _state.update { it.copy(isLoading = false, errorMessage = "Email already registered") }
                            return@launch
                        }

                        if(currentEmail != null){
                            usersRepository.editUser(
                                oldEmail = currentEmail,
                                newEmail = current.email,
                                firstName = current.firstName,
                                lastName = current.lastName,
                                phoneNumber = current.phoneNumber,
                                location = current.city,
                                bio = current.bio,
                                profilePicture = current.picture
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

                    navController.popBackStack()

                } catch (e: Exception) {
                    _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Update failed") }
                }
            }
        }

        override fun updateFieldFor(field: ProfileField): (String) -> Unit {
            return { value -> updateField(field, value) }
        }

        override fun setPicture(value: ByteArray) {
            _state.update { it.copy(picture = value) }
        }

        override fun setPreviewImageUri(value: String) {
            _state.update { it.copy(previewImageUri = value) }
        }

        override fun setPictureUri(value: String) {
            _state.update { it.copy(profileImageUri = value) }
        }

        override fun toggleUIControl(control: ProfileControl, value: Boolean) {
            _state.update {
                when (control) {
                    ProfileControl.ImagePicker -> it.copy(showImagePicker = value)
                    ProfileControl.CameraRationale -> it.copy(permissionRationaleVisible = value)
                    ProfileControl.CameraDenied -> it.copy(permissionCameraDeniedVisible = value)
                    ProfileControl.CameraScreen -> it.copy(showCameraScreen = value)
                    ProfileControl.ImagePreview -> it.copy(showImagePreview = value)
                }
            }
        }
    }

    fun updateField(field: ProfileField, value: String) {
        _state.update {
            when (field) {
                ProfileField.FirstName -> it.copy(firstName = value)
                ProfileField.LastName -> it.copy(lastName = value)
                ProfileField.Email -> it.copy(email = value, errorMessage = if (it.errorMessage == "Email already registered") null else it.errorMessage)
                ProfileField.Phone -> it.copy(phoneNumber = value)
                ProfileField.City -> it.copy(city = value)
                ProfileField.Bio -> it.copy(bio = value)
            }
        }
    }

}
