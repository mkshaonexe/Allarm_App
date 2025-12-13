package com.aura.wake.ui.mission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import com.aura.wake.ui.AppViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypingMissionSettingsScreen(
    navController: NavController,
    viewModel: MissionSettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var sentences by remember { mutableStateOf(viewModel.typingSentences.toMutableList()) }
    var wordCount by remember { mutableIntStateOf(viewModel.typingWordCount) }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.typingSentences, viewModel.typingWordCount) {
        sentences = viewModel.typingSentences.toMutableList()
        wordCount = viewModel.typingWordCount
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Typing Mission", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(onClick = {
                        viewModel.saveTypingSettings(sentences, wordCount)
                        navController.popBackStack()
                    }) {
                        Text("Save", color = Color(0xFF6EC3F5), fontSize = 16.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF6EC3F5)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Sentence")
            }
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
            // Word Count Section
            Column {
                 Text("Minimum Word Count: $wordCount", color = Color.Gray, fontSize = 14.sp)
                Slider(
                    value = wordCount.toFloat(),
                    onValueChange = { wordCount = it.toInt() },
                    valueRange = 3f..20f,
                    steps = 16,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF6EC3F5),
                        activeTrackColor = Color(0xFF6EC3F5)
                    )
                )
            }

            Divider(color = Color.DarkGray)

            // Sentences Section
            Text("Custom Sentences", color = Color.Gray, fontSize = 14.sp)
            
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(sentences) { sentence ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1C1C1E))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            sentence, 
                            color = Color.White, 
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { sentences.remove(sentence) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            var newSentence by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add Sentence") },
                text = {
                    TextField(
                        value = newSentence,
                        onValueChange = { newSentence = it },
                        placeholder = { Text("Enter a motivating sentence") }
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newSentence.isNotBlank()) {
                                sentences.add(newSentence)
                                showAddDialog = false
                            }
                        }
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
