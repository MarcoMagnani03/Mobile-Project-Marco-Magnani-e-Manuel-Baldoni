package com.example.travelbuddy.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert

@Dao
interface TripsDAO {
    @Upsert
    suspend fun upsert(trip: Trip)

    @Delete
    suspend fun delete(item: Trip)
}

@Dao
interface NotificationsDAO {
    @Upsert
    suspend fun upsert(notification: Notification)

    @Delete
    suspend fun delete(item: Notification)
}

@Dao
interface NotificationsTypesDAO {
    @Upsert
    suspend fun upsert(notificationType: NotificationType)

    @Delete
    suspend fun delete(item: NotificationType)
}

@Dao
interface ActivitiesDAO {
    @Upsert
    suspend fun upsert(activity: Activity)

    @Delete
    suspend fun delete(item: Activity)
}

@Dao
interface ActivitiesTypesDAO {
    @Upsert
    suspend fun upsert(activity: Activity)

    @Delete
    suspend fun delete(item: Activity)
}

@Dao
interface ExpensesDAO {
    @Upsert
    suspend fun upsert(expense: Expense)

    @Delete
    suspend fun delete(item: Expense)
}

@Dao
interface UsersDAO {
    @Upsert
    suspend fun upsert(user: User)

    @Delete
    suspend fun delete(item: User)
}

@Dao
interface PhotosDAO {
    @Upsert
    suspend fun upsert(photo: Photo)

    @Delete
    suspend fun delete(item: Photo)
}

@Dao
interface GroupsDAO {
    @Upsert
    suspend fun upsert(group: Group)

    @Delete
    suspend fun delete(item: Group)
}

@Dao
interface FriendshipDAO {
    @Upsert
    suspend fun upsert(friendship: Friendship)

    @Delete
    suspend fun delete(item: Friendship)
}