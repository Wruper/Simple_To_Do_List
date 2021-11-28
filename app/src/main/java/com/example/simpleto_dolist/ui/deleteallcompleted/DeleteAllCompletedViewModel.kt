package com.example.simpleto_dolist.ui.deleteallcompleted

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.simpleto_dolist.data.TaskDao
import com.example.simpleto_dolist.di.ApplicationScope

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
/*Delete Dialog Functions*/
class DeleteAllCompletedViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    fun onConfirmClick() = applicationScope.launch {
        taskDao.deleteCompletedTasks()
    }
}