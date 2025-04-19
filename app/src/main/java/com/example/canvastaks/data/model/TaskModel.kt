package com.example.canvastaks.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class TaskModel (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val tasks: List<TaskNodeModel>,
    val taskTitle: String
)