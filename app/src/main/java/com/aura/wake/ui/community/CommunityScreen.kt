package com.aura.wake.ui.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aura.wake.data.model.GlobalMessage
import com.aura.wake.data.model.Profile
import com.aura.wake.data.repository.AuthRepository
import com.aura.wake.data.repository.CommunityRepository
import com.aura.wake.ui.components.LoginPrompt
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CommunityScreen(navController: NavController) {
    val authRepository = remember { AuthRepository() }
    val communityRepository = remember { CommunityRepository() }
    val sessionStatus by authRepository.sessionStatus.collectAsState(initial = SessionStatus.NotAuthenticated(false))
    val scope = rememberCoroutineScope()
    
    var messages by remember { mutableStateOf<List<Pair<GlobalMessage, Profile?>>>(emptyList()) }
    var newMessageText by remember { mutableStateOf("") }

    // Simple polling for now
    LaunchedEffect(sessionStatus) {
        if (sessionStatus is SessionStatus.Authenticated) {
            while(true) {
                messages = communityRepository.getMessages()
                delay(5000) // Poll every 5s
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black)
    ) {
        when (sessionStatus) {
            is SessionStatus.Authenticated -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Global Community",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Chat List
                    LazyColumn(
                        modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                        reverseLayout = true // Show newest at bottom if we were appending, but our query is DESC so newest is top. Let's keep DESC and norm list.
                    ) {
                         // Actually our query is DESC (newest first). 
                         // So top of list is newest.
                        items(messages) { (msg, profile) ->
                            MessageItem(msg, profile, authRepository.currentUser?.id == msg.userId)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    // Input Area
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E1E1E))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = newMessageText,
                            onValueChange = { newMessageText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Say something...") },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        IconButton(onClick = {
                            if (newMessageText.isNotBlank()) {
                                scope.launch {
                                    val text = newMessageText
                                    newMessageText = "" // Clear immediately
                                    communityRepository.sendMessage(text)
                                    messages = communityRepository.getMessages() // Refresh immediately
                                }
                            }
                        }) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color(0xFFFF9800))
                        }
                    }
                }
            }
            else -> {
                LoginPrompt(onLoginSuccess = {})
            }
        }
    }
}

@Composable
fun MessageItem(message: GlobalMessage, profile: Profile?, isMe: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        if (!isMe) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color.Gray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                 Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
        ) {
            if (!isMe) {
                Text(
                    text = profile?.username ?: "Unknown",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
            
            Box(
                modifier = Modifier
                    .background(
                        color = if (isMe) Color(0xFFFF9800) else Color(0xFF333333),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = message.content,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
        
        if (isMe) {
             Spacer(modifier = Modifier.width(8.dp))
             // No avatar for me on right side for now, just to keep it clean
        }
    }
}
