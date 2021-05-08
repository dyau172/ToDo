package com.sid1723431.happytimes.ui.Home

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.sid1723431.happytimes.ADD_TASK_RESULT_OK
import com.sid1723431.happytimes.EDIT_TASK_RESULT_OK
import com.sid1723431.happytimes.PreferenceManager
import com.sid1723431.happytimes.SortOrder
import com.sid1723431.happytimes.data.Habit
import com.sid1723431.happytimes.data.HabitDao
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject constructor(
    private val habitDao: HabitDao,
    private val preferenceManager: PreferenceManager,
    @Assisted private val state: SavedStateHandle
) : ViewModel(){

    //val searchQuery = MutableStateFlow("")


    lateinit var mAuth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient


    val searchQuery = state.getLiveData("searchQuery", "")

    val preferencesFlow = preferenceManager.preferencesFlow

    private val taskEventChannel = Channel<TaskEvent>()
    val taskEvent = taskEventChannel.receiveAsFlow()

    private val habitFlow = combine(
            searchQuery.asFlow(),
            preferencesFlow
    ){ query, filterPreferences ->
        Pair(query, filterPreferences)
    }

            .flatMapLatest {(query, filterPreferences)->
        habitDao.getTasks(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }

    val habits = habitFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferenceManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedClick(hideCompleted: Boolean) = viewModelScope.launch {
        preferenceManager.updateHideCompleted(hideCompleted)
    }

    fun onHabitSelected(habit: Habit) = viewModelScope.launch{
        taskEventChannel.send(
            TaskEvent.NavigateToEditHabitScreen(
                habit
            )
        )
    }

    fun onHabitCheckedChanged(habit: Habit, isChecked: Boolean) = viewModelScope.launch {
        habitDao.update(habit.copy(completed = isChecked))
    }
    fun onTaskSwiped(habit: Habit) = viewModelScope.launch {
        habitDao.delete(habit)
        taskEventChannel.send(
            TaskEvent.ShowUndoDeleteTaskManager(
                habit
            )
        )
    }

    fun onUndoDeleteClick(habit: Habit) = viewModelScope.launch{
        habitDao.insert(habit)
    }

    fun onAddNewHabitClick() = viewModelScope.launch {
        taskEventChannel.send(TaskEvent.NavigateToAddHabitScreen)
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Habit added")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Edit updated")
        }
    }

    private fun showTaskSavedConfirmationMessage(text: String) = viewModelScope.launch {
        taskEventChannel.send(
            TaskEvent.ShowTaskSavedConfirmation(
                text
            )
        )
    }

    fun onDeleteAllCompletedClick() = viewModelScope.launch {
        taskEventChannel.send(TaskEvent.NavigateToDeleteAllCompletedScreen)
    }

    fun logoutClick(){
        if(mGoogleSignInClient != null ){
            mGoogleSignInClient.signOut()
        }else
          mAuth.signOut()



    }


    sealed class TaskEvent{
        object NavigateToAddHabitScreen: TaskEvent()

        data class NavigateToEditHabitScreen(val habit: Habit) : TaskEvent()
        data class ShowUndoDeleteTaskManager(val habit: Habit) : TaskEvent()
        data class ShowTaskSavedConfirmation(val msg: String) : TaskEvent()
        object NavigateToDeleteAllCompletedScreen : TaskEvent()
    }

}

