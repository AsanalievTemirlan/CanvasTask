package com.example.canvastaks.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.canvastaks.data.model.TaskModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM task")
    fun getAllTask(): Flow<List<TaskModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTask(model: TaskModel)

    @Delete
    suspend fun deleteTask(model: TaskModel)

    @Query("SELECT * FROM task WHERE id = :id  ")
    fun getTaskById(id: Int): TaskModel?

    @Update
    suspend fun updateTask(taskModel: TaskModel)
}