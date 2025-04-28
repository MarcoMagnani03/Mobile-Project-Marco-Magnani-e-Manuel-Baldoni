package com.example.travelbuddy.data.repositories

import com.example.travelbuddy.data.database.FriendRequest
import com.example.travelbuddy.data.database.FriendRequestsDAO
import com.example.travelbuddy.data.database.Friendship
import com.example.travelbuddy.data.database.FriendshipsDAO

class FriendRequestsRepository(
    private val friendRequestsDao: FriendRequestsDAO,
    private val friendshipsDao: FriendshipsDAO
) {
    suspend fun acceptFriendRequest(senderEmail: String, receiverEmail: String) {
        val request = friendRequestsDao.getRequestByEmails(senderEmail, receiverEmail)
            ?: throw Exception("Request not found")

        val (user1, user2) = listOf(request.senderEmail, request.receiverEmail).sorted()
        val friendship = Friendship(
            emailFirstUser = user1,
            emailSecondUser = user2
        )

        friendshipsDao.upsert(friendship)
        friendRequestsDao.delete(request)
    }

    suspend fun refuseFriendRequest(senderEmail: String, receiverEmail: String) {
        friendRequestsDao.refuseFriendRequest(receiverEmail, senderEmail)
    }

    suspend fun getFriendRequests(email: String): List<FriendRequest>{
        return friendRequestsDao.getFriendRequests(email)
    }

    suspend fun sendFriendRequest(senderEmail: String, receiverEmail: String) {
        val existingRequest = friendRequestsDao.getRequestByEmails(senderEmail, receiverEmail)
        if (existingRequest != null) {
            throw Exception("Friend request already exists")
        }

        val friendRequest = FriendRequest(
            senderEmail = senderEmail,
            receiverEmail = receiverEmail
        )

        friendRequestsDao.upsert(friendRequest)
    }

    suspend fun getSentFriendRequests(email: String): List<FriendRequest> {
        return friendRequestsDao.getSentFriendRequests(email)
    }
}