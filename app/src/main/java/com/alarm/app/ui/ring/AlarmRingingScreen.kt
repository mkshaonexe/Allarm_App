package com.alarm.app.ui.ring

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.alarm.app.R
import com.alarm.app.data.alarm.AlarmService
import com.alarm.app.data.alarm.SnoozeReceiver
import com.alarm.app.data.model.ChallengeType
import java.util.Calendar

@Composable
fun AlarmRingingScreen(
    navController: NavController,
    alarmId: String? = null,
    initialChallengeTypeStr: String? = null,
    startChallengeImmediately: Boolean = false
) {
    val context = LocalContext.current
    var isChallengeActive by remember { mutableStateOf(startChallengeImmediately) }
    
    // Parse the challenge type
    val challengeType = try {
        if (initialChallengeTypeStr != null) ChallengeType.valueOf(initialChallengeTypeStr) else ChallengeType.NONE
    } catch (e: Exception) { ChallengeType.NONE }
    
    val currentTime = Calendar.getInstance()
    
    // Function to stop alarm and navigate home (called AFTER challenge success)
    val finishAlarm: () -> Unit = {
        context.stopService(Intent(context, AlarmService::class.java))
        navController.navigate("home") { 
            popUpTo("ringing") { inclusive = true } 
        }
    }
    
    // Function to snooze
    val snoozeAlarm: () -> Unit = {
        if (alarmId != null) {
            val intent = Intent(context, SnoozeReceiver::class.java).apply {
                action = "ACTION_SNOOZE"
                putExtra("ALARM_ID", alarmId)
            }
            context.sendBroadcast(intent)
        } else {
            // For preview/testing without ID -> just stop service
            context.stopService(Intent(context, AlarmService::class.java))
        }
        navController.navigate("home") { 
             popUpTo("ringing") { inclusive = true } 
        }
    }
    
    if (isChallengeActive && challengeType != ChallengeType.NONE) {
        // Show the specific challenge UI
        when (challengeType) {
            ChallengeType.MATH -> com.alarm.app.ui.ring.challenges.MathChallenge(onCompleted = finishAlarm)
            ChallengeType.SHAKE -> com.alarm.app.ui.ring.challenges.ShakeChallenge(onCompleted = finishAlarm)
            ChallengeType.TYPING -> com.alarm.app.ui.ring.challenges.TypingChallenge(onCompleted = finishAlarm)
            ChallengeType.QR -> com.alarm.app.ui.ring.challenges.QRChallenge(onCompleted = finishAlarm)
            else -> finishAlarm() 
        }
    } else {
        // Main Ringing UI (Moon Theme)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground), // Fallback icon
                        contentDescription = "Icon",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Alarmy • Just now", color = Color.Gray, fontSize = 14.sp)
                }
                Text("Alarmy", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.fillMaxWidth().padding(top=4.dp))
                Text("Tap to dismiss ⏰", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.fillMaxWidth())
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Big Time
                Text(
                    text = String.format("%02d:%02d", currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE)),
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Moon Image (Placeholder circle for now, or use a drawable if available)
                Box(
                    modifier = Modifier.size(260.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Moon Graphic (Circle)
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha=0.3f)) 
                            .align(Alignment.Center)
                    ) {
                        // In a real app, use: Image(painter = painterResource(R.drawable.moon), ...)
                         Text("🌑", fontSize = 180.sp, modifier = Modifier.align(Alignment.Center))
                    }
                    
                    // Snooze Pill Button Overlay
                    Surface(
                        onClick = snoozeAlarm,
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFEEEEEE),
                        modifier = Modifier
                            .padding(bottom = 24.dp)
                            .height(50.dp)
                            .clip(RoundedCornerShape(50))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        ) {
                            Text(
                                "3", 
                                color = Color.White, 
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .background(Color.Black, CircleShape)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("Snooze", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                
                // Dismiss Button
                Button(
                    onClick = {
                        // If no challenge, just finish. If challenge, show it.
                        if (challengeType == ChallengeType.NONE) {
                            finishAlarm()
                        } else {
                            isChallengeActive = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3B30)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                ) {
                    Text("Dismiss", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

