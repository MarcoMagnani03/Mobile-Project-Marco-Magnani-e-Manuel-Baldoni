package com.example.travelbuddy.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert

@Dao
interface TripsDAO {
    @Upsert
    suspend fun upsert(trip: Trip): Long

    @Delete
    suspend fun delete(item: Trip)

    @Query("SELECT * FROM Trip WHERE id = :tripId")
    suspend fun getTripById(tripId: Long): Trip?
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
interface TripActivitiesDAO {
    @Upsert
    suspend fun upsert(tripActivity: TripActivity)

    @Delete
    suspend fun delete(item: TripActivity)
}

@Dao
interface ActivitiesTypesDAO {
    @Upsert
    suspend fun upsert(activityType: ActivityType)

    @Delete
    suspend fun delete(item: ActivityType)
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

    @Query("SELECT * FROM User WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("UPDATE User SET pin = :pin WHERE email = :email")
    suspend fun updateUserPin(email: String, pin: String)

    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE email = :email AND pin IS NOT NULL)")
    suspend fun hasPin(email: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE email = :email AND pin = :pin)")
    suspend fun verifyUserPin(email: String, pin: String): Boolean

    @Transaction
    suspend fun modifyUserAndUpdateEmail(oldUser: User, newEmail: String) {
        val updatedUser = oldUser.copy(email = newEmail)
        upsert(updatedUser)
        delete(oldUser)
    }

    @Transaction
    @Query("SELECT * FROM User WHERE email = :email")
    suspend fun getUserWithTrips(email: String): UserWithTrips?
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
interface FriendshipsDAO {
    @Upsert
    suspend fun upsert(friendship: Friendship)

    @Delete
    suspend fun delete(item: Friendship)
}