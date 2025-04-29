package com.example.canvastaks.utils

import android.content.Context

class SharedPref(context: Context) {

    private val preferences = context.getSharedPreferences("shered", Context.MODE_PRIVATE)

    fun saveId(id: Int){
        preferences.edit().putInt("task_id", id).apply()
    }

    fun getId(): Int{
        return preferences.getInt("task_id", 0)
    }

}