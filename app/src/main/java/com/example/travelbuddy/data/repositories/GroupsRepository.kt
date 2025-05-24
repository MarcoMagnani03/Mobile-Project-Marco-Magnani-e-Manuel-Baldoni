package com.example.travelbuddy.data.repositories

import com.example.travelbuddy.data.database.Group
import com.example.travelbuddy.data.database.GroupsDAO

class GroupsRepository(
    private val dao: GroupsDAO
) {
    suspend fun upsert(group: Group) = dao.upsert(group)

    suspend fun delete(group: Group) = dao.delete(group)

    suspend fun addMemberToGroup(group: Group) = dao.upsert(group)

    suspend fun removeMemberFromGroup(userEmail: String, tripId: Long) {
        val groupMember = Group(userEmail = userEmail, tripId = tripId)
        dao.delete(groupMember)
    }

    suspend fun getGroupMembersByTripId(tripId: Long): List<Group> {
        return dao.getGroupMembersByTripId(tripId)
    }

    suspend fun getGroupsByUserEmail(email: String): List<Group> {
        return dao.getGroupsByUserEmail(email)
    }

    suspend fun isUserInGroup(userEmail: String, tripId: Long): Boolean {
        return dao.getGroupMember(userEmail, tripId) != null
    }

    suspend fun getGroupMembersCount(tripId: Long): Int {
        return dao.getGroupMembersCount(tripId)
    }
}