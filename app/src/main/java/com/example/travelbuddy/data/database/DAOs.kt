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
    suspend fun delete(trip: Trip)

    @Query("SELECT * FROM Trip WHERE id = :tripId")
    suspend fun getTripById(tripId: Long): Trip?

    @Query("SELECT * FROM Trip")
    suspend fun getAllTrips(): List<Trip>

    @Transaction
    @Query("SELECT * FROM Trip WHERE id = :tripId")
    suspend fun getTripWithActivitiesById(tripId: Long): TripWithTripActivities?

    @Transaction
    @Query("SELECT * FROM Trip WHERE id = :tripId")
    suspend fun getTripWithActivitiesAndExpensesById(tripId: Long): TripWithTripActivitiesAndExpenses?

    @Transaction
    @Query("SELECT * FROM Trip WHERE id = :tripId")
    suspend fun getTripWithAllRelationsById(tripId: Long): TripWithActivitiesAndExpensesAndPhotosAndUsers?

    @Transaction
    @Query("SELECT * FROM Trip")
    suspend fun getAllTripsWithAllRelations(): List<TripWithActivitiesAndExpensesAndPhotosAndUsers>

    @Transaction
    @Query("""
        SELECT t.* FROM Trip t 
        INNER JOIN `Group` g ON t.id = g.tripId 
        WHERE g.userEmail = :userEmail
    """)
    suspend fun getTripsForUser(userEmail: String): List<Trip>

    @Transaction
    @Query("""
        SELECT t.* FROM Trip t 
        INNER JOIN `Group` g ON t.id = g.tripId 
        WHERE g.userEmail = :userEmail
    """)
    suspend fun getTripsWithAllRelationsForUser(userEmail: String): List<TripWithActivitiesAndExpensesAndPhotosAndUsers>
}

@Dao
interface NotificationsDAO {
    @Query("SELECT * FROM Notification WHERE userEmail = :userEmail ORDER BY id DESC")
    suspend fun getNotificationsForUser(userEmail: String): List<Notification>

    @Upsert
    suspend fun updateNotification(notification: Notification)

    @Query("DELETE FROM Notification WHERE id = :notificationId")
    suspend fun deleteNotification(notificationId: Long)

    @Query("UPDATE Notification SET isRead = 1  WHERE id=:notificationId")
    suspend fun markNotificationAsRead(notificationId:Long)
}

@Dao
interface NotificationsTypesDAO {
    @Upsert
    suspend fun upsert(notificationType: NotificationType)

    @Delete
    suspend fun delete(item: NotificationType)

    @Query("SELECT * FROM NotificationType")
    suspend fun getAllNotificationTypes(): List<NotificationType>

    @Query("SELECT * FROM NotificationType WHERE id = :typeId")
    suspend fun getNotificationTypeById(typeId: Int): NotificationType
}

@Dao
interface TripActivitiesDAO {
    @Upsert
    suspend fun upsert(tripActivity: TripActivity): Long

    @Delete
    suspend fun delete(item: TripActivity)

    @Query("SELECT * FROM TripActivity WHERE id = :tripActivityId")
    suspend fun getTripActivityById(tripActivityId: Long): TripActivity?

    @Query("SELECT * FROM TripActivity WHERE tripId = :tripId AND (:name IS NULL OR name LIKE '%' || :name || '%') AND (:startDate IS NULL OR startDate >= :startDate) AND (:endDate IS NULL OR endDate <= :endDate) AND (:pricePerPerson IS NULL OR pricePerPerson <= :pricePerPerson) AND (:tripActivityTypeId IS NULL OR tripActivityTypeId = :tripActivityTypeId)")
    suspend fun getFilteredActivities(
        tripId: Long,
        name: String?,
        startDate: String?,
        endDate: String?,
        pricePerPerson: Double?,
        tripActivityTypeId: Int?
    ): List<TripActivity>
}

@Dao
interface TripActivitiesTypesDAO {
    @Upsert
    suspend fun upsert(tripActivityType: TripActivityType)

    @Delete
    suspend fun delete(item: TripActivityType)

    @Query("SELECT * FROM TripActivityType")
    suspend fun getAll(): List<TripActivityType>
}

@Dao
interface ExpensesDAO {
    @Upsert
    suspend fun upsert(expense: Expense): Long

    @Delete
    suspend fun delete(item: Expense)

    @Query("SELECT * FROM Expense WHERE id = :expenseId")
    suspend fun getExpenseById(expenseId: Long): Expense?
}


@Dao
interface UsersDAO {
    @Upsert
    suspend fun upsert(user: User)

    @Delete
    suspend fun delete(item: User)

    @Query("SELECT * FROM User WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("UPDATE User SET pin = :pin, pinSalt = :pinSalt WHERE email = :email")
    suspend fun updateUserPin(email: String, pin: String, pinSalt: String)

    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE email = :email AND pin IS NOT NULL)")
    suspend fun hasPin(email: String): Boolean

    @Query("SELECT pin, pinSalt FROM User WHERE email = :email")
    suspend fun getUserPinAndSalt(email: String): PinData?

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

    @Query("""
    SELECT * FROM User 
    WHERE email NOT IN (:excludeEmails) 
    ORDER BY RANDOM()
""")
    suspend fun getRandomUsersExcluding(excludeEmails: List<String>): List<User>
}

@Dao
interface PhotosDAO {
    @Upsert
    suspend fun upsert(photo: Photo)

    @Delete
    suspend fun delete(item: Photo)

    @Query("SELECT * FROM Photo WHERE tripId = :tripId ORDER BY creationDate DESC")
    suspend fun getPhotosByTripId(tripId: Long): List<Photo>

    @Query("SELECT * FROM Photo WHERE id = :photoId")
    suspend fun getPhotoById(photoId: Long): Photo?
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

    @Query("""
        SELECT * FROM Friendship
        WHERE emailFirstUser = :email OR emailSecondUser = :email
    """)
    suspend fun getFriendshipsByUser(email: String): List<Friendship>

    @Query("""
        DELETE FROM Friendship
        WHERE (emailFirstUser = :user1 AND emailSecondUser = :user2)
        OR (emailFirstUser = :user2 AND emailSecondUser = :user1)
    """)
    suspend fun deleteFriendshipBetween(user1: String, user2: String)

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM Friendship 
            WHERE (emailFirstUser = :user1 AND emailSecondUser = :user2) 
               OR (emailFirstUser = :user2 AND emailSecondUser = :user1)
        )
    """)
    suspend fun areFriends(user1: String, user2: String): Boolean
}

@Dao
interface FriendRequestsDAO {
    @Upsert
    suspend fun upsert(request: FriendRequest)

    @Delete
    suspend fun delete(request: FriendRequest)

    @Query("SELECT * FROM FriendRequest WHERE receiverEmail = :email")
    suspend fun getFriendRequests(email: String): List<FriendRequest>

    @Query("SELECT * FROM FriendRequest WHERE senderEmail = :senderEmail AND receiverEmail = :receiverEmail")
    suspend fun getRequestByEmails(senderEmail: String, receiverEmail: String): FriendRequest?

    @Query("DELETE FROM FriendRequest WHERE senderEmail = :senderEmail AND receiverEmail = :receiverEmail")
    suspend fun refuseFriendRequest(receiverEmail: String, senderEmail: String)

    @Query("SELECT * FROM FriendRequest WHERE senderEmail = :email")
    suspend fun getSentFriendRequests(email: String): List<FriendRequest>

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM FriendRequest 
            WHERE senderEmail = :sender AND receiverEmail = :receiver
        )
    """)
    suspend fun hasSentRequest(sender: String, receiver: String): Boolean
}
