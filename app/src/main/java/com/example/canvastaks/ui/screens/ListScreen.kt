package com.example.canvastaks.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.canvastaks.data.model.TaskModel
import com.example.canvastaks.ui.navigation.Route
import com.example.canvastaks.ui.viewModel.CanvasViewModel
import com.example.canvastaks.utils.SharedPref
import kotlinx.coroutines.launch

@Composable
fun ListScreen(navController: NavController){

    val viewModel: CanvasViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp) // чтобы FAB не перекрывал
        ) {
            items(viewModel.task.value) { index ->
                Text(
                    text = index.taskTitle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }

        FloatingActionButton(
            onClick = {
                coroutineScope.launch {
                    val taskId = viewModel.insert(TaskModel(tasks = mutableListOf(), taskTitle = "New task"))
                    SharedPref(context).saveId(taskId)
                    Log.e("ololo", "ListScreen: $taskId", )
                    navController.navigate(Route.MAIN)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Добавить")
        }
    }
}