package com.example.canvastaks.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.canvastaks.data.converter.Converters
import com.example.canvastaks.data.dao.TaskDao
import com.example.canvastaks.data.model.TaskModel

@Database(entities = [TaskModel::class], version = 2)
@TypeConverters(Converters::class)
abstract class TaskDataBase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}

