package com.sid1723431.happytimes.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sid1723431.happytimes.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Habit::class], version = 5)
abstract class HabitDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao

    class Callback@Inject constructor(
        private val database: Provider<HabitDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ): RoomDatabase.Callback(){

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().habitDao()

            //dao.insert()

            //GlobalScope

            applicationScope.launch {
                dao.insert(Habit("Eat Pizza", " ", " ", ""))
                dao.insert(Habit("Go for a run",  " ", " ", "", important = true))
                dao.insert(Habit("Fish for a compliment", " ", " ", "", completed = true))
            }
        }
    }
}