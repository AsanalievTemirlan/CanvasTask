package com.example.canvastaks.utils

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp

fun screenOffset(context: Context): Offset {
    val displayMetrics = context.resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels.toFloat()
    val screenHeight = displayMetrics.heightPixels.toFloat()

    return Offset(
        x = screenWidth / 2,
        y = screenHeight / 2
    )
}

@Composable
fun CanvasButton(
    modifier: Modifier,
    plusButton: ()  -> Unit,
    minusButton: ()  -> Unit,
    zeroButton: ()  -> Unit,
    addButton: ()  -> Unit,
){
    Column(
        modifier = modifier
    ) {
        Button(onClick = { plusButton()  }) { Text("+") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            zeroButton()
        }) {
            Text("o")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { minusButton()  }) { Text("-") }
        Spacer(modifier = Modifier.height(40.dp))
        Button(onClick = { addButton() }) { Text("+") }
    }
}