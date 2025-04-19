package com.example.canvastaks.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import com.example.canvastaks.ui.navigation.AppNavHost
import com.example.canvastaks.ui.theme.CanvasTaskTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CanvasTaskTheme {
                AppNavHost()
            }
        }
    }

}

fun Path.drawCurvedConnection(from: Offset, to: Offset) {
    val controlPoint1 = Offset(from.x + (to.x - from.x) / 2, from.y)
    val controlPoint2 = Offset(from.x + (to.x - from.x) / 2, to.y)
    cubicTo(
        controlPoint1.x, controlPoint1.y,
        controlPoint2.x, controlPoint2.y,
        to.x, to.y
    )
}


