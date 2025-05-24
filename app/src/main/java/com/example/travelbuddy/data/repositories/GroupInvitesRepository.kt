package com.example.travelbuddy.data.repositories

import com.example.travelbuddy.data.database.GroupInvitation
import com.example.travelbuddy.data.database.GroupInvitationDAO
import com.example.travelbuddy.data.database.GroupsDAO
import androidx.room.RoomDatabase
import com.example.travelbuddy.data.database.InvitationWithTripName

class GroupInvitesRepository(
    private val invitationDao: GroupInvitationDAO,
    private val groupDao: GroupsDAO
) {

    suspend fun getPendingInvitesForUser(email: String): List<InvitationWithTripName> {
        return invitationDao.getPendingInvitesForUser(email)
    }

    suspend fun acceptGroupInvite(receiverEmail: String, tripId: Long) {
        invitationDao.acceptInviteTransaction(receiverEmail, tripId, groupDao)
    }

    suspend fun declineGroupInvite(receiverEmail: String, tripId: Long) {
        invitationDao.deleteInvitationByEmailAndTripId(receiverEmail, tripId)
    }

    suspend fun sendGroupInvite(senderEmail: String, receiverEmail: String, tripId: Long) {
        val invitation = GroupInvitation(
            senderEmail = senderEmail,
            receiverEmail = receiverEmail,
            tripId = tripId
        )
        invitationDao.upsert(invitation)
    }

    suspend fun getInviteByEmailAndTripId(email: String, tripId: Long): GroupInvitation? {
        return invitationDao.getInviteByEmailAndTripId(email, tripId)
    }

    suspend fun getInvitesSentByUser(senderEmail: String): List<GroupInvitation> {
        return invitationDao.getInvitesSentByUser(senderEmail)
    }
}