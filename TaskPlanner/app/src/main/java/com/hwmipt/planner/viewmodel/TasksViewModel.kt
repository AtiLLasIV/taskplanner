package com.hwmipt.planner.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hwmipt.planner.storage.TaskHelper
import com.hwmipt.planner.storage.TaskModel
import com.hwmipt.planner.storage.TaskRepository

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TasksViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TaskRepository(TaskHelper(application))
    val selectedTags = mutableSetOf<String>()
    private val _tasks = MutableLiveData<List<TaskModel>>()
    val tasks: LiveData<List<TaskModel>> get() = _tasks

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            val allTasks = withContext(Dispatchers.IO) {
                repository.getAllTasks()
            }
            _tasks.value = allTasks
        }
    }

    fun addTask(task: TaskModel) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.insertTask(task)
            }
            loadTasks()
        }
    }

    fun updateTask(task: TaskModel) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.updateTask(task)
            }
            loadTasks()
        }
    }

    fun deleteTask(task: TaskModel) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteTask(task)
            }
            loadTasks()
        }
    }

    fun markTaskDone(task: TaskModel) {
        updateTask(task.copy(isDone = true))
    }

    fun refreshTasks() {
        _tasks.value = _tasks.value
    }
}