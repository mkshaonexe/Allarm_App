package com.alarm.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@Composable
fun TrackerSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color(0xFF0D0D0D), RoundedCornerShape(16.dp)) // Very dark background for the section
            .padding(16.dp)
    ) {
        // Header: Settings and Year Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Settings Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { }
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Settings",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Settings",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Year Tabs
            YearTabs()
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Heatmap Grid
        HeatmapGrid()
    }
}

@Composable
fun YearTabs() {
    val years = listOf("2025", "2024", "2023")
    var selectedYear by remember { mutableIntStateOf(0) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        years.forEachIndexed { index, year ->
            val isSelected = index == selectedYear
            Text(
                text = year,
                fontSize = 12.sp,
                color = if (isSelected) Color(0xFF26C6DA) else Color.Gray, // Teal for selected
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { selectedYear = index }
                    .background(
                        if (isSelected) Color(0xFF26C6DA).copy(alpha = 0.1f) else Color.Transparent
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun HeatmapGrid() {
    val days = listOf("Mon", "Wed", "Fri")
    // Mock data: 7 rows (days), ~16 columns (weeks)
    val rows = 7
    val columns = 16

    Column {
        // Month Labels (Approximate)
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
                    .height(100.dp) // Match grid height
                    .padding(end = 8.dp)
            ) {
                // Only showing Mon, Wed, Fri as per visual reference usually, 
                // but let's just show a few evenly spaced
                Text("Mon", color = Color.Gray, fontSize = 10.sp)
                Text("Wed", color = Color.Gray, fontSize = 10.sp)
                Text("Fri", color = Color.Gray, fontSize = 10.sp)
            }

            // Grid
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(rows) { rowIndex ->
                    // Only show rows for visualisation that mapped to the labels roughly if needed,
                    // but standard heatmap shows all 7 days.
                    // Visual check: The image shows approx 5-6 rows. Let's do 7 for a full week.
                    // Actually image shows 3 labeled rows but grid has dots for all.
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(columns) { colIndex ->
                            // Randomize active state for "cool" look
                            val isActive = remember { Random.nextFloat() > 0.7 } 
                            val color = if (isActive) Color(0xFF26C6DA) else Color(0xFF1E1E1E) // Teal vs Dark Grey

                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(color)
                            )
                        }
                    }
                }
            }
        }
    }
}
