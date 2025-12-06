package com.alarm.app.ui.ring.challenges

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
fun MathChallenge(
    onCompleted: () -> Unit
) {
    val num1 = remember { Random.nextInt(10, 99) }
    val num2 = remember { Random.nextInt(10, 99) }
    val correctAnswer = num1 + num2
    
    var answer by remember { mutableStateOf("") }
    var shakeError by remember { mutableStateOf(false) } // Visual feedback hook

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1C1E))
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Handle back if needed */ }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text("1 / 3", color = Color.Gray, fontSize = 16.sp)
            IconButton(onClick = { /* Toggle Mute */ }) {
                Icon(Icons.Default.VolumeOff, contentDescription = "Mute", tint = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Equation Display
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "$num1 + $num2 =",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Answer Box
            Surface(
                color = Color(0xFF2C2C2E),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(2.dp, if (shakeError) Color.Red else Color.White),
                modifier = Modifier
                    .width(200.dp)
                    .height(60.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                     Text(
                        text = answer,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Custom Numpad
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val rows = listOf(
                listOf("7", "8", "9"),
                listOf("4", "5", "6"),
                listOf("1", "2", "3")
            )

            rows.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    row.forEach { digit ->
                        NumPadButton(
                            text = digit,
                            modifier = Modifier.weight(1f).height(70.dp),
                            onClick = { 
                                if (answer.length < 4) answer += digit 
                                shakeError = false
                            }
                        )
                    }
                }
            }
            
            // Bottom Row: Backspace, 0, Check
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Backspace
                Surface(
                    onClick = { if (answer.isNotEmpty()) answer = answer.dropLast(1) },
                    color = Color(0xFF2C2C2E),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(70.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Backspace, contentDescription = "Delete", tint = Color.White)
                    }
                }
                
                // 0
                NumPadButton(
                    text = "0",
                    modifier = Modifier.weight(1f).height(70.dp),
                    onClick = { 
                        if (answer.isNotEmpty() && answer.length < 4) answer += "0" 
                    }
                )
                
                // Check (Submit)
                Surface(
                    onClick = {
                        if (answer.toIntOrNull() == correctAnswer) {
                            onCompleted()
                        } else {
                            shakeError = true
                            answer = ""
                        }
                    },
                    color = Color(0xFFFF3B30), // Red Accent
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(70.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Check, contentDescription = "Submit", tint = Color.White)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // "Exit preview" placeholder
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text("Exit preview", color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
fun NumPadButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color(0xFF2C2C2E),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
