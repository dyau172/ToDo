package com.sid1723431.happytimes.data

import androidx.room.*
import com.sid1723431.happytimes.SortOrder
import kotlinx.coroutines.flow.Flow

@Dao

interface HabitDao {

    fun getTasks (query : String, sortOrder: SortOrder, hideCompleted: Boolean ): Flow<List<Habit>> =
        when(sortOrder){
            SortOrder.BY_DATE -> getTasksSortedByDateCreated(query, hideCompleted)
            SortOrder.BY_NAME -> getTasksSortedByName(query, hideCompleted)
        }

    @Query("SELECT * FROM habit_table WHERE(completed != :hideCompleted OR completed = 0 ) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC, name")
    fun getTasksSortedByName(searchQuery: String, hideCompleted: Boolean): Flow<List<Habit>>

    @Query("SELECT * FROM habit_table WHERE(completed != :hideCompleted OR completed = 0 ) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC, created")
    fun getTasksSortedByDateCreated(searchQuery: String, hideCompleted: Boolean): Flow<List<Habit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    //suspend - a way for function to switch to different thread
    suspend fun insert(habit: Habit)

    @Update
    suspend fun update(habit: Habit)

    @Delete
    suspend fun delete(habit: Habit)

    @Query("DELETE FROM habit_table WHERE completed = 1")
    suspend fun deletedCompletedHabits()

}