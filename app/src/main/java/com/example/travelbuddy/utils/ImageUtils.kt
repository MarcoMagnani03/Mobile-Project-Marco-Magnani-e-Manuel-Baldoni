package com.example.travelbuddy.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object ImageUtils {
    fun uriToByteArray(context: Context, uri: Uri): ByteArray {
        val inputStream = context.contentResolver.openInputStream(uri)
        return inputStream?.readBytes() ?: byteArrayOf()
    }

    fun saveImageToGallery(context: Context, imageBytes: ByteArray): Boolean {
        val filename = "TravelBuddy_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.jpg"

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val resolver = context.contentResolver
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        return imageUri?.let { uri ->
            resolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(imageBytes)
                outputStream.flush()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }

            Toast.makeText(context, "Image saved into gallery", Toast.LENGTH_SHORT).show()
            true
        } ?: false
    }
}