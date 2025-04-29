package com.example.canvastaks.data.model

import androidx.compose.ui.geometry.Offset
import com.example.canvastaks.data.model.OffsetDto

data class TaskNodeModel(
    val id: Int,
    val title: String,
    var position: OffsetDto,
    var children: List<Int> = emptyList(), // ID других задач
    val description: String = "",
    val status: Boolean = false
)

fun OffsetDto.toOffset(): Offset = Offset(x, y)
fun Offset.toDto(): OffsetDto = OffsetDto(x, y)