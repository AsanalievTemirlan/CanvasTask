package com.example.canvastaks.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.canvastaks.data.model.TaskNodeModel
import com.example.canvastaks.ui.activity.drawCurvedConnection
import com.example.canvastaks.ui.theme.Purple
import com.example.canvastaks.utils.CanvasButton
import com.example.canvastaks.utils.screenOffset

@Composable
fun TaskCanvas(navController: NavController) {

    val context = LocalContext.current

    val nodes = remember {
        mutableStateListOf(
            TaskNodeModel(1, "Купить мясо", Offset(360f, 200f), listOf(2, 3)),
            TaskNodeModel(2, "Сделать фарш", Offset(100f, 400f), listOf(4)),
            TaskNodeModel(3, "Сделать тесто", Offset(620f, 400f), listOf()),
            TaskNodeModel(4, "Слепить", Offset(360f, 600f))
        )
    }

    val minScale = 0.3f
    val maxScale = 3f

    var scale by remember { mutableStateOf(1f) }
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
                        color = Color.LightGray,
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
                nodes.forEach { node ->
                    node.children.forEach { childId ->
                        val fromNode = node
                        val toNode = nodes.find { it.id == childId }

                        if (toNode != null) {
                            val from =
                                fromNode.position + Offset(cardWidthPx / 2f, cardHeightPx / 2f)
                            val to =
                                toNode.position + Offset(cardWidthPx / 2f, cardHeightPx / 2f)

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
            nodes.forEachIndexed { index, node ->
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
                                position += dragAmount
                                nodes[index] = nodes[index].copy(position = position)
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
                                            nodes.indexOfFirst { it.id == linkingFromId }
                                        if (fromIndex != -1) {
                                            val fromNode = nodes[fromIndex]
                                            val isLinked = node.id in fromNode.children

                                            val updated = fromNode.copy(
                                                children = if (isLinked)
                                                    fromNode.children - node.id // ❌ отвязка
                                                else
                                                    fromNode.children + node.id // ✅ привязка
                                            )
                                            nodes[fromIndex] = updated
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
            modifier = Modifier.padding(12.dp).align(Alignment.CenterEnd),
            plusButton = {
                //navController.navigate(LIST)
                scale = (scale * 1.1f).coerceAtMost(maxScale)
                         },
            minusButton = {scale = (scale * 0.9f).coerceAtLeast(minScale)},
            zeroButton = {scale = 1f
                offset = Offset.Zero},
            addButton = {nodes.add(TaskNodeModel(5, "Сварить", screenOffset(context)))}
        )
    }
}

