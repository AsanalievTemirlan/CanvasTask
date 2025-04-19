package com.example.canvastaks.data.converter

import androidx.compose.ui.geometry.Offset
import androidx.room.TypeConverter
import com.example.canvastaks.data.model.OffsetDto
import com.example.canvastaks.data.model.TaskNodeModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    private val gson = Gson()
    @TypeConverter
    fun fromOffset(offset: Offset): OffsetDto {
        return OffsetDto(offset.x, offset.y)
    }
    @TypeConverter
    fun toOffset(offsetDto: OffsetDto): Offset {
        return Offset(offsetDto.x, offsetDto.y)
    }
    @TypeConverter
    fun fromTaskNodeList(list: List<TaskNodeModel>): String {
        return gson.toJson(list)
    }
    @TypeConverter
    fun toTaskNodeList(json: String): List<TaskNodeModel> {
        return gson.fromJson(json, object : TypeToken<List<TaskNodeModel>>() {}.type)
    }
}
