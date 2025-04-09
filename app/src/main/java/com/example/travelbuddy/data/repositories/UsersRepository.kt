package com.example.travelbuddy.data.repositories

import com.example.travelbuddy.data.database.User
import com.example.travelbuddy.data.database.UsersDAO

public class UsersRepository(
    private val dao: UsersDAO
) {
    suspend fun upsert(user: User) = dao.upsert(user)
    suspend fun delete(user: User) = dao.delete(user)
}