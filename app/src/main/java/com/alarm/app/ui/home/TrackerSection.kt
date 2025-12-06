package com.alarm.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@Composable
fun TrackerSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color(0xFF0D0D0D), RoundedCornerShape(16.dp)) // Very dark background
            .padding(16.dp)
    ) {
        // Heatmap Grid (No header/settings/years as per new request)
        HeatmapGrid()
    }
}

@Composable
fun HeatmapGrid() {
    // Mock data: 7 rows (days), ~16 columns (weeks)
    val rows = 7
    val columns = 16

    Column {
        // Month Labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp, bottom = 8.dp), // Offset for day labels
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Dec", color = Color.Gray, fontSize = 10.sp)
            Text("Jan", color = Color.Gray, fontSize = 10.sp)
            Text("Feb", color = Color.Gray, fontSize = 10.sp)
            Text("Mar", color = Color.Gray, fontSize = 10.sp)
            Text("Apr", color = Color.Gray, fontSize = 10.sp)
        }

        Row {
            // Day Labels Column
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .height(100.dp) // Match grid height roughly
                    .padding(end = 8.dp)
            ) {
                Text("Mon", color = Color.Gray, fontSize = 10.sp)
                Text("Wed", color = Color.Gray, fontSize = 10.sp)
                Text("Fri", color = Color.Gray, fontSize = 10.sp)
            }

            // Grid
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(rows) { 
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(columns) { 
                            // Randomize active state
                            val isActive = remember { Random.nextFloat() > 0.7 } 
                            val color = if (isActive) Color(0xFF26C6DA) else Color(0xFF1E1E1E) // Teal vs Dark Grey

                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(RoundedCornerShape(3.dp)) // Slightly rounder squares
                                    .background(color)
                            )
                        }
                    }
                }
            }
        }
    }
}
