package com.example.travelbuddy.data.repositories

import com.example.travelbuddy.data.database.Photo
import com.example.travelbuddy.data.database.PhotosDAO

class PhotosRepository (
    private val dao: PhotosDAO
) {
    suspend fun upsert(photo: Photo) = dao.upsert(photo)
    suspend fun delete(photo: Photo) = dao.delete(photo)
}