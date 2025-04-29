package com.example.canvastaks.ui.screens

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.canvastaks.data.model.TaskNodeModel
import com.example.canvastaks.data.model.toDto
import com.example.canvastaks.data.model.toOffset
import com.example.canvastaks.ui.activity.drawCurvedConnection
import com.example.canvastaks.ui.theme.Purple
import com.example.canvastaks.ui.viewModel.CanvasViewModel
import com.example.canvastaks.utils.CanvasButton
import com.example.canvastaks.utils.SharedPref
import com.example.canvastaks.utils.screenOffset

@Composable
fun TaskCanvas(navController: NavController) {

    val context = LocalContext.current
    val idTask = SharedPref(context).getId()
    val viewModel: CanvasViewModel = hiltViewModel()

    val nodes = viewModel.getById(idTask)

    Log.e("ololo", "TaskCanvas: $nodes")

    val minScale = 0.3f
    val maxScale = 3f

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(minScale, maxScale)
        offset += panChange
    }

    val cardWidthPx = with(LocalDensity.current) { 125.dp.toPx() }
    val cardHeightPx = with(LocalDensity.current) { 100.dp.toPx() }

    var linkingFromId by remember { mutableStateOf<Int?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray)
            .transformable(state = transformableState)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp)
                .padding(horizontal = 30.dp),
            colors = CardDefaults.cardColors(contentColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp),
        ) {
            Box {
                Text(text = "Заголовок", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Описание карточки. Здесь можно написать что угодно.")
            }
        }
        // Внутренний контейнер, масштабируемый
        Box(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                }
        ) {
            // Canvas с сеткой и связями
            Canvas(modifier = Modifier.fillMaxSize()) {
                val step = 100f
                val gridRange = -5000..5000

                for (x in gridRange step step.toInt()) {
                    drawLine(
                        color = Color.White,
                        start = Offset(x.toFloat(), gridRange.first.toFloat()),
                        end = Offset(x.toFloat(), gridRange.last.toFloat()),
                        strokeWidth = 1f
                    )
                }

                for (y in gridRange step step.toInt()) {
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(gridRange.first.toFloat(), y.toFloat()),
                        end = Offset(gridRange.last.toFloat(), y.toFloat()),
                        strokeWidth = 1f
                    )
                }

                // Связи между узлами
                nodes.tasks.forEach { node ->
                    node.children.forEach { childId ->
                        val fromNode = node
                        val toNode = nodes.tasks.find { it.id == childId }

                        if (toNode != null) {
                            val from =
                                fromNode.position.toOffset() + Offset(
                                    cardWidthPx / 2f,
                                    cardHeightPx / 2f
                                )
                            val to =
                                toNode.position.toOffset() + Offset(
                                    cardWidthPx / 2f,
                                    cardHeightPx / 2f
                                )

                            val path = Path().apply {
                                moveTo(from.x, from.y)
                                drawCurvedConnection(from, to)
                            }
                            drawPath(path, Purple, style = Stroke(width = 4f))
                        }
                    }
                }
            }

            // Карточки
            nodes.tasks.forEachIndexed { index, node ->
                var position by remember { mutableStateOf(node.position) }
                val isLinkingSource = linkingFromId == node.id

                val infiniteTransition = rememberInfiniteTransition()
                val shakeOffset by infiniteTransition.animateFloat(
                    initialValue = -3f,
                    targetValue = 3f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(150, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )

                val animatedOffsetX = if (isLinkingSource) shakeOffset else 0f

                val borderColor = if (isLinkingSource) Color.Red else Purple

                Box(
                    Modifier
                        .offset {
                            IntOffset(
                                (position.x + animatedOffsetX).toInt(),
                                position.y.toInt()
                            )
                        }
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(2.dp, borderColor, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                // Обновляем позицию узла
                                val currentNode = nodes.tasks[index]
                                val newOffset = currentNode.position.toOffset() + dragAmount
                                nodes.tasks[index] = currentNode.copy(
                                    position = newOffset.toDto()
                                )
                            }
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    linkingFromId = node.id
                                },
                                onTap = {
                                    if (linkingFromId != null && linkingFromId != node.id) {
                                        val fromIndex =
                                            nodes.tasks.indexOfFirst { it.id == linkingFromId }
                                        if (fromIndex != -1) {
                                            val fromNode = nodes.tasks[fromIndex]
                                            val isLinked = node.id in fromNode.children

                                            val updated = fromNode.copy(
                                                children = if (isLinked)
                                                    fromNode.children - node.id // ❌ отвязка
                                                else
                                                    fromNode.children + node.id // ✅ привязка
                                            )
                                            nodes.tasks[fromIndex] = updated
                                        }
                                        linkingFromId = null
                                    } else if (linkingFromId == node.id) {
                                        linkingFromId = null // отмена режима связывания
                                    }
                                }
                            )
                        }
                ) {
                    Text(node.title)
                }
            }
        }

        // Кнопки масштабирования
        CanvasButton(
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.CenterEnd),
            plusButton = {
                scale = (scale * 1.1f).coerceAtMost(maxScale)
                         },
            minusButton = {scale = (scale * 0.9f).coerceAtLeast(minScale)},
            zeroButton = {scale = 1f
                offset = Offset.Zero},
            addButton = {
                nodes.tasks.add(
                    TaskNodeModel(
                        nodes.tasks.size + 1,
                        "Сварить",
                        screenOffset(context)
                    )
                )
            }
        )
    }
}

