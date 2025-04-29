package com.example.canvastaks.utils

import android.content.Context

class SharedPref(context: Context) {

    private val preferences = context.getSharedPreferences("shered", Context.MODE_PRIVATE)

    fun saveId(id: Long){
        preferences.edit().putLong("task_id", id).apply()
    }

    fun getId(): Long{
        return preferences.getLong("task_id", 0)
    }

}