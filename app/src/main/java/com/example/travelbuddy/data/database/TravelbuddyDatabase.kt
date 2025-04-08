package com.example.travelbuddy.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities =
        [
            Trip::class,
            User::class,
            Friendship::class,
            Activity::class,
            ActivityType::class,
            Notification::class,
            NotificationType::class,
            Group::class,
            Expense::class,
            Photo::class
        ],
    version = 1
)
abstract class TravelDiaryDatabase : RoomDatabase() {
    abstract fun tripsDAO(): TripsDAO
    abstract fun usersDAO(): UsersDAO
    abstract fun friendshipsDAO(): FriendshipDAO
    abstract fun activitiesDAO(): ActivitiesDAO
    abstract fun activitiesTypesDAO(): ActivitiesTypesDAO
    abstract fun notificationsDAO(): NotificationsDAO
    abstract fun notificationsTypesDAO(): NotificationsTypesDAO
    abstract fun groupsDAO(): GroupsDAO
    abstract fun expensesDAO(): ExpensesDAO
    abstract fun photosDAO(): PhotosDAO
}
