package com.example.travelbuddy.data.database

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Relation
import androidx.room.Junction

@Entity
data class Trip (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo
    var name: String,

    @ColumnInfo
    var destination: String,

    @ColumnInfo
    var startDate: String,

    @ColumnInfo
    var endDate: String,

    @ColumnInfo
    var budget: Double,

    @ColumnInfo
    var description: String,
)

@Entity
data class User (
    @PrimaryKey
    val email: String,

    @ColumnInfo
    var password: String,

    @ColumnInfo
    var passwordSalt: String,

    @ColumnInfo
    var pin: Int,

    @ColumnInfo
    var firstname: String,

    @ColumnInfo
    var lastname: String,

    @ColumnInfo
    var phoneNumber: String?,

    @ColumnInfo
    var location: String?,

    @ColumnInfo
    var bio: String?,

    @ColumnInfo
    var profilePicture: ByteArray?
)

@Entity
data class NotificationType(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo
    var label: String,

    @ColumnInfo
    var icon: ByteArray?
)

@Entity
data class Notification(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo
    var title: String,

    @ColumnInfo
    var description: String,

    @ColumnInfo
    var notificationTypeId: Int,

    @ColumnInfo
    var userEmail: String,
)

data class UserWithNotification(
    @Embedded val user: User,
    @Relation(
        parentColumn = "email",
        entityColumn = "userEmail"
    )
    val notifications: List<Notification>
)

@Entity
data class Photo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo
    var file: ByteArray?,

    @ColumnInfo
    var creationDate: String,

    @ColumnInfo
    var tripId: Long
)

data class TripWithPhotos(
    @Embedded val trip: Trip,
    @Relation(
        parentColumn = "id",
        entityColumn = "tripId"
    )
    val photos: List<Photo>
)

@Entity
data class ActivityType(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo
    var label: String,

    @ColumnInfo
    var icon: ByteArray?
)

@Entity
data class Activity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo
    var name: String,

    @ColumnInfo
    var startDate: String,

    @ColumnInfo
    var endDate: String,

    @ColumnInfo
    var pricePerPerson: Double?,

    @ColumnInfo
    var position: String?,

    @ColumnInfo
    var notes: String?,

    @ColumnInfo
    var activityTypeId: Int
)

@Entity
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo
    var title: String,

    @ColumnInfo
    var amount: Double,

    @ColumnInfo
    var date: String,

    @ColumnInfo
    var description: String?,

    @ColumnInfo
    var cretedByUserEmail: String,

    @ColumnInfo
    var tripId: Long
)

data class TripWithExpenses(
    @Embedded val trip: Trip,
    @Relation(
        parentColumn = "id",
        entityColumn = "tripId"
    )
    val expenses: List<Expense>
)

@Entity(primaryKeys = ["userEmail", "tripId"])
data class Group(
    val userEmail: String,
    val tripId: Long
)

data class UserWithTrips(
    @Embedded val user: User,
    @Relation(
        parentColumn = "email",
        entityColumn = "userEmail",
        associateBy = Junction(Group::class)
    )
    val trips: List<Trip>
)
data class TripWithUsers(
    @Embedded val trip: Trip,
    @Relation(
        parentColumn = "id",
        entityColumn = "tripId",
        associateBy = Junction(Group::class)
    )
    val users: List<User>
)

@Entity(primaryKeys = ["emailFirstUser", "emailSecondUser"])
data class Friendship(
    var emailFirstUser: String,
    var emailSecondUser: String
)

data class UserWithFriends(
    @Embedded val user: User,
    @Relation(
        parentColumn = "email",
        entityColumn = "emailFirstUser",
    )
    val friends: List<User>
)
