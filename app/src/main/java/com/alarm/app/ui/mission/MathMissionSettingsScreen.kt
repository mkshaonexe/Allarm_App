package com.alarm.app.ui.mission

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.alarm.app.data.model.Difficulty
import com.alarm.app.ui.AppViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MathMissionSettingsScreen(
    navController: NavController,
    viewModel: MissionSettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var difficulty by remember { mutableStateOf(viewModel.mathDifficulty) }
    var problemCount by remember { mutableIntStateOf(viewModel.mathProblemCount) }

    LaunchedEffect(viewModel.mathDifficulty, viewModel.mathProblemCount) {
        difficulty = viewModel.mathDifficulty
        problemCount = viewModel.mathProblemCount
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Math Mission", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(onClick = {
                        viewModel.saveMathSettings(difficulty, problemCount)
                        navController.popBackStack()
                    }) {
                        Text("Save", color = Color(0xFF6EC3F5), fontSize = 16.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Difficulty Section
            Text("Difficulty", color = Color.Gray, fontSize = 14.sp)
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DifficultyOption(
                    title = "Easy",
                    subtitle = "Simple addition (e.g. 12 + 7)",
                    isSelected = difficulty == Difficulty.EASY,
                    onClick = { difficulty = Difficulty.EASY }
                )
                DifficultyOption(
                    title = "Medium",
                    subtitle = "Addition & subtraction (e.g. 45 - 12 + 8)",
                    isSelected = difficulty == Difficulty.MEDIUM,
                    onClick = { difficulty = Difficulty.MEDIUM }
                )
                DifficultyOption(
                    title = "Hard",
                    subtitle = "Multiplication (e.g. 12 x 8 + 5)",
                    isSelected = difficulty == Difficulty.HARD,
                    onClick = { difficulty = Difficulty.HARD }
                )
                 DifficultyOption(
                    title = "Very Hard",
                    subtitle = "Complex calculations",
                    isSelected = difficulty == Difficulty.VERY_HARD,
                    onClick = { difficulty = Difficulty.VERY_HARD }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Problem Count Section
            Text("Number of Problems: $problemCount", color = Color.Gray, fontSize = 14.sp)
            
            Slider(
                value = problemCount.toFloat(),
                onValueChange = { problemCount = it.toInt() },
                valueRange = 1f..10f,
                steps = 8,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF6EC3F5),
                    activeTrackColor = Color(0xFF6EC3F5)
                )
            )
        }
    }
}

@Composable
fun DifficultyOption(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFF2C2C2E) else Color(0xFF1C1C1E))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 16.sp)
            Text(subtitle, color = Color.Gray, fontSize = 12.sp)
        }
        if (isSelected) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF6EC3F5))
        }
    }
}
