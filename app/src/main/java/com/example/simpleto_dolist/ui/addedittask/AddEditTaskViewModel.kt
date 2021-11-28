package com.example.simpleto_dolist.ui.addedittask

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simpleto_dolist.ADD_TASK_RESULT_OK
import com.example.simpleto_dolist.EDIT_TASK_RESULT_OK
import com.example.simpleto_dolist.data.Task
import com.example.simpleto_dolist.data.TaskDao
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

// Functions for AddEditTaskFragment
class AddEditTaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    val task = state.get<Task>("task")

    var taskName = state.get<String>("taskName") ?: task?.name ?: ""
        set(value) {
            field = value
            state.set("taskName", value)
        }

    var taskReminder = state.get<Boolean>("taskReminder") ?: task?.hasReminder ?: false
        set(value) {
            field = value
            state.set("taskReminder", value)
        }

    var taskReminderTime = state.get<Long>("taskReminderTime") ?: task?.reminderCreated ?: 0
        set(value) {
            field = value
            state.set("taskReminderTime", value)
        }

    var taskIsCompleted = state.get<Boolean>("taskIsCompleted") ?: task?.completed ?: false
        set(value) {
            field = value
            state.set("taskReminderTime", value)
        }

    var taskNotification = state.get<Int>("notificationCode") ?: task?.notificationCode ?: 0
        set(value) {
            field = value
            state.set("notificationCode", value)
        }


    var taskID = task?.id ?: 0

    private val addEditTaskEventChannel = Channel<AddEditTaskEvent>()
    val addEditTaskEvent = addEditTaskEventChannel.receiveAsFlow()

    fun onSaveClick() {
        if (taskName.isBlank()) {
            showInvalidInputMessage("Name cannot be empty")
            return
        }
        if (task != null) {
            val updatedTask = task.copy(
                name = taskName,
                hasReminder = taskReminder,
                reminderCreated = taskReminderTime,
                notificationCode = taskNotification
            )
            updateTask(updatedTask)
        } else {
            val newTask = Task(
                name = taskName,
                hasReminder = taskReminder,
                reminderCreated = taskReminderTime,
                notificationCode = taskNotification
            )
            createTask(newTask)
        }
    }

    private fun createTask(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
    }

    private fun updateTask(task: Task) = viewModelScope.launch {
        taskDao.update(task)
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        addEditTaskEventChannel.send(AddEditTaskEvent.ShowInvalidInputMessage(text))
    }


    sealed class AddEditTaskEvent {
        data class ShowInvalidInputMessage(val msg: String) : AddEditTaskEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditTaskEvent()
    }


}