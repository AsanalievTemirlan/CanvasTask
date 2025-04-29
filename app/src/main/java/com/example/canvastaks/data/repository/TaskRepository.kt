package com.example.canvastaks.data.repository

import com.example.canvastaks.data.dao.TaskDao
import com.example.canvastaks.data.model.TaskModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskRepository @Inject constructor(private val taskDao: TaskDao) {

    suspend fun addTask(task: TaskModel): Int {
        return taskDao.addTask(task)
    }

    suspend fun updateTask(task: TaskModel) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: TaskModel) {
        taskDao.deleteTask(task)
    }

    fun getTaskById(taskId: Int): TaskModel? {
        return taskDao.getTaskById(taskId)
    }

    fun getAll(): Flow<List<TaskModel>> = taskDao.getAllTask()

}