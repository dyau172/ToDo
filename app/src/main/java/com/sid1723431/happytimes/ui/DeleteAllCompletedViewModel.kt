package com.sid1723431.happytimes.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.sid1723431.happytimes.data.HabitDao
import com.sid1723431.happytimes.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DeleteAllCompletedViewModel @ViewModelInject constructor(
    private val habitDao : HabitDao,
            @ApplicationScope private val applicationScope: CoroutineScope
): ViewModel(){

    fun onConfirmClick() = applicationScope.launch{
        habitDao.deletedCompletedHabits()
    }
}