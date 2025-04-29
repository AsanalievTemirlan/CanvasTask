package com.example.canvastaks.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.canvastaks.data.model.TaskModel
import com.example.canvastaks.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CanvasViewModel @Inject constructor(private val repository: TaskRepository) : ViewModel() {

    private var _tasks = MutableStateFlow<List<TaskModel>>(emptyList())
    val task = _tasks.asStateFlow()

    init {
        getAll()
    }

    suspend fun insert(model: TaskModel): Long {
        return repository.addTask(model)
    }

    suspend fun update(model: TaskModel) {
        repository.updateTask(model)
    }

    suspend fun delete(model: TaskModel) {
        repository.deleteTask(model)
    }

    private fun getAll() {
        viewModelScope.launch {
            repository.getAll().collect { list ->
                _tasks.value = list
            }
        }
    }

    fun getById(id: Long): TaskModel = repository.getTaskById(id) ?: TaskModel(200, mutableListOf(), "dw");

}