package com.example.travelbuddy.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities =
        [
            Trip::class,
            User::class,
            Friendship::class,
            TripActivity::class,
            ActivityType::class,
            Notification::class,
            NotificationType::class,
            Group::class,
            Expense::class,
            Photo::class
        ],
    version = 3
)
abstract class TravelBuddyDatabase : RoomDatabase() {
    abstract fun tripsDAO(): TripsDAO
    abstract fun usersDAO(): UsersDAO
    abstract fun friendshipsDAO(): FriendshipsDAO
    abstract fun activitiesDAO(): TripActivitiesDAO
    abstract fun activitiesTypesDAO(): ActivitiesTypesDAO
    abstract fun notificationsDAO(): NotificationsDAO
    abstract fun notificationsTypesDAO(): NotificationsTypesDAO
    abstract fun groupsDAO(): GroupsDAO
    abstract fun expensesDAO(): ExpensesDAO
    abstract fun photosDAO(): PhotosDAO
}