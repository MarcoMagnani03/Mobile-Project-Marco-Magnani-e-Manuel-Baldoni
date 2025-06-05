package com.example.travelbuddy.ui.screens.tripPhotos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelbuddy.data.database.Photo
import com.example.travelbuddy.data.repositories.GroupsRepository
import com.example.travelbuddy.data.repositories.NotificationsRepository
import com.example.travelbuddy.data.repositories.PhotosRepository
import com.example.travelbuddy.data.repositories.UserSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TripPhotosState(
    val tripId: Long = 0,
    val photos: List<Photo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val profileImageUri: String? = null,
    val previewImageUri: String? = null,
    val showImagePicker: Boolean = false,
    val showCameraScreen: Boolean = false,
    val showImagePreview: Boolean = false,
    val permissionRationaleVisible: Boolean = false,
    val permissionCameraDeniedVisible: Boolean = false,
    val selectedPhotos: Set<Long> = emptySet(), // Photo IDs
    val isSelectionMode: Boolean = false,
    val isSavingToGallery: Boolean = false
)

interface TripPhotosActions {
    fun setTripId(tripId: Long)
    fun loadPhotos()
    fun addPhoto(photoBytes: ByteArray)
    fun deletePhoto(photo: Photo)
    fun showImagePicker(show: Boolean)
    fun showCameraScreen(show: Boolean)
    fun showImagePreview(show: Boolean)
    fun setPreviewImageUri(uri: String)
    fun showPermissionRationale(show: Boolean)
    fun showCameraPermissionDeniedAlert(show: Boolean)
    fun togglePhotoSelection(photoId: Long)
    fun toggleSelectionMode()
    fun clearSelection()
    fun selectAllPhotos()
    fun saveSelectedPhotosToGallery()
    fun deleteSelectedPhotos()
}

class TripPhotosViewModel(
    private val photoRepository: PhotosRepository,
    private val groupsRepository: GroupsRepository,
    private val userSessionRepository: UserSessionRepository,
    private val notificationsRepository: NotificationsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(TripPhotosState())
    val state = _state.asStateFlow()

    val actions = object : TripPhotosActions {
        override fun setTripId(tripId: Long) {
            _state.value = _state.value.copy(tripId = tripId)
            loadPhotos()
        }

        override fun loadPhotos() {
            viewModelScope.launch {
                try {
                    _state.value = _state.value.copy(isLoading = true)
                    val tripId = _state.value.tripId
                    if (tripId <= 0) {
                        _state.value = _state.value.copy(
                            error = "Invalid trip ID",
                            isLoading = false
                        )
                        return@launch
                    }

                    val photos = photoRepository.getPhotosByTripId(tripId)
                    _state.value = _state.value.copy(photos = photos, isLoading = false)
                } catch (e: Exception) {
                    _state.value = _state.value.copy(
                        error = "Error loading photos: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }

        override fun addPhoto(photoBytes: ByteArray) {
            viewModelScope.launch {
                try {
                    _state.value = _state.value.copy(isLoading = true)
                    val tripId = _state.value.tripId
                    if (tripId <= 0) {
                        _state.value = _state.value.copy(
                            error = "Invalid trip ID",
                            isLoading = false
                        )
                        return@launch
                    }

                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val currentDate = dateFormat.format(Date())

                    val photo = Photo(
                        file = photoBytes,
                        creationDate = currentDate,
                        tripId = tripId
                    )

                    photoRepository.upsert(photo)

                    val currentUserEmail = userSessionRepository.userEmail.first()
                    if (!currentUserEmail.isNullOrBlank()) {
                        val groupEntries = groupsRepository.getGroupMembersByTripId(tripId)
                        groupEntries.toSet().forEach { group ->
                            notificationsRepository.addInfoNotification(
                                description = "$currentUserEmail added a photo",
                                title = "New Photo",
                                userEmail = group.userEmail
                            )
                        }
                    }

                    loadPhotos()
                } catch (e: Exception) {
                    _state.value = _state.value.copy(
                        error = "Error adding photo: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }

        override fun deletePhoto(photo: Photo) {
            viewModelScope.launch {
                try {
                    _state.value = _state.value.copy(isLoading = true)
                    photoRepository.delete(photo)

                    val currentUserEmail = userSessionRepository.userEmail.first()
                    if (!currentUserEmail.isNullOrBlank()) {
                        val groupEntries = groupsRepository.getGroupMembersByTripId(_state.value.tripId)
                        groupEntries.toSet().forEach { group ->
                            notificationsRepository.addInfoNotification(
                                description = "$currentUserEmail deleted a photo",
                                title = "Deleted Photo",
                                userEmail = group.userEmail
                            )
                        }
                    }

                    loadPhotos()
                } catch (e: Exception) {
                    _state.value = _state.value.copy(
                        error = "Error deleting photo: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }

        override fun showImagePicker(show: Boolean) {
            _state.value = _state.value.copy(showImagePicker = show)
        }

        override fun showCameraScreen(show: Boolean) {
            _state.value = _state.value.copy(showCameraScreen = show)
        }

        override fun showImagePreview(show: Boolean) {
            _state.value = _state.value.copy(showImagePreview = show)
        }

        override fun setPreviewImageUri(uri: String) {
            _state.value = _state.value.copy(previewImageUri = uri)
        }

        override fun showPermissionRationale(show: Boolean) {
            _state.value = _state.value.copy(permissionRationaleVisible = show)
        }

        override fun showCameraPermissionDeniedAlert(show: Boolean) {
            _state.value = _state.value.copy(permissionCameraDeniedVisible = show)
        }

        override fun togglePhotoSelection(photoId: Long) {
            val currentSelection = _state.value.selectedPhotos
            val newSelection = if (currentSelection.contains(photoId)) {
                currentSelection - photoId
            } else {
                currentSelection + photoId
            }
            _state.value = _state.value.copy(selectedPhotos = newSelection)
        }

        override fun toggleSelectionMode() {
            val newSelectionMode = !_state.value.isSelectionMode
            _state.value = _state.value.copy(
                isSelectionMode = newSelectionMode,
                selectedPhotos = if (!newSelectionMode) emptySet() else _state.value.selectedPhotos
            )
        }

        override fun clearSelection() {
            _state.value = _state.value.copy(
                selectedPhotos = emptySet(),
                isSelectionMode = false
            )
        }

        override fun selectAllPhotos() {
            val allPhotoIds = _state.value.photos.mapNotNull { it.id }.toSet()
            _state.value = _state.value.copy(selectedPhotos = allPhotoIds)
        }

        override fun saveSelectedPhotosToGallery() {
            viewModelScope.launch {
                try {
                    _state.value = _state.value.copy(isSavingToGallery = true)
                    val selectedPhotos = _state.value.photos.filter { photo ->
                        photo.id?.let { _state.value.selectedPhotos.contains(it) } == true
                    }

                    val imageBytesList = selectedPhotos.mapNotNull { it.file }

                    // You'll need to pass context here from the UI layer
                    // For now, we'll emit a state that the UI can observe and handle the actual saving
                    // Alternatively, you can inject a repository that handles file operations

                    _state.value = _state.value.copy(
                        isSavingToGallery = false,
                        selectedPhotos = emptySet(),
                        isSelectionMode = false
                    )

                    // Emit success state - the UI will handle showing the snackbar
                } catch (e: Exception) {
                    _state.value = _state.value.copy(
                        error = "Error saving photos: ${e.message}",
                        isSavingToGallery = false
                    )
                }
            }
        }

        override fun deleteSelectedPhotos() {
            viewModelScope.launch {
                try {
                    _state.value = _state.value.copy(isLoading = true)
                    val selectedPhotos = _state.value.photos.filter { photo ->
                        photo.id?.let { _state.value.selectedPhotos.contains(it) } == true
                    }

                    selectedPhotos.forEach { photo ->
                        photoRepository.delete(photo)
                    }

                    val currentUserEmail = userSessionRepository.userEmail.first()
                    if (!currentUserEmail.isNullOrBlank() && selectedPhotos.isNotEmpty()) {
                        val groupEntries = groupsRepository.getGroupMembersByTripId(_state.value.tripId)
                        groupEntries.toSet().forEach { group ->
                            notificationsRepository.addInfoNotification(
                                description = "$currentUserEmail deleted ${selectedPhotos.size} photos",
                                title = "Deleted Photos",
                                userEmail = group.userEmail
                            )
                        }
                    }

                    _state.value = _state.value.copy(
                        selectedPhotos = emptySet(),
                        isSelectionMode = false
                    )
                    loadPhotos()
                } catch (e: Exception) {
                    _state.value = _state.value.copy(
                        error = "Error deleting photos: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
}