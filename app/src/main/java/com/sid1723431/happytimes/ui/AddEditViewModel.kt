package com.sid1723431.happytimes.ui

import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.util.rangeTo
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.sid1723431.happytimes.ADD_TASK_RESULT_OK
import com.sid1723431.happytimes.EDIT_TASK_RESULT_OK
import com.sid1723431.happytimes.data.Habit
import com.sid1723431.happytimes.data.HabitDao
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*

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
        if (habit != null){
            val updatedHabit = habit.copy(name = habitName, important = habitImportance, startDay = habitStartDay,  endDay = habitEndDay, notes = habitNotes)
            updateHabit(updatedHabit)
        }else{
            val newHabit = Habit(name = habitName, important = habitImportance, startDay = habitStartDay,  endDay = habitEndDay, notes = habitNotes)
            Log.d("clickSave", "Something, $habitStartDay , $habitName")
            createHabit(newHabit)
        }
    }


    fun selectStartDate(){

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+1"))
        calendar.time = Date()
        val dateTime = "${calendar.get(Calendar.DAY_OF_MONTH)}- " +
                "${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.YEAR)}"

        val constraintsBuilder =
                CalendarConstraints.Builder()
                        .setValidator(DateValidatorPointForward.now())
          Log.i("startDates", dateTime)

    }


    private fun createHabit(habit: Habit) = viewModelScope.launch {
        habitDao.insert(habit)
        addEditEventChannel.send(AddEditEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
    }

    private fun updateHabit(habit: Habit) = viewModelScope.launch {
        habitDao.update(habit)
        addEditEventChannel.send(AddEditEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch{
        addEditEventChannel.send(AddEditEvent.ShowInvalidInputMessage(text))
    }

    sealed class AddEditEvent{
        data class ShowInvalidInputMessage(val msg: String) : AddEditEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditEvent()

        data class SelectedStartDate(val dateStart: String) : AddEditEvent()
        data class SelectedEndDate(val dateEnd: String) : AddEditEvent()
    }





}