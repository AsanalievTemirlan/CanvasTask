package com.example.canvastaks.data.di

import android.app.Application
import androidx.room.Room
import com.example.canvastaks.data.dao.TaskDao
import com.example.canvastaks.data.db.TaskDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(application: Application): TaskDataBase {
        return Room.databaseBuilder(application, TaskDataBase::class.java, "task_database")
            .allowMainThreadQueries()
            .build()
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: TaskDataBase): TaskDao {
        return database.taskDao()
    }
}