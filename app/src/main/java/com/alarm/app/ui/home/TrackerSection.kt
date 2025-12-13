package com.alarm.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alarm.app.ui.theme.AccentOrange
import com.alarm.app.ui.theme.PrimaryRed
import java.util.*
import kotlin.random.Random

// Constants for pixel-perfect alignment
private val SQUARE_SIZE = 10.dp
private val GRID_SPACING = 4.dp
private val MONTH_LABEL_HEIGHT = 16.dp

@Composable
fun TrackerSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1C1C1E)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
        ) {
            // Header with Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = AccentOrange,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Consistency",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Mini Stats
                Row(verticalAlignment = Alignment.CenterVertically) {
                   StatBadge("Active", "148")
                   Spacer(modifier = Modifier.width(8.dp))
                   StatBadge("Streak", "12")
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))

            ContributionHeatmap()
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
               Text("Less", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.padding(end = 4.dp))
               Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                   HeatmapSquare(0)
                   HeatmapSquare(1)
                   HeatmapSquare(2)
                   HeatmapSquare(3)
                   HeatmapSquare(4)
               }
               Text("More", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp))
            }
        }
    }
}

@Composable
fun StatBadge(label: String, value: String) {
    Column(horizontalAlignment = Alignment.End) {
        Text(
            value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            label,
            color = Color.Gray,
            fontSize = 10.sp
        )
    }
}

@Composable
fun ContributionHeatmap() {
    // 1. Generate Dummy Data (Last 20 Weeks to fit cleanly)
    val weeksData = remember { generateHeatmapData(20) }
    
    // 2. Layout
    Row(modifier = Modifier.fillMaxWidth()) {
        // Left Column: Day Labels (Mon, Wed, Fri)
        Column(modifier = Modifier.padding(top = MONTH_LABEL_HEIGHT + GRID_SPACING)) {
            DayLabels()
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Right Column: Month Labels + Grid
        Column(modifier = Modifier.weight(1f)) {
            MonthLabels(weeksData)
            Spacer(modifier = Modifier.height(GRID_SPACING))
            HeatmapGrid(weeksData)
        }
    }
}

@Composable
fun DayLabels() {
    Column(
        verticalArrangement = Arrangement.spacedBy(GRID_SPACING)
    ) {
        // 7 Rows (Sun, Mon, Tue, Wed, Thu, Fri, Sat)
        repeat(7) { index ->
            Box(
                modifier = Modifier.height(SQUARE_SIZE),
                contentAlignment = Alignment.CenterEnd 
            ) {
                if (index == 1 || index == 3 || index == 5) {
                    val label = when(index) {
                        1 -> "Mon"
                        3 -> "Wed"
                        5 -> "Fri"
                        else -> ""
                    }
                    Text(
                        text = label, 
                        color = Color.Gray,
                        fontSize = 9.sp, 
                        lineHeight = 10.sp,
                        softWrap = false,
                        modifier = Modifier
                            .requiredHeight(12.dp)
                            .wrapContentWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun MonthLabels(weeks: List<WeekData>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(GRID_SPACING)
    ) {
        weeks.forEach { week ->
            Box(
                modifier = Modifier.width(SQUARE_SIZE)
            ) {
                if (week.isNewMonth || (weeks.indexOf(week) == 0 && week.monthName.isNotEmpty())) {
                    Text(
                        text = week.monthName,
                        color = Color.Gray,
                        fontSize = 10.sp,
                        softWrap = false,
                        modifier = Modifier.requiredWidth(40.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HeatmapGrid(weeks: List<WeekData>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(GRID_SPACING),
        modifier = Modifier.fillMaxWidth()
    ) {
        weeks.forEach { week ->
            Column(
                verticalArrangement = Arrangement.spacedBy(GRID_SPACING)
            ) {
                week.days.forEach { level ->
                    HeatmapSquare(level)
                }
            }
        }
    }
}

@Composable
fun HeatmapSquare(level: Int) { // level 0..4 (0=Empty, 4=Max)
    val color = when (level) {
        0 -> Color(0xFF2C2C2E) // Dark gray placeholder
        1 -> Color(0xFF442B2D) // Very dark red
        2 -> Color(0xFF8B3A3A) // Dark Red
        3 -> PrimaryRed       // Main Red
        4 -> AccentOrange     // Bright Orange (Max)
        else -> Color(0xFF2C2C2E)
    }

    Box(
        modifier = Modifier
            .size(SQUARE_SIZE)
            .clip(RoundedCornerShape(2.dp))
            .background(color)
    )
}

// --- Data Models & Helpers ---

data class WeekData(
    val monthName: String,
    val isNewMonth: Boolean,
    val days: List<Int> // 7 ints representing activity level 0-4
)

fun generateHeatmapData(numWeeks: Int): List<WeekData> {
    val weeks = mutableListOf<WeekData>()
    val cal = Calendar.getInstance()
    
    // Go back 'numWeeks'
    cal.add(Calendar.WEEK_OF_YEAR, -numWeeks)
    
    var lastMonth = -1

    repeat(numWeeks) {
        val days = mutableListOf<Int>()
        repeat(7) {
            // Random activity
            val r = Random.nextFloat()
            val level = if (r > 0.6) Random.nextInt(1, 5) else 0
            days.add(level)
        }
        
        val currentMonth = cal.get(Calendar.MONTH)
        val monthName = if (currentMonth != lastMonth) getMonthName(currentMonth) else ""
        val isNewMonth = currentMonth != lastMonth
        
        weeks.add(WeekData(monthName, isNewMonth, days))
        
        lastMonth = currentMonth
        cal.add(Calendar.WEEK_OF_YEAR, 1)
    }
    return weeks
}

fun getMonthName(month: Int): String {
    return when(month) {
        Calendar.JANUARY -> "Jan"
        Calendar.FEBRUARY -> "Feb"
        Calendar.MARCH -> "Mar"
        Calendar.APRIL -> "Apr"
        Calendar.MAY -> "May"
        Calendar.JUNE -> "Jun"
        Calendar.JULY -> "Jul"
        Calendar.AUGUST -> "Aug"
        Calendar.SEPTEMBER -> "Sep"
        Calendar.OCTOBER -> "Oct"
        Calendar.NOVEMBER -> "Nov"
        Calendar.DECEMBER -> "Dec"
        else -> ""
    }
}
