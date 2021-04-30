package com.sid1723431.happytimes.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat
import java.time.LocalDate

@Entity(tableName = "habit_table")

//Send data between fragments
@Parcelize
data class Habit(
    val name: String,
    val startDay: String,
    val endDay: String,
    val notes: String,
    val important: Boolean = false,
    val completed: Boolean = false,
    val created: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable {
    val createdDateFormatted: String
        get() = DateFormat.getDateTimeInstance().format(created)
}