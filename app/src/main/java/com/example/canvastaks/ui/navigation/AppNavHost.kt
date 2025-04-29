package com.example.canvastaks.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.canvastaks.ui.navigation.Route.LIST
import com.example.canvastaks.ui.navigation.Route.MAIN
import com.example.canvastaks.ui.screens.ListScreen
import com.example.canvastaks.ui.screens.TaskCanvas

@Composable
fun AppNavHost() {

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = LIST) {
        composable(MAIN) { TaskCanvas(navController) }
        composable(LIST) { ListScreen(navController) }
    }
}

object Route{
    const val MAIN = "main"
    const val LIST = "list"
}