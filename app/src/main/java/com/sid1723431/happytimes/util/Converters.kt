package com.sid1723431.happytimes.util

import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun toDate(value: String?): Date?{
        return if (value == null) null else Date(value)
    }
}