package com.sid1723431.happytimes.ui

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.sid1723431.happytimes.data.HabitDao
import com.sid1723431.happytimes.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope

class ProgressViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the progress Fragment"
    }
    val text: LiveData<String> = _text
}
