package com.example.travelbuddy.data.repositories

import com.example.travelbuddy.data.database.Friendship
import com.example.travelbuddy.data.database.FriendshipsDAO

class FriendshipsRepository (
    private val dao: FriendshipsDAO
) {
    suspend fun upsert(friendship: Friendship) = dao.upsert(friendship)
    suspend fun delete(friendship: Friendship) = dao.delete(friendship)

    suspend fun getFriendshipsByUser(email: String): List<Friendship> = dao.getFriendshipsByUser(email)

    suspend fun deleteFriendshipBetween(user1: String, user2: String) {
        dao.deleteFriendshipBetween(user1,user2)
    }
}