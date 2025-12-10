package com.alarm.app.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun OnboardingSetupScreen(
    viewModel: OnboardingViewModel,
    onComplete: () -> Unit
) {
    LaunchedEffect(Unit) {
        // Simulate setup delay
        delay(2000)
        // Complete onboarding
        viewModel.completeOnboarding {
             onComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F121C)) // Dark blueish/black
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        // verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(100.dp))
        
        // Character/Logo with spotlight effect (simplified)
         Box(
            modifier = Modifier
                .size(150.dp)
                .background(Color(0xFFFF3B30), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
             // Placeholder for character
             Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(60.dp))
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        LinearProgressIndicator(
            modifier = Modifier.height(4.dp),
            color = Color(0xFFFF3B30),
            trackColor = Color.Gray.copy(alpha=0.3f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Trusted by 100M+",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your alarm is almost ready",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
         Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Finding the right mission 53%",
            color = Color.Gray,
            fontSize = 14.sp
        )
        
        Spacer(modifier = Modifier.height(48.dp))
    }
}
