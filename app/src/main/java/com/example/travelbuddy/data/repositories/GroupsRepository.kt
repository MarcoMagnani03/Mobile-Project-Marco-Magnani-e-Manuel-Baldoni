package com.example.travelbuddy.data.repositories

import com.example.travelbuddy.data.database.Group
import com.example.travelbuddy.data.database.GroupsDAO

class GroupsRepository (
    private val dao: GroupsDAO
) {
    suspend fun upsert(group: Group) = dao.upsert(group)
    suspend fun delete(group: Group) = dao.delete(group)
}