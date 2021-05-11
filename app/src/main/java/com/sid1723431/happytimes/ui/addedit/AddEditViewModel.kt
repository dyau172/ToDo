package com.sid1723431.happytimes.ui.addedit

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sid1723431.happytimes.ADD_TASK_RESULT_OK
import com.sid1723431.happytimes.EDIT_TASK_RESULT_OK
import com.sid1723431.happytimes.data.Habit
import com.sid1723431.happytimes.data.HabitDao
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditViewModel @ViewModelInject constructor(
        private val habitDao: HabitDao,
        @Assisted private val state: SavedStateHandle
) : ViewModel() {
        val habit = state.get<Habit>("habit")
    // split up habit object, separate name and importance so that it could be changed

    var habitName = state.get<String>("habitName") ?: habit?.name ?: ""
    set(value){
        field = value
        state.set("habitName", value)
    }

    var habitImportance = state.get<Boolean>("habitImportant") ?: false
        set(value){
            field = value
            state.set("habitImportant", value)
        }

    var habitStartDay = state.get<String>("habitStartDay") ?: habit?.startDay ?: ""
        set(value) {
            field = value
            state.set("habitStartDay", value)
        }
    var habitEndDay = state.get<String>("habitEndDay") ?: habit?.endDay ?: ""
        set(value) {
            field = value
            state.set("habitEndDay", value)
        }

    var habitNotes = state.get<String>("habitNotes") ?: habit?.notes ?: ""
        set(value) {
            field = value
            state.set("habitNotes", value)
        }



    private val addEditEventChannel = Channel<AddEditEvent>()
    val addEditEvent = addEditEventChannel.receiveAsFlow()

    fun onSaveClick(){
        if (habitName.isBlank()) {
        showInvalidInputMessage("Name cannot be empty")
            return
        }
        if(habitEndDay <= habitStartDay) {
            showInvalidDateMessage("End date is not valid")
            return
        }
        if (habit != null){
            val updatedHabit = habit.copy(name = habitName, important = habitImportance, startDay = habitStartDay,  endDay = habitEndDay, notes = habitNotes)
            updateHabit(updatedHabit)

        }else{
            val newHabit = Habit(name = habitName, important = habitImportance, startDay = habitStartDay,  endDay = habitEndDay, notes = habitNotes)
            Log.d("clickSave", "Something, $habitStartDay , $habitName")
            createHabit(newHabit)
        }
    }

    private fun createHabit(habit: Habit) = viewModelScope.launch {
        habitDao.insert(habit)
        addEditEventChannel.send(
            AddEditEvent.NavigateBackWithResult(
                ADD_TASK_RESULT_OK
            )
        )
    }

    private fun updateHabit(habit: Habit) = viewModelScope.launch {
        habitDao.update(habit)
        addEditEventChannel.send(
            AddEditEvent.NavigateBackWithResult(
                EDIT_TASK_RESULT_OK
            )
        )
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch{
        addEditEventChannel.send(
            AddEditEvent.ShowInvalidInputMessage(
                text
            )
        )
    }

    private fun showInvalidDateMessage(text: String) = viewModelScope.launch {
        addEditEventChannel.send(
            AddEditEvent.ShowInvalidDateMessage(
                text
            )
        )
    }

    sealed class AddEditEvent{
        data class ShowInvalidInputMessage(val msg: String) : AddEditEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditEvent()
        data class ShowInvalidDateMessage(val msg: String) : AddEditEvent()

    }





}